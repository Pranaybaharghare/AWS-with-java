package com.aws.build;

import java.io.IOException;
import java.util.Scanner;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codebuild.CodeBuildClient;
import software.amazon.awssdk.services.codebuild.model.ArtifactsType;
import software.amazon.awssdk.services.codebuild.model.CloudWatchLogsConfig;
import software.amazon.awssdk.services.codebuild.model.CodeBuildException;
import software.amazon.awssdk.services.codebuild.model.CreateProjectRequest;
import software.amazon.awssdk.services.codebuild.model.CreateProjectResponse;
import software.amazon.awssdk.services.codebuild.model.EnvironmentType;
import software.amazon.awssdk.services.codebuild.model.LogsConfig;
import software.amazon.awssdk.services.codebuild.model.LogsConfigStatusType;
import software.amazon.awssdk.services.codebuild.model.ProjectArtifacts;
import software.amazon.awssdk.services.codebuild.model.ProjectEnvironment;
import software.amazon.awssdk.services.codebuild.model.ProjectSource;
import software.amazon.awssdk.services.codebuild.model.SourceType;
import software.amazon.awssdk.services.codebuild.model.Tag;

public class CreateCodeBuildProject {
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.println("enter a project name");
		 String projectName = sc.next();
		 System.out.println("enter a repository url");
	        String sourceRepoUrl = sc.next();
	        String sourceType = SourceType.CODECOMMIT.toString(); // Replace with your source type (e.g., GITHUB, CODECOMMIT)
//	        String buildspecPath = "DemoApp/buildspec.yml";

	        CodeBuildClient codeBuildClient = CodeBuildClient.builder()
	                .region(Region.AP_SOUTH_1)  // Replace with your desired AWS region
	                .credentialsProvider(DefaultCredentialsProvider.create())
	                .build();

	        try {
//	        	String buildspecContents = new String(Files.readAllBytes(Paths.get(buildspecPath)));
	            // Create CodeBuild project request
	        	LogsConfig logsConfig = LogsConfig.builder()
	        	        .cloudWatchLogs(CloudWatchLogsConfig.builder()
	        	                .status(LogsConfigStatusType.ENABLED) // Enable CloudWatch Logs
//	        	                .groupName("/aws/codebuild/YourLogGroupName") // Specify your CloudWatch Logs group name
//	        	                .streamName("/aws/codebuild/YourLogStreamName") // Specify your CloudWatch Logs stream name
	        	                .build())
	        	        .build();

	            CreateProjectResponse createProjectResponse = codeBuildClient.createProject(
	                    CreateProjectRequest.builder()
	                            .name(projectName)
	                            .source(
	                                    ProjectSource.builder()
	                                            .type(sourceType)
	                                            .location(sourceRepoUrl)
	                                            .buildspec("version: 0.2\r\n"
	                                            		+ "\r\n"
	                                            		+ "phases:\r\n"
	                                            		+ "  install:\r\n"
	                                            		+ "    runtime-versions:\r\n"
	                                            		+ "      java: corretto17\r\n"
	                                            		+ "  build:\r\n"
	                                            		+ "    commands:\r\n"
	                                            		+ "      - mvn clean install\r\n"
	                                            		+ "  post_build:\r\n"
	                                            		+ "    commands:\r\n"
	                                            		+ "      - echo Build completed\r\n"
	                                            		+ "artifacts:\r\n"
	                                            		+ "  files:\r\n"
	                                            		+ "    - target/*.jar\r\n"
	                                            		+ "    - scripts/*.sh\r\n"
	                                            		+ "    - appspec.yml\r\n"
	                                            		+ "  discard-paths: yes")
	                                            .build()
	                            )
	                            .artifacts(
	                                    ProjectArtifacts.builder()
	                                            .type(ArtifactsType.NO_ARTIFACTS)
	                                            .build()
	                            )
	                            .environment(
	                                    ProjectEnvironment.builder()
	                                    .type(EnvironmentType.LINUX_CONTAINER)
	                                            .computeType("BUILD_GENERAL1_SMALL")
	                                            .image("aws/codebuild/amazonlinux2-x86_64-standard:5.0")
	                                            .build()
	                            )
	                            .sourceVersion("master") 
//	                            .encryptionKey("arn:aws:kms:region:account-id:key/key-id") // Optional: Replace with your KMS key ARN
	                            .badgeEnabled(true)
	                            .serviceRole("arn:aws:iam::210858943634:role/service-role/codebuild-DemoBuild-service-role")
	                            .logsConfig(logsConfig)
	                            .description("Your CodeBuild project description")
	                            .tags(Tag.builder().key("key").value("value").build()) // Optional: Replace with your desired tags
	                            .timeoutInMinutes(10)
	                            .build()
	            );

	            // Print information about the created project
	            System.out.println("CodeBuild project ARN: " + createProjectResponse.project().arn());
	        } catch (CodeBuildException e) {
	            System.err.println("Error creating CodeBuild project: " + e.awsErrorDetails().errorMessage());
	        } finally {
	            codeBuildClient.close();
	        }
	}
}
