package com.example.ecs;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.ListClustersResponse;
import software.amazon.awssdk.services.ecs.model.EcsException;
import java.util.List;

public class ListClusters {
	public static void main(String[] args) {
		Region region = Region.AP_SOUTH_1;
		EcsClient ecsClient = EcsClient.builder().region(region).build();

		listAllClusters(ecsClient);
		ecsClient.close();
	}

	public static void listAllClusters(EcsClient ecsClient) {
		try {
			ListClustersResponse response = ecsClient.listClusters();
			List<String> clusters = response.clusterArns();
			for (String cluster : clusters) {
				System.out.println("The cluster arn is " + cluster);
			}

		} catch (EcsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}
}
