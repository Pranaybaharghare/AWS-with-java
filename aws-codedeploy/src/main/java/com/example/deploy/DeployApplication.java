

package com.example.deploy;


import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.S3Location;
import software.amazon.awssdk.services.codedeploy.model.RevisionLocation;
import software.amazon.awssdk.services.codedeploy.model.CreateDeploymentRequest;
import software.amazon.awssdk.services.codedeploy.model.CreateDeploymentResponse;
import software.amazon.awssdk.services.codedeploy.model.RevisionLocationType;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;


public class DeployApplication {
    public static void main(String[] args) {

    	Scanner sc =new Scanner(System.in);
    	System.out.println("enter a application name");
        String appName = sc.next();
        System.out.println("enter a bucket name");
        String bucketName = sc.next();
        System.out.println("enter a bundletype");
        String bundleType = sc.next();
        System.out.println("enter a key");
        String key = sc.next();
        System.out.println("enter a deployment group");
        String deploymentGroup = sc.next();

        Region region = Region.AP_SOUTH_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
            .region(region)
            .build();

        String deploymentId = createAppDeployment(deployClient, appName, bucketName, bundleType, key, deploymentGroup);
        System.out.println("The deployment Id is "+deploymentId);
        deployClient.close();
    }

    // snippet-start:[codedeploy.java2._deploy_app.main]
    public static String createAppDeployment(CodeDeployClient deployClient,
                                           String appName,
                                           String bucketName,
                                           String bundleType,
                                           String key,
                                           String deploymentGroup) {

        try{
            S3Location s3Location = S3Location.builder()
                .bucket(bucketName)
                .bundleType(bundleType)
                .key(key)
                .build();

            RevisionLocation revisionLocation = RevisionLocation.builder()
                .s3Location(s3Location)
                .revisionType(RevisionLocationType.S3)
                .build();

            CreateDeploymentRequest deploymentRequest = CreateDeploymentRequest.builder()
                .applicationName(appName)
                .deploymentGroupName(deploymentGroup)
                .description("A deployment created by the Java API")
                .revision(revisionLocation)
                .build();

            CreateDeploymentResponse response = deployClient.createDeployment(deploymentRequest);
            return response.deploymentId();

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
 }
