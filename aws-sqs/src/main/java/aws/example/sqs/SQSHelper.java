package aws.example.sqs;

import java.util.Scanner;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ListQueuesResult;

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
}
