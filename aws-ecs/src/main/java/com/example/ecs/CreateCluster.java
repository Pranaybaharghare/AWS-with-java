package com.example.ecs;

import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.ExecuteCommandConfiguration;
import software.amazon.awssdk.services.ecs.model.ExecuteCommandLogging;
import software.amazon.awssdk.services.ecs.model.ClusterConfiguration;
import software.amazon.awssdk.services.ecs.model.CreateClusterResponse;
import software.amazon.awssdk.services.ecs.model.EcsException;
import software.amazon.awssdk.services.ecs.model.CreateClusterRequest;

public class CreateCluster {
	public static void main(String[] args) {

		System.out.println("enter a cluster name");
		Scanner sc = new Scanner(System.in);
		String clusterName = sc.next();
		Region region = Region.AP_SOUTH_1;
		EcsClient ecsClient = EcsClient.builder().region(region).build();

		String clusterArn = createGivenCluster(ecsClient, clusterName);
		System.out.println("The cluster ARN is " + clusterArn);
		ecsClient.close();
	}

	public static String createGivenCluster(EcsClient ecsClient, String clusterName) {
		try {
			ExecuteCommandConfiguration commandConfiguration = ExecuteCommandConfiguration.builder()
					.logging(ExecuteCommandLogging.DEFAULT).build();

			ClusterConfiguration clusterConfiguration = ClusterConfiguration.builder()
					.executeCommandConfiguration(commandConfiguration).build();

			CreateClusterRequest clusterRequest = CreateClusterRequest.builder().clusterName(clusterName)
					.configuration(clusterConfiguration).build();

			CreateClusterResponse response = ecsClient.createCluster(clusterRequest);
			return response.cluster().clusterArn();

		} catch (EcsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		return "";
	}
}
