package aws.example.sqs;

import java.util.Scanner;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

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

}
