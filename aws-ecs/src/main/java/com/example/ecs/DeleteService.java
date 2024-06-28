
package com.example.ecs;

import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.DeleteServiceRequest;
import software.amazon.awssdk.services.ecs.model.EcsException;

public class DeleteService {
	public static void main(String[] args) {

		Scanner sc =new Scanner(System.in);
		System.out.println("enter a cluster name");
		String clusterName = sc.next();
		System.out.println("enter a service arn");
		String serviceArn = sc.next();
		Region region = Region.AP_SOUTH_1;
		EcsClient ecsClient = EcsClient.builder().region(region).build();

		deleteSpecificService(ecsClient, clusterName, serviceArn);
		ecsClient.close();
	}

	public static void deleteSpecificService(EcsClient ecsClient, String clusterName, String serviceArn) {
		try {
			DeleteServiceRequest serviceRequest = DeleteServiceRequest.builder().cluster(clusterName)
					.service(serviceArn).build();

			ecsClient.deleteService(serviceRequest);
			System.out.println("The Service was successfully deleted");

		} catch (EcsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}
}
