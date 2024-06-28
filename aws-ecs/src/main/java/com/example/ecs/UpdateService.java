
package com.example.ecs;

import java.util.Scanner;

// snippet-start:[ecs.java2.update_service.main]
// snippet-start:[ecs.java2.update_service.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.EcsException;
import software.amazon.awssdk.services.ecs.model.UpdateServiceRequest;

public class UpdateService {

	public static void main(String[] args) {

		System.out.println("enter a cluster name");
		Scanner sc = new Scanner(System.in);
		String clusterName = sc.next();
		System.out.println("enter a service arn");
		String serviceArn = sc.next();
		Region region = Region.AP_SOUTH_1;
		EcsClient ecsClient = EcsClient.builder().region(region).build();

		updateSpecificService(ecsClient, clusterName, serviceArn);
		ecsClient.close();
	}

	public static void updateSpecificService(EcsClient ecsClient, String clusterName, String serviceArn) {
		try {
			UpdateServiceRequest serviceRequest = UpdateServiceRequest.builder().cluster(clusterName)
					.service(serviceArn).desiredCount(0).build();

			ecsClient.updateService(serviceRequest);
			System.out.println("The service was modified");

		} catch (EcsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}
}
