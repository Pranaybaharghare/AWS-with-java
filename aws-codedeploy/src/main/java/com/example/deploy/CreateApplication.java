package com.example.deploy;

import java.util.Scanner;

// snippet-start:[codedeploy.java2.create_app.main]
// snippet-start:[codedeploy.java2.create_app.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.ComputePlatform;
import software.amazon.awssdk.services.codedeploy.model.CreateApplicationRequest;
import software.amazon.awssdk.services.codedeploy.model.CreateApplicationResponse;


public class CreateApplication {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("enter a application name");
		String appName = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodeDeployClient deployClient = CodeDeployClient.builder().region(region).build();

		createApp(deployClient, appName);
		deployClient.close();
	}

	public static void createApp(CodeDeployClient deployClient, String appName) {
		try {
			CreateApplicationRequest applicationRequest = CreateApplicationRequest.builder().applicationName(appName)
					.computePlatform(ComputePlatform.SERVER).build();

			CreateApplicationResponse applicationResponse = deployClient.createApplication(applicationRequest);
			String appId = applicationResponse.applicationId();
			System.out.println("The application ID is " + appId);

		} catch (CodeDeployException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}
}
