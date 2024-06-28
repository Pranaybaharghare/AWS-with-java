package com.example.pipeline;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codepipeline.CodePipelineClient;
import software.amazon.awssdk.services.codepipeline.model.ActionDeclaration;
import software.amazon.awssdk.services.codepipeline.model.ActionTypeId;
import software.amazon.awssdk.services.codepipeline.model.ArtifactStore;
import software.amazon.awssdk.services.codepipeline.model.ArtifactStoreType;
import software.amazon.awssdk.services.codepipeline.model.CodePipelineException;
import software.amazon.awssdk.services.codepipeline.model.CreatePipelineRequest;
import software.amazon.awssdk.services.codepipeline.model.CreatePipelineResponse;
import software.amazon.awssdk.services.codepipeline.model.DeletePipelineRequest;
import software.amazon.awssdk.services.codepipeline.model.GetPipelineRequest;
import software.amazon.awssdk.services.codepipeline.model.GetPipelineResponse;
import software.amazon.awssdk.services.codepipeline.model.InputArtifact;
import software.amazon.awssdk.services.codepipeline.model.ListPipelineExecutionsRequest;
import software.amazon.awssdk.services.codepipeline.model.ListPipelineExecutionsResponse;
import software.amazon.awssdk.services.codepipeline.model.ListPipelinesResponse;
import software.amazon.awssdk.services.codepipeline.model.OutputArtifact;
import software.amazon.awssdk.services.codepipeline.model.PipelineDeclaration;
import software.amazon.awssdk.services.codepipeline.model.PipelineExecutionSummary;
import software.amazon.awssdk.services.codepipeline.model.PipelineSummary;
import software.amazon.awssdk.services.codepipeline.model.StageDeclaration;
import software.amazon.awssdk.services.codepipeline.model.StartPipelineExecutionRequest;
import software.amazon.awssdk.services.codepipeline.model.StartPipelineExecutionResponse;

public class CodePipelineHelper {

	public static void createPipelineWithCodeDeploy(Scanner sc) {
		// Replace these values with your own
		Region region = Region.AP_SOUTH_1;
		System.out.println("enter your pipeline name");
		String pipelineName = sc.next();
		String sourceOutputArtifact = "SourceOutput";
		String buildOutputArtifact = "BuildOutput";
		System.out.println("enter a repository name");
		String codeCommitRepoName = sc.next();
		System.out.println("enter codebuild project name");
		String codeBuildProjectName = sc.next();
		System.out.println("enter codedeploy application name");
		String codedeployAppName = sc.next();
		System.out.println("enter codedeploy environment name");
		String coddeDeployEnvName = sc.next();

		CodePipelineClient codePipelineClient = CodePipelineClient.builder().region(region).build();

		ArtifactStore artifactStore = ArtifactStore.builder().type(ArtifactStoreType.S3).location("artifactpipeline") // Replace
																														// with
																														// your
																														// S3
																														// bucket
																														// name
				.build();
		
		//for codecommit
		 Map<String, String> configurationForCodeCommit = new HashMap<>();
		 configurationForCodeCommit.put("RepositoryName", codeCommitRepoName);
		 configurationForCodeCommit.put("BranchName", "master");
		 Map<String, String> configurationMapForCodeCommit = Collections.unmodifiableMap(configurationForCodeCommit);
		 
		 //for codebuild
		 Map<String, String> configurationForCodeBuild = new HashMap<>();
		 configurationForCodeBuild.put("ProjectName", codeBuildProjectName);
		 Map<String, String> configurationMapForCodeBuild = Collections.unmodifiableMap(configurationForCodeBuild);
		 
		 //for codedeploy
		 Map<String, String> configurationForCodeDeploy = new HashMap<>();
		 configurationForCodeDeploy.put("ApplicationName", codedeployAppName);
		 configurationForCodeDeploy.put("DeploymentGroupName", coddeDeployEnvName);
		 Map<String, String> configurationMapForCodeDeploy = Collections.unmodifiableMap(configurationForCodeDeploy);



		// Create a pipeline
		CreatePipelineResponse pipelineResponse = codePipelineClient.createPipeline(CreatePipelineRequest.builder()
				.pipeline(PipelineDeclaration.builder().name(pipelineName).pipelineType("V1").roleArn(
						"arn:aws:iam::210858943634:role/service-role/AWSCodePipelineServiceRole-ap-south-1-DemoPipeline")
						.artifactStore(artifactStore).stages(
								StageDeclaration.builder().name("Source").actions(ActionDeclaration.builder()
										.name("SourceAction")
										.actionTypeId(ActionTypeId.builder().category("Source").owner("AWS")
												.provider("CodeCommit").version("1").build())
										.configuration(configurationMapForCodeCommit)
										.outputArtifacts(OutputArtifact.builder().name(sourceOutputArtifact).build())
										.runOrder(1).build()).build(),
								StageDeclaration.builder().name("Build").actions(ActionDeclaration.builder()
										.name("BuildAction")
										.actionTypeId(ActionTypeId.builder().category("Build").owner("AWS")
												.provider("CodeBuild").version("1").build())
										.configuration(configurationMapForCodeBuild)
										.inputArtifacts(InputArtifact.builder().name(sourceOutputArtifact).build())
										.outputArtifacts(OutputArtifact.builder().name(buildOutputArtifact).build())
										.runOrder(1).build()).build(),
								StageDeclaration.builder().name("Deploy").actions(ActionDeclaration.builder()
										.name("DeployAction")
										.actionTypeId(ActionTypeId.builder().category("Deploy").owner("AWS")
												.provider("CodeDeploy").version("1").build())
										.configuration(configurationMapForCodeDeploy)
										.inputArtifacts(InputArtifact.builder().name(buildOutputArtifact).build())
										.runOrder(1).build()).build())
						.build())
				.build());

		System.out.println("Pipeline created successfully. Pipeline ARN: " + pipelineResponse.pipeline().roleArn());
	}

