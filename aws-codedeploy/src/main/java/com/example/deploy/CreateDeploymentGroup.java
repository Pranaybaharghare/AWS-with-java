
package com.example.deploy;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.DeploymentStyle;
import software.amazon.awssdk.services.codedeploy.model.DeploymentType;
import software.amazon.awssdk.services.codedeploy.model.DeploymentOption;
import software.amazon.awssdk.services.codedeploy.model.EC2TagFilter;
import software.amazon.awssdk.services.codedeploy.model.CreateDeploymentGroupRequest;
import software.amazon.awssdk.services.codedeploy.model.CreateDeploymentGroupResponse;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CreateDeploymentGroup {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("enter a deployment group name");
		String deploymentGroupName = sc.next();
		System.out.println("enter a application name");
		String appName = sc.next();
		String serviceRoleArn = "arn:aws:iam::210858943634:role/CodeDeployRole";
		String tagKey = "name";
		System.out.println("enter a instance name");
		String tagValue = sc.next();

		Region region = Region.AP_SOUTH_1;
		CodeDeployClient deployClient = CodeDeployClient.builder().region(region).build();

		String groupId = createNewDeploymentGroup(deployClient, deploymentGroupName, appName, serviceRoleArn, tagKey,
				tagValue);
		System.out.println("The group deployment ID is " + groupId);
		deployClient.close();
	}

	// snippet-start:[codedeploy.java2.create_deployment_group.main]
	public static String createNewDeploymentGroup(CodeDeployClient deployClient, String deploymentGroupName,
			String appName, String serviceRoleArn, String tagKey, String tagValue) {

		try {
			DeploymentStyle style = DeploymentStyle.builder().deploymentType(DeploymentType.IN_PLACE)
					.deploymentOption(DeploymentOption.WITHOUT_TRAFFIC_CONTROL).build();

			EC2TagFilter tagFilter = EC2TagFilter.builder().key(tagKey).value(tagValue).type("KEY_AND_VALUE").build();

			List<EC2TagFilter> tags = new ArrayList<>();
			tags.add(tagFilter);

			CreateDeploymentGroupRequest groupRequest = CreateDeploymentGroupRequest.builder()
					.deploymentGroupName(deploymentGroupName).applicationName(appName).serviceRoleArn(serviceRoleArn)
					.deploymentStyle(style).ec2TagFilters(tags).build();

			CreateDeploymentGroupResponse groupResponse = deployClient.createDeploymentGroup(groupRequest);
			return groupResponse.deploymentGroupId();

		} catch (CodeDeployException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		return "";
	}
}
