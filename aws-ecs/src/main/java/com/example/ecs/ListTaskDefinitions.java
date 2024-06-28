package com.example.ecs;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.DescribeTasksRequest;
import software.amazon.awssdk.services.ecs.model.DescribeTasksResponse;
import software.amazon.awssdk.services.ecs.model.EcsException;
import software.amazon.awssdk.services.ecs.model.Task;
import java.util.List;
import java.util.Scanner;

public class ListTaskDefinitions {
	public static void main(String[] args) {
		System.out.println("enter a cluster arn");
		Scanner sc = new Scanner(System.in);
		String clusterArn = sc.next();
		System.out.println("enter a task id");
		String taskId = sc.next();
		Region region = Region.AP_SOUTH_1;
		EcsClient ecsClient = EcsClient.builder().region(region).build();

		getAllTasks(ecsClient, clusterArn, taskId);
		ecsClient.close();
	}

	public static void getAllTasks(EcsClient ecsClient, String clusterArn, String taskId) {
		try {
			DescribeTasksRequest tasksRequest = DescribeTasksRequest.builder().cluster(clusterArn).tasks(taskId)
					.build();

			DescribeTasksResponse response = ecsClient.describeTasks(tasksRequest);
			List<Task> tasks = response.tasks();
			for (Task task : tasks) {
				System.out.println("The task ARN is " + task.taskDefinitionArn());
			}

		} catch (EcsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}
}
