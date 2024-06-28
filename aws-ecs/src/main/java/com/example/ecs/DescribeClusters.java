//snippet-sourcedescription:[DescribeClusters.java demonstrates how to describe a cluster for the Amazon Elastic Container Service (Amazon ECS) service.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Elastic Container Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ecs;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.DescribeClustersRequest;
import software.amazon.awssdk.services.ecs.model.DescribeClustersResponse;
import software.amazon.awssdk.services.ecs.model.Cluster;
import software.amazon.awssdk.services.ecs.model.EcsException;
import java.util.List;
import java.util.Scanner;

public class DescribeClusters {
	public static void main(String[] args) {
		System.out.println("enter a cluster name");
		Scanner sc =new Scanner(System.in);
		String clusterArn = sc.next();
		Region region = Region.AP_SOUTH_1;
		EcsClient ecsClient = EcsClient.builder().region(region).build();

		descCluster(ecsClient, clusterArn);
	}

	public static void descCluster(EcsClient ecsClient, String clusterArn) {
		try {
			DescribeClustersRequest clustersRequest = DescribeClustersRequest.builder().clusters(clusterArn).build();

			DescribeClustersResponse response = ecsClient.describeClusters(clustersRequest);
			List<Cluster> clusters = response.clusters();
			for (Cluster cluster : clusters) {
				System.out.println("The cluster name is " + cluster.clusterName());
			}

		} catch (EcsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}
}
