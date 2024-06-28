package com.example.commit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CodeCommitException;
import software.amazon.awssdk.services.codecommit.model.CreateBranchRequest;
import software.amazon.awssdk.services.codecommit.model.CreatePullRequestRequest;
import software.amazon.awssdk.services.codecommit.model.CreatePullRequestResponse;
import software.amazon.awssdk.services.codecommit.model.CreateRepositoryRequest;
import software.amazon.awssdk.services.codecommit.model.CreateRepositoryResponse;
import software.amazon.awssdk.services.codecommit.model.DeleteBranchRequest;
import software.amazon.awssdk.services.codecommit.model.DeleteRepositoryRequest;
import software.amazon.awssdk.services.codecommit.model.File;
import software.amazon.awssdk.services.codecommit.model.GetMergeOptionsRequest;
import software.amazon.awssdk.services.codecommit.model.GetMergeOptionsResponse;
import software.amazon.awssdk.services.codecommit.model.GetPullRequestRequest;
import software.amazon.awssdk.services.codecommit.model.GetPullRequestResponse;
import software.amazon.awssdk.services.codecommit.model.GetRepositoryRequest;
import software.amazon.awssdk.services.codecommit.model.GetRepositoryResponse;
import software.amazon.awssdk.services.codecommit.model.ListRepositoriesResponse;
import software.amazon.awssdk.services.codecommit.model.MergeBranchesByFastForwardRequest;
import software.amazon.awssdk.services.codecommit.model.MergeBranchesByFastForwardResponse;
import software.amazon.awssdk.services.codecommit.model.PutFileRequest;
import software.amazon.awssdk.services.codecommit.model.PutFileResponse;
import software.amazon.awssdk.services.codecommit.model.RepositoryMetadata;
import software.amazon.awssdk.services.codecommit.model.RepositoryNameIdPair;
import software.amazon.awssdk.services.codecommit.model.Target;

public class CodeCommitHelper {

