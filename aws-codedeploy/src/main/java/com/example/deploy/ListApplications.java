
package com.example.deploy;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.ListApplicationsResponse;
import java.util.List;

public class ListApplications {
    public static void main(String[] args) {
        Region region = Region.AP_SOUTH_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
            .region(region)
            .build();

        listApps(deployClient);
        deployClient.close();
    }

    // snippet-start:[codedeploy.java2._list_apps.main]
    public static void listApps(CodeDeployClient deployClient) {

        try {
            ListApplicationsResponse applicationsResponse = deployClient.listApplications();
            List<String> apps = applicationsResponse.applications();
            for (String app: apps) {
                System.out.println("The application name is: "+app);
            }

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
