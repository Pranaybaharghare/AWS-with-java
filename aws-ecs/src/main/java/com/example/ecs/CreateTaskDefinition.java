package com.example.ecs;

import java.util.Scanner;

import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.Compatibility;
import software.amazon.awssdk.services.ecs.model.ContainerDefinition;
import software.amazon.awssdk.services.ecs.model.EcsException;
import software.amazon.awssdk.services.ecs.model.NetworkMode;
import software.amazon.awssdk.services.ecs.model.PortMapping;
import software.amazon.awssdk.services.ecs.model.RegisterTaskDefinitionRequest;
import software.amazon.awssdk.services.ecs.model.RegisterTaskDefinitionResponse;
import software.amazon.awssdk.services.ecs.model.TransportProtocol;

public class CreateTaskDefinition {

	public static void main(String[] args) {
		System.out.println("enter task definition name");
		Scanner sc = new Scanner(System.in);
		String familyName = sc.next();
		System.out.println("enter a container name");
		String containerName = sc.next();
		System.out.println("enter a image name");
		String imageName = sc.next();

		EcsClient ecsClient = EcsClient.builder().build();

		try {
			// Create a task definition
			RegisterTaskDefinitionResponse registerTaskDefinitionResponse = ecsClient
					.registerTaskDefinition(
							RegisterTaskDefinitionRequest.builder().family(familyName).networkMode(NetworkMode.AWSVPC)
									.cpu("256") // Specify CPU units for the task
									.memory("512") // Specify memory for the task
									.requiresCompatibilities(Compatibility.FARGATE)
									.executionRoleArn("arn:aws:iam::210858943634:role/ecsTaskExecutionRole") // Replace with your IAM role ARN
									.containerDefinitions(ContainerDefinition.builder().name(containerName)
											.image(imageName).memory(128) // Specify memory for the container
											.cpu(64) // Specify CPU units for the container
											.essential(true)
											.portMappings(PortMapping.builder().containerPort(8080).hostPort(8080) // You can set a different host port if needed
													.protocol(TransportProtocol.TCP) // TCP or UDP
													.build())
											.build())
									.build());

			// Print the ARN of the registered task definition
			System.out.println(
					"Task Definition ARN: " + registerTaskDefinitionResponse.taskDefinition().taskDefinitionArn());
		} catch (EcsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		} finally {
			ecsClient.close();
		}
	}

}
