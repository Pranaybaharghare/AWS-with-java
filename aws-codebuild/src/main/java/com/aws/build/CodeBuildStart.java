package com.aws.build;

import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codebuild.CodeBuildClient;
import software.amazon.awssdk.services.codebuild.model.Build;
import software.amazon.awssdk.services.codebuild.model.CodeBuildException;
import software.amazon.awssdk.services.codebuild.model.StartBuildRequest;
import software.amazon.awssdk.services.codebuild.model.StartBuildResponse;

public class CodeBuildStart {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("enter a project name");
		String projectName = sc.next();

		CodeBuildClient codeBuildClient = CodeBuildClient.builder().region(Region.AP_SOUTH_1).build();

		try {
			// Start a build
			StartBuildResponse startBuildResponse = codeBuildClient
					.startBuild(StartBuildRequest.builder().projectName(projectName).sourceVersion("master").build());

			// Print information about the started build
			Build build = startBuildResponse.build();
			System.out.println("Build ARN: " + build.arn());
			System.out.println("Build ID: " + build.id());
			System.out.println("Build Status: " + build.buildStatus());
		} catch (CodeBuildException e) {
			System.err.println("Error starting build: " + e.awsErrorDetails().errorMessage());
		} finally {
			codeBuildClient.close();
		}
	}
}
