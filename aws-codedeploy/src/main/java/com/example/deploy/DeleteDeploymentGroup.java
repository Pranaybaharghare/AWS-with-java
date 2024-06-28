//snippet-sourcedescription:[DeleteDeploymentGroup.java demonstrates how to delete a deployment group.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS CodeDeploy]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.deploy;

import java.util.Scanner;

// snippet-start:[codedeploy.java2.delete_group.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.DeleteDeploymentGroupRequest;
// snippet-end:[codedeploy.java2.delete_group.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteDeploymentGroup {
    public static void main(String[] args) {

    	Scanner sc =new Scanner(System.in);
    	System.out.println("enter a application name");
        String appName = sc.next();
        System.out.println("enter a deployment group name");
        String deploymentGroupName = sc.next();
        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
            .region(region)
            .build();

        delDeploymentGroup(deployClient, appName, deploymentGroupName);
        deployClient.close();
    }

    public static void delDeploymentGroup(CodeDeployClient deployClient,
                                          String appName,
                                          String deploymentGroupName) {

        try {
            DeleteDeploymentGroupRequest deleteDeploymentGroupRequest = DeleteDeploymentGroupRequest.builder()
                .deploymentGroupName(appName)
                .applicationName(deploymentGroupName)
                .build();

            deployClient.deleteDeploymentGroup(deleteDeploymentGroupRequest);
            System.out.println(deploymentGroupName +" was deleted!");

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