	public static void createNewRepository(Scanner sc) {
		System.out.println("enter a repository name");
		String repoName = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {
			CreateRepositoryRequest repositoryRequest = CreateRepositoryRequest.builder()
					.repositoryDescription("Created by the CodeCommit Java API").repositoryName(repoName).build();

			CreateRepositoryResponse repositoryResponse = codeCommitClient.createRepository(repositoryRequest);
			System.out.println("The ARN of the new repository is " + repositoryResponse.repositoryMetadata().arn());

		} catch (CodeCommitException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		codeCommitClient.close();

	}

	public static void getRepository(Scanner sc) {
		System.out.println("enter a repository name");
		String repoName = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {
			GetRepositoryRequest repositoryRequest = GetRepositoryRequest.builder().repositoryName(repoName).build();

			GetRepositoryResponse repositoryResponse = codeCommitClient.getRepository(repositoryRequest);
			System.out.println("The ARN of the " + repoName + " is " + repositoryResponse.repositoryMetadata().arn());

		} catch (CodeCommitException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		codeCommitClient.close();
	}

	public static void listAllRepository() {
		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {
			ListRepositoriesResponse repResponse = codeCommitClient.listRepositories();
			List<RepositoryNameIdPair> repoList = repResponse.repositories();

			for (RepositoryNameIdPair repo : repoList) {
				System.out.println("The repository name is " + repo.repositoryName());
			}

		} catch (CodeCommitException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		codeCommitClient.close();
	}

	public static void deleteRepository(Scanner sc) {
		System.out.println("enter a repository name");
		String repoName = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {
			DeleteRepositoryRequest deleteRepositoryRequest = DeleteRepositoryRequest.builder().repositoryName(repoName)
					.build();

			codeCommitClient.deleteRepository(deleteRepositoryRequest);
			System.out.println("repository deleted successfully");

		} catch (CodeCommitException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		codeCommitClient.close();
	}

	public static void createNewBranch(Scanner sc) {
		System.out.println("enter a repository name");
		String repoName = sc.next();
		System.out.println("enter a branch name");
		String branchName = sc.next();
		System.out.println("enter a commit ID");
		String commitId = sc.next();

		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {

			CreateBranchRequest branchRequest = CreateBranchRequest.builder().branchName(branchName)
					.repositoryName(repoName).commitId(commitId).build();

			codeCommitClient.createBranch(branchRequest);
			System.out.println("Branch " + branchName + " was created");

		} catch (CodeCommitException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		codeCommitClient.close();
	}

	public static void mergeBranch(Scanner sc) {
		System.out.println("enter a repository name");
		String repoName = sc.next();
		System.out.println("eter a target branch");
		String targetBranch = sc.next();
		System.out.println("enter a soure reference");
		String sourceReference = sc.next();
		System.out.println("enter a destination commit ID");
		String destinationCommitId = sc.next();

		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {
			MergeBranchesByFastForwardRequest fastForwardRequest = MergeBranchesByFastForwardRequest.builder()
					.destinationCommitSpecifier(destinationCommitId).targetBranch(targetBranch)
					.sourceCommitSpecifier(sourceReference).repositoryName(repoName).build();

			MergeBranchesByFastForwardResponse response = codeCommitClient
					.mergeBranchesByFastForward(fastForwardRequest);
			System.out.println("The commit id is " + response.commitId());

		} catch (CodeCommitException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		codeCommitClient.close();
	}

	public static void deleteBranch(Scanner sc) {
		System.out.println("enter a repository name");
		String repoName = sc.next();
		System.out.println("enter a branch name");
		String branchName = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {
			DeleteBranchRequest branchRequest = DeleteBranchRequest.builder().branchName(branchName)
					.repositoryName(repoName).build();

			codeCommitClient.deleteBranch(branchRequest);
			System.out.println("The " + branchName + " branch was deleted!");

		} catch (CodeCommitException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		codeCommitClient.close();
	}

	public static void getMergeOption(Scanner sc) {
		System.out.println("enter a repository name");
		String repoName = sc.next();
		System.out.println("enter a destination reference");
		String destinationReference = sc.next();
		System.out.println("enter a source reference");
		String sourceReference = sc.next();

		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {
			GetMergeOptionsRequest optionsRequest = GetMergeOptionsRequest.builder().repositoryName(repoName)
					.destinationCommitSpecifier(destinationReference).sourceCommitSpecifier(sourceReference).build();

			GetMergeOptionsResponse response = codeCommitClient.getMergeOptions(optionsRequest);
			System.out.println("The id value is " + response.baseCommitId());

		} catch (CodeCommitException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		codeCommitClient.close();
	}

	public static void createPullRequest(Scanner sc) {
		System.out.println("enter a repository name");
		String repoName = sc.next();
		System.out.println("enter a destination reference");
		String destinationReference = sc.next();
		System.out.println("enter a source reference");
		String sourceReference = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {
			Target target = Target.builder().repositoryName(repoName).destinationReference(destinationReference)
					.sourceReference(sourceReference).build();

			List<Target> myList = new ArrayList<>();
			myList.add(target);
			CreatePullRequestRequest pullRequestRequest = CreatePullRequestRequest.builder()
					.description("A Pull request created by the Java API").title("Example Pull Request").targets(myList)
					.build();

			CreatePullRequestResponse requestResponse = codeCommitClient.createPullRequest(pullRequestRequest);
			System.out.println("The pull request id is " + requestResponse.pullRequest().pullRequestId());

		} catch (CodeCommitException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		codeCommitClient.close();
	}

	public static void getPullRequest(Scanner sc) {
		System.out.println("enter a pull rewquest ID");
		String pullRequestId = sc.next();
		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {
			GetPullRequestRequest pullRequestRequest = GetPullRequestRequest.builder().pullRequestId(pullRequestId)
					.build();

			GetPullRequestResponse pullResponse = codeCommitClient.getPullRequest(pullRequestRequest);
			System.out.println("The title of the pull request is  " + pullResponse.pullRequest().title());
			System.out.println("The pull request status is " + pullResponse.pullRequest().pullRequestStatus());
			System.out.println("The pull request id is " + pullResponse.pullRequest().pullRequestId());

		} catch (CodeCommitException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		codeCommitClient.close();
	}

	public static void putFile(Scanner sc) {
		System.out.println("enter a repository name");
		String repoName = sc.next();
		System.out.println("enter a branch name");
		String branchName = sc.next();
		System.out.println("enter a filepath for file");
		String filePath = sc.next();
		System.out.println("enter a email");
		String email = sc.next();
		System.out.println("enter a author name");
		String name = sc.next();
		System.out.println("enter a repository path");
		String repoPath = sc.next();

		Region region = Region.AP_SOUTH_1;
		CodeCommitClient codeCommitClient = CodeCommitClient.builder().region(region).build();

		try {
			java.io.File myFile = new java.io.File(filePath);
			InputStream is = new FileInputStream(myFile);
			SdkBytes fileToUpload = SdkBytes.fromInputStream(is);

			PutFileRequest fileRequest = PutFileRequest.builder().fileContent(fileToUpload).repositoryName(repoName)
					.commitMessage("Uploaded via the Java API").branchName(branchName).filePath(repoPath)
					.email(email).name(name).build();

			PutFileResponse fileResponse = codeCommitClient.putFile(fileRequest);
			System.out.println("The commit ID is " + fileResponse.commitId());

		} catch (CodeCommitException | FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		codeCommitClient.close();
	}

	public static void getRepoUrl(Scanner sc) {
		System.out.println();
        Region region = Region.AP_SOUTH_1;

        System.out.println("enter a repository name");
        String repositoryName = sc.next();

        // Set up the CodeCommit client
        CodeCommitClient codeCommitClient = CodeCommitClient.builder()
                .region(region)
                .build();

        // Get repository metadata, including the HTTPS URL
        RepositoryMetadata repositoryMetadata = getRepositoryMetadata(codeCommitClient, repositoryName);

        // Print the HTTPS URL of the CodeCommit repository
        if (repositoryMetadata != null) {
            System.out.println("CodeCommit Repository URL: " + repositoryMetadata.cloneUrlHttp());
        } else {
            System.out.println("CodeCommit repository not found.");
        }

        // Close the CodeCommit client
        codeCommitClient.close();
		
	}
	
	private static RepositoryMetadata getRepositoryMetadata(CodeCommitClient codeCommitClient, String repositoryName) {
        try {
            return codeCommitClient.getRepository(r -> r.repositoryName(repositoryName)).repositoryMetadata();
        } catch (Exception e) {
            // Handle exceptions (e.g., repository not found)
            e.printStackTrace();
            return null;
        }
    }

}
