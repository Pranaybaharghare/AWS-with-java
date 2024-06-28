package com.example.ecs;

import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.CreateRepositoryRequest;
import software.amazon.awssdk.services.ecr.model.CreateRepositoryResponse;

public class CreateECRRepository {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Region region = Region.AP_SOUTH_1;

		System.out.println("enter a repository name");
		String repositoryName = sc.next();

		
		EcrClient ecrClient = EcrClient.builder().region(region).build();

		// Create the ECR repository
		CreateRepositoryResponse response = createRepository(ecrClient, repositoryName);

		// Print the repository URI
		System.out.println("ECR Repository URI: " + response.repository().repositoryUri());
		System.out.println();
		System.out.println("Retrieve an authentication token and authenticate your Docker client to your registry.\r\n"
				+ "Use the AWS CLI:\r\n"
				+ "\r\n"
				+ "=> aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 133221723595.dkr.ecr.us-east-2.amazonaws.com\r\n"
				+ "\r\n"
				+ "Note: If you receive an error using the AWS CLI, make sure that you have the latest version of the AWS CLI and Docker installed.\r\n"
				+ "\r\n"
				+ "Build your Docker image using the following command. For information on building a Docker file from scratch see the instructions here . You can skip this step if your image is already built:\r\n"
				+ "\r\n"
				+ "=> docker build -t "+repositoryName+" .\r\n"
				+ "\r\n"
				+ "After the build completes, tag your image so you can push the image to this repository:\r\n"
				+ "\r\n"
				+ "=> docker tag "+repositoryName+":latest 133221723595.dkr.ecr.us-east-2.amazonaws.com/"+repositoryName+":latest\r\n"
				+ "\r\n"
				+ "Run the following command to push this image to your newly created AWS repository:\r\n"
				+ "\r\n"
				+ "=> docker push 133221723595.dkr.ecr.us-east-2.amazonaws.com/"+repositoryName+":latest");
	}

	private static CreateRepositoryResponse createRepository(EcrClient ecrClient, String repositoryName) {
		CreateRepositoryRequest createRepositoryRequest = CreateRepositoryRequest.builder()
				.repositoryName(repositoryName).build();

		return ecrClient.createRepository(createRepositoryRequest);
	}
}
