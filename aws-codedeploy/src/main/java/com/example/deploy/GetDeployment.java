package com.example.deploy;

import java.util.Scanner;

// snippet-start:[codedeploy.java2._get_deployment.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.GetDeploymentRequest;
import software.amazon.awssdk.services.codedeploy.model.GetDeploymentResponse;

public class GetDeployment {

    public static void main(String[] args) {

    	Scanner sc = new Scanner(System.in);
    	System.out.println("enter a deployment id");
       String deploymentId = sc.next();
       Region region = Region.AP_SOUTH_1;
       CodeDeployClient deployClient = CodeDeployClient.builder()
           .region(region)
           .build();

       getSpecificDeployment(deployClient, deploymentId);
       deployClient.close();
    }

    public static void getSpecificDeployment(CodeDeployClient deployClient, String deploymentId ) {

        try {
            GetDeploymentRequest deploymentRequest = GetDeploymentRequest.builder()
                .deploymentId(deploymentId)
                .build();

            GetDeploymentResponse response = deployClient.getDeployment(deploymentRequest);
            System.out.println("The application associated with this deployment is "+ response.deploymentInfo().applicationName());

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
