package aws.example.sqs;

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

}
