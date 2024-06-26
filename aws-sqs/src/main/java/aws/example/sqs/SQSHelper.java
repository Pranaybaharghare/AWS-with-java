package aws.example.sqs;

import java.util.List;
import java.util.Scanner;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;

public class SQSHelper {
	public static void listAllQueues() {
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

		// List your queues
		ListQueuesResult lq_result = sqs.listQueues();
		System.out.println("Your SQS Queue:");
		for (String url : lq_result.getQueueUrls()) {
			String s1 = url.substring(url.lastIndexOf('/') + 1);
			System.out.println(s1);
			System.out.println(url);

		}
	}
	
	public static void createNewQueue(Scanner sc) {
		int option;
		System.out.println("0 = Standard");
		System.out.println("1 = FIFO");
		option = sc.nextInt();
		System.out.println("enter a queue name");
		String QUEUE_NAME = sc.next();
		switch (option) {
		case 0:
			AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

			// Creating a Queue
			CreateQueueRequest create_request = new CreateQueueRequest(QUEUE_NAME)
					.addAttributesEntry("DelaySeconds", "0").addAttributesEntry("MessageRetentionPeriod", "86400");

			try {
				sqs.createQueue(create_request);
				String queue_url = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
				System.out.println("Queue created successfully");
				System.out.println("QUEUE_URL: " + queue_url);
			} catch (AmazonSQSException e) {
				if (!e.getErrorCode().equals("QueueAlreadyExists")) {
					throw e;
				}
			}
			break;
		case 1:
			String fifoQueueName = QUEUE_NAME.concat(".fifo");
			System.out.println(fifoQueueName);
			AmazonSQS sqsfifo = AmazonSQSClientBuilder.defaultClient();

			// Creating a Queue
			CreateQueueRequest create_request_fifo = new CreateQueueRequest(fifoQueueName)
					.addAttributesEntry("DelaySeconds", "0").addAttributesEntry("FifoQueue", "true")
					.addAttributesEntry("MessageRetentionPeriod", "86400");

			try {
				sqsfifo.createQueue(create_request_fifo);
				String queue_url = sqsfifo.getQueueUrl(fifoQueueName).getQueueUrl();
				System.out.println("Queue created successfully");
				System.out.println("QUEUE_URL: " + queue_url);
			} catch (AmazonSQSException e) {
				if (!e.getErrorCode().equals("QueueAlreadyExists")) {
					throw e;
				}
			}
		default:
			break;
		}

	}

	public static void deleteQueue(Scanner sc) {
		System.out.println("enter a queue name");
		String queueName = sc.next();
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		String queue_url = sqs.getQueueUrl(queueName).getQueueUrl();
		sqs.deleteQueue(queue_url);
		System.out.println("successfully deleted queue");
	}
	
	public static void sendMessage(Scanner sc) {
		System.out.println("enter queue name");
		String QUEUE_NAME = sc.next();
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

		String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
		boolean flag = queueUrl.contains(".fifo");
		System.out.println("enter a message");
		Scanner scanner = new Scanner(System.in);
		String message = scanner.nextLine();
		if (flag == true) {
			System.out.println("enter message groupId");
			String messageGroupId = sc.next();
			System.out.println("enter message duplicateId(enter digit in sequeunce)");
			String messageDuplicateId = sc.next();
			SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl)
					.withMessageBody(message).withMessageGroupId(messageGroupId)
					.withMessageDeduplicationId(messageDuplicateId);
			sqs.sendMessage(send_msg_request);
			System.out.println("message sent successfully");
		} else {
			SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl)
					.withMessageBody(message);
			sqs.sendMessage(send_msg_request);
			System.out.println("message sent successfully");
		}

	}
	
	public static void receiveMessage(Scanner sc) {
		System.out.println("enter queue name");
		String QUEUE_NAME = sc.next();
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

		String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();

		// Receive messages from the queue
		ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest().withQueueUrl(queueUrl)
				.withMaxNumberOfMessages(5) // Adjust as needed
				.withWaitTimeSeconds(20); // Adjust as needed

		List<Message> messages = sqs.receiveMessage(receiveRequest).getMessages();

		// Process the received messages
		for (Message message : messages) {
			System.out.println("Received message: " + message.getBody());

			// TODO: Process the message content as needed
		}

	}
	
	public static void deleteMessageafterConsumption(Scanner sc) {
		System.out.println("enter queue name");
		String QUEUE_NAME = sc.next();
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

		String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();

		// Receive messages from the queue
		ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest().withQueueUrl(queueUrl)
				.withMaxNumberOfMessages(5) // Adjust as needed
				.withWaitTimeSeconds(0); // Adjust as needed

		List<Message> messages = sqs.receiveMessage(receiveRequest).getMessages();

		// Process the received messages
		for (Message message : messages) {
			// Delete the message from the queue once processed
			DeleteMessageRequest deleteRequest = new DeleteMessageRequest().withQueueUrl(queueUrl)
					.withReceiptHandle(message.getReceiptHandle());

			sqs.deleteMessage(deleteRequest);
			System.out.println("message deleted successfully");
		}

	}
	

	public static void createDeadLetterQueue(Scanner sc) {
		System.out.println("enter a source queue name");
		String sourceQueueName = sc.next();
		System.out.println("enter a dead letter queue name");
		String deadLetterQueueName = sc.next();

		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

		// Create dead-letter queue
		try {
			sqs.createQueue(deadLetterQueueName);
		} catch (AmazonSQSException e) {
			if (!e.getErrorCode().equals("QueueAlreadyExists")) {
				throw e;
			}
		}

		// Get dead-letter queue ARN
		String dl_queue_url = sqs.getQueueUrl(deadLetterQueueName).getQueueUrl();

		GetQueueAttributesResult queue_attrs = sqs
				.getQueueAttributes(new GetQueueAttributesRequest(dl_queue_url).withAttributeNames("QueueArn"));

		String dl_queue_arn = queue_attrs.getAttributes().get("QueueArn");

		// Set dead letter queue with redrive policy on source queue.
		String src_queue_url = sqs.getQueueUrl(sourceQueueName).getQueueUrl();

		SetQueueAttributesRequest request = new SetQueueAttributesRequest().withQueueUrl(src_queue_url)
				.addAttributesEntry("RedrivePolicy",
						"{\"maxReceiveCount\":\"5\", \"deadLetterTargetArn\":\"" + dl_queue_arn + "\"}");

		sqs.setQueueAttributes(request);
	}

}
