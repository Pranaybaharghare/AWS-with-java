package com.example.deploy;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.ListDeploymentGroupsRequest;
import software.amazon.awssdk.services.codedeploy.model.ListDeploymentGroupsResponse;
import java.util.List;
import java.util.Scanner;

public class ListDeploymentGroups {
    public static void main(String[] args) {

    	Scanner sc = new Scanner(System.in);
    	System.out.println("enter a application name");
        String appName = sc.next();
        Region region = Region.AP_SOUTH_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
            .region(region)
            .build();

        listDeployGroups(deployClient, appName);
        deployClient.close();
    }

    public static void listDeployGroups(CodeDeployClient deployClient, String appName) {

        try {
            ListDeploymentGroupsRequest groupsRequest = ListDeploymentGroupsRequest.builder()
                .applicationName(appName)
                .build();

            ListDeploymentGroupsResponse response = deployClient.listDeploymentGroups(groupsRequest);
            List<String> groups = response.deploymentGroups();
            for (String group: groups) {
                System.out.println("The deployment group is: "+group);
            }

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
   }
}