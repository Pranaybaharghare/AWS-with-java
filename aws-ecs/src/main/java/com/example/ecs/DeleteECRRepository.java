package com.example.ecs;

import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.DeleteRepositoryRequest;
import software.amazon.awssdk.services.ecr.model.DeleteRepositoryResponse;

public class DeleteECRRepository {
	public static void main(String[] args) {
		Region region = Region.AP_SOUTH_1;

		Scanner sc =new Scanner(System.in);
		String repositoryName = sc.next();

		// Set up the ECR client
		EcrClient ecrClient = EcrClient.builder().region(region).build();

		// Delete the ECR repository
		DeleteRepositoryResponse response = deleteRepository(ecrClient, repositoryName);

		// Print the repository URI of the deleted repository
		System.out.println("Deleted ECR Repository URI: " + response.repository().repositoryUri());
	}

	private static DeleteRepositoryResponse deleteRepository(EcrClient ecrClient, String repositoryName) {
		DeleteRepositoryRequest deleteRepositoryRequest = DeleteRepositoryRequest.builder()
				.repositoryName(repositoryName).force(true) // Set to true to delete the repository even if it contains
															// images
				.build();

		return ecrClient.deleteRepository(deleteRepositoryRequest);
	}
}
