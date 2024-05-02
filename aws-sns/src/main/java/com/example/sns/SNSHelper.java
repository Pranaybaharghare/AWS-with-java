package com.example.sns;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest;
import software.amazon.awssdk.services.sns.model.DeleteTopicResponse;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsRequest;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
import software.amazon.awssdk.services.sns.model.UnsubscribeRequest;
import software.amazon.awssdk.services.sns.model.UnsubscribeResponse;
import software.amazon.awssdk.services.sns.paginators.ListTopicsIterable;

public class SNSHelper {

	public static void listAllTopics() {
		SnsClient snsClient = SnsClient.builder().region(Region.AF_SOUTH_1).build();

		listSNSTopics(snsClient);
		snsClient.close();
	}

	public static void listSNSTopics(SnsClient snsClient) {
		try {
			ListTopicsIterable listTopics = snsClient.listTopicsPaginator();
			listTopics.stream().flatMap(r -> r.topics().stream())
					.forEach(content -> System.out.println(" Topic ARN: " + content.topicArn()));

		} catch (SnsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}
	}

	public static void createNewTopic(Scanner sc) {
		int option;
		System.out.println("0 = Standard");
		System.out.println("1 = FIFO");
		option = sc.nextInt();
		System.out.println("enter a topic name");
		String topicName = sc.next();
		System.out.println("Creating a topic with name: " + topicName);
		switch (option) {
		case 0:
			SnsClient snsClient = SnsClient.builder().region(Region.AP_SOUTH_1).build();

			String arnVal = createSNSTopic(snsClient, topicName);
			System.out.println("The topic ARN is" + arnVal);
			snsClient.close();
			break;
		case 1:
			String fifoTopicName = topicName.concat(".fifo");
			SnsClient snsClientFifo = SnsClient.builder().region(Region.AP_SOUTH_1).build();

			createFIFO(snsClientFifo, fifoTopicName);
		}
	}

	private static void createFIFO(SnsClient snsClient, String fifoTopicName) {
		// Create a FIFO topic by using the SNS service client.
		Map<String, String> topicAttributes = new HashMap<>();
		topicAttributes.put("FifoTopic", "true");
		topicAttributes.put("ContentBasedDeduplication", "false");

		CreateTopicRequest topicRequest = CreateTopicRequest.builder().name(fifoTopicName).attributes(topicAttributes)
				.build();

		CreateTopicResponse response = snsClient.createTopic(topicRequest);
		String topicArn = response.topicArn();
		System.out.println("The topic ARN is: " + topicArn);
	}

	public static String createSNSTopic(SnsClient snsClient, String topicName) {
		CreateTopicResponse result;
		try {
			CreateTopicRequest request = CreateTopicRequest.builder().name(topicName).build();

			result = snsClient.createTopic(request);
			return result.topicArn();

		} catch (SnsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		return "";
	}

	public static void deleteTopic(Scanner sc) {
		System.out.println("enter a topic ARN");
		String topicArn = sc.next();
		SnsClient snsClient = SnsClient.builder().region(Region.AP_SOUTH_1).build();

		System.out.println("Deleting a topic with name: " + topicArn);
		snsClient.close();
		try {
			DeleteTopicRequest request = DeleteTopicRequest.builder().topicArn(topicArn).build();

			DeleteTopicResponse result = snsClient.deleteTopic(request);
			System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode());

		} catch (SnsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}

	public static void listSubscription() {
		SnsClient snsClient = SnsClient.builder().region(Region.AP_SOUTH_1).build();

		try {
			ListSubscriptionsRequest request = ListSubscriptionsRequest.builder().build();

			ListSubscriptionsResponse result = snsClient.listSubscriptions(request);
			System.out.println(result.subscriptions());

		} catch (SnsException e) {

			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		snsClient.close();
	}

	public static void publishTopicMessagee(Scanner sc) {
		System.out.println("enter a topic ARN");
		String topicArn = sc.next();
		System.out.println("enter a message");
		String message = sc.next();

		SnsClient snsClient = SnsClient.builder().region(Region.AP_SOUTH_1).build();
		try {
			PublishRequest request = PublishRequest.builder().message(message).topicArn(topicArn).build();

			PublishResponse result = snsClient.publish(request);
			System.out
					.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());

		} catch (SnsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		snsClient.close();
	}

	public static void subscribeToMail(Scanner sc) {
		System.out.println("enter a topic ARN");
		String topicArn = sc.next();
		if (topicArn.contains(".fifo")) {
			System.out.println("can't subscribe to email , you can only subscribe to SQS queue");
		} else {
			System.out.println("enter a email to subscribe");
			String email = sc.next();
			System.out.println(email);
			SnsClient snsClient = SnsClient.builder().region(Region.AP_SOUTH_1).build();

			try {
				SubscribeRequest request = SubscribeRequest.builder().protocol("email").endpoint(email)
						.returnSubscriptionArn(true).topicArn(topicArn).build();

				SubscribeResponse result = snsClient.subscribe(request);
				System.out.println("Subscription ARN: " + result.subscriptionArn() + "\n\n Status is "
						+ result.sdkHttpResponse().statusCode());

			} catch (SnsException e) {
				System.err.println(e.awsErrorDetails().errorMessage());
				System.exit(1);
			}
			snsClient.close();
		}
	}

	public static void subscribeToSqs(Scanner sc) {
		System.out.println("enter a topic ARN");
		String topicArn = sc.next();
		System.out.println("enter a queue ARN");
		String queueArn = sc.next();
		SubscribeRequest subscribeRequest = SubscribeRequest.builder().topicArn(topicArn).endpoint(queueArn)
				.protocol("sqs").build();
		final SnsClient snsClient = SnsClient.create();

		// Subscribe to the endpoint by using the SNS service client.
		// Only Amazon SQS queues can receive notifications from an Amazon SNS FIFO
		// topic.
		snsClient.subscribe(subscribeRequest);
		System.out.println("The queue [" + queueArn + "] subscribed to the topic [" + topicArn + "]");
	}

	public static void unsubscribeTopic(Scanner sc) {
		System.out.println("enter a subscription ARN");
		String subscriptionArn = sc.next();
		SnsClient snsClient = SnsClient.builder().region(Region.AP_SOUTH_1).build();
		try {
			UnsubscribeRequest request = UnsubscribeRequest.builder().subscriptionArn(subscriptionArn).build();

			UnsubscribeResponse result = snsClient.unsubscribe(request);
			System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode()
					+ "\n\nSubscription was removed for " + request.subscriptionArn());

		} catch (SnsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		snsClient.close();
	}
}
