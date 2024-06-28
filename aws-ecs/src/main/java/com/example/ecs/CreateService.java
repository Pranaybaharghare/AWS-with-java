	
package com.example.ecs;

import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.AwsVpcConfiguration;
import software.amazon.awssdk.services.ecs.model.NetworkConfiguration;
import software.amazon.awssdk.services.ecs.model.CreateServiceRequest;
import software.amazon.awssdk.services.ecs.model.LaunchType;
import software.amazon.awssdk.services.ecs.model.CreateServiceResponse;
import software.amazon.awssdk.services.ecs.model.EcsException;

public class CreateService {
	public static void main(String[] args) {
		System.out.println("enter a cluster name");
		Scanner sc = new Scanner(System.in);
		String clusterName = sc.next();
		System.out.println("enter a service name");
		String serviceName = sc.next();
		System.out.println("enter a security group");
		String securityGroups = sc.next();
		System.out.println("enter a subnet");
		String subnets = sc.next();
		System.out.println("enter a task definition");
		String taskDefinition = sc.next();
		Region region = Region.AP_SOUTH_1;
		EcsClient ecsClient = EcsClient.builder().region(region).build();

		String serviceArn = createNewService(ecsClient, clusterName, serviceName, securityGroups, subnets,
				taskDefinition);
		System.out.println("The ARN of the service is " + serviceArn);
		ecsClient.close();
	}

	public static String createNewService(EcsClient ecsClient, String clusterName, String serviceName,
			String securityGroups, String subnets, String taskDefinition) {

		try {
			AwsVpcConfiguration vpcConfiguration = AwsVpcConfiguration.builder().securityGroups(securityGroups)
					.subnets(subnets).build();

			NetworkConfiguration configuration = NetworkConfiguration.builder().awsvpcConfiguration(vpcConfiguration)
					.build();

			CreateServiceRequest serviceRequest = CreateServiceRequest.builder().cluster(clusterName)
					.networkConfiguration(configuration).desiredCount(1).launchType(LaunchType.FARGATE)
					.serviceName(serviceName).taskDefinition(taskDefinition).build();

			CreateServiceResponse response = ecsClient.createService(serviceRequest);
			return response.service().serviceArn();

		} catch (EcsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		return "";
	}
}