	public static void createPipelineWithBeanstalk(Scanner sc) {
		// Replace these values with your own
		Region region = Region.AP_SOUTH_1;
		System.out.println("enter your pipeline name");
		String pipelineName = sc.next();
		String sourceOutputArtifact = "SourceOutput";
		String buildOutputArtifact = "BuildOutput";
		System.out.println("enter a repository name");
		String codeCommitRepoName = sc.next();
		System.out.println("enter codebuild project name");
		String codeBuildProjectName = sc.next();
		System.out.println("enter elastic beanstalk application name");
		String beanstalkAppName = sc.next();
		System.out.println("enter elastic beanstalk environment name");
		String beanstalkEnvName = sc.next();

		CodePipelineClient codePipelineClient = CodePipelineClient.builder().region(region).build();

		ArtifactStore artifactStore = ArtifactStore.builder().type(ArtifactStoreType.S3).location("artifactpipeline") // Replace
																														// with
																														// your
																														// S3
																														// bucket
																														// name
				.build();
		
		//for codecommit
		 Map<String, String> configurationForCodeCommit = new HashMap<>();
		 configurationForCodeCommit.put("RepositoryName", codeCommitRepoName);
		 configurationForCodeCommit.put("BranchName", "master");
		 Map<String, String> configurationMapForCodeCommit = Collections.unmodifiableMap(configurationForCodeCommit);
		 
		 //for codebuild
		 Map<String, String> configurationForCodeBuild = new HashMap<>();
		 configurationForCodeBuild.put("ProjectName", codeBuildProjectName);
		 Map<String, String> configurationMapForCodeBuild = Collections.unmodifiableMap(configurationForCodeBuild);
		 
		 //for codedeploy
		 Map<String, String> configurationForBeanstalk = new HashMap<>();
		 configurationForBeanstalk.put("ApplicationName", beanstalkAppName);
		 configurationForBeanstalk.put("EnvironmentName", beanstalkEnvName);
		 Map<String, String> configurationMapForBeanstalk = Collections.unmodifiableMap(configurationForBeanstalk);



		// Create a pipeline
		CreatePipelineResponse pipelineResponse = codePipelineClient.createPipeline(CreatePipelineRequest.builder()
				.pipeline(PipelineDeclaration.builder().name(pipelineName).pipelineType("V1").roleArn(
						"arn:aws:iam::210858943634:role/service-role/AWSCodePipelineServiceRole-ap-south-1-githubline")
						.artifactStore(artifactStore).stages(
								StageDeclaration.builder().name("Source").actions(ActionDeclaration.builder()
										.name("SourceAction")
										.actionTypeId(ActionTypeId.builder().category("Source").owner("AWS")
												.provider("CodeCommit").version("1").build())
										.configuration(configurationMapForCodeCommit)
										.outputArtifacts(OutputArtifact.builder().name(sourceOutputArtifact).build())
										.runOrder(1).build()).build(),
								StageDeclaration.builder().name("Build").actions(ActionDeclaration.builder()
										.name("BuildAction")
										.actionTypeId(ActionTypeId.builder().category("Build").owner("AWS")
												.provider("CodeBuild").version("1").build())
										.configuration(configurationMapForCodeBuild)
										.inputArtifacts(InputArtifact.builder().name(sourceOutputArtifact).build())
										.outputArtifacts(OutputArtifact.builder().name(buildOutputArtifact).build())
										.runOrder(1).build()).build(),
								StageDeclaration.builder().name("Deploy").actions(ActionDeclaration.builder()
										.name("DeployAction")
										.actionTypeId(ActionTypeId.builder().category("Deploy").owner("AWS")
												.provider("ElasticBeanstalk").version("1").build())
										.configuration(configurationMapForBeanstalk)
										.inputArtifacts(InputArtifact.builder().name(buildOutputArtifact).build())
										.runOrder(1).build()).build())
						.build())
				.build());

		System.out.println("Pipeline created successfully. Pipeline ARN: " + pipelineResponse.pipeline().roleArn());
	}

	
	public static void listAllPipeline() {
		Region region = Region.AP_SOUTH_1;
		CodePipelineClient pipelineClient = CodePipelineClient.builder().region(region).build();

		try {
			ListPipelinesResponse response = pipelineClient.listPipelines();
			List<PipelineSummary> pipelines = response.pipelines();
			for (PipelineSummary pipeline : pipelines) {
				System.out.println("The name of the pipeline is " + pipeline.name());
			}

		} catch (CodePipelineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		pipelineClient.close();
	}

	public static void listPipelineExecution(Scanner sc) {
		System.out.println("enter a pipeline name");
		String name = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodePipelineClient pipelineClient = CodePipelineClient.builder().region(region).build();

		try {
			ListPipelineExecutionsRequest executionsRequest = ListPipelineExecutionsRequest.builder().maxResults(10)
					.pipelineName(name).build();

			ListPipelineExecutionsResponse response = pipelineClient.listPipelineExecutions(executionsRequest);
			List<PipelineExecutionSummary> executionSummaryList = response.pipelineExecutionSummaries();
			for (PipelineExecutionSummary exe : executionSummaryList) {
				System.out.println("The pipeline execution id is " + exe.pipelineExecutionId());
				System.out.println("The execution status is " + exe.statusAsString());
			}

		} catch (CodePipelineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		pipelineClient.close();
	}

	public static void getPipeline(Scanner sc) {
		System.out.println("enter a pipeline name");
		String name = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodePipelineClient pipelineClient = CodePipelineClient.builder().region(region).build();

		try {
			GetPipelineRequest pipelineRequest = GetPipelineRequest.builder().name(name).version(1).build();

			GetPipelineResponse response = pipelineClient.getPipeline(pipelineRequest);
			List<StageDeclaration> stages = response.pipeline().stages();
			for (StageDeclaration stage : stages) {
				System.out.println("Stage name is " + stage.name() + " and actions are:");

				// Get the stage actions.
				List<ActionDeclaration> actions = stage.actions();
				for (ActionDeclaration action : actions) {
					System.out.println("Action name is " + action.name());
					System.out.println("Action type id is " + action.actionTypeId());
				}
			}

		} catch (CodePipelineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		pipelineClient.close();
	}

	public static void deletePipeline(Scanner sc) {
		System.out.println("enter a pipeline name");
		String name = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodePipelineClient pipelineClient = CodePipelineClient.builder().region(region).build();

		try {
			DeletePipelineRequest deletePipelineRequest = DeletePipelineRequest.builder().name(name).build();

			pipelineClient.deletePipeline(deletePipelineRequest);
			System.out.println(name + " was successfully deleted");

		} catch (CodePipelineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		pipelineClient.close();
	}

	public static void startPipelineExecution(Scanner sc) {
		System.out.println("enter a pipeline name");
		String name = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodePipelineClient pipelineClient = CodePipelineClient.builder().region(region).build();

		try {
			StartPipelineExecutionRequest pipelineExecutionRequest = StartPipelineExecutionRequest.builder().name(name)
					.build();

			StartPipelineExecutionResponse response = pipelineClient.startPipelineExecution(pipelineExecutionRequest);
			System.out.println("Pipeline " + response.pipelineExecutionId() + " was successfully executed");

		} catch (CodePipelineException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		pipelineClient.close();
	}

}
