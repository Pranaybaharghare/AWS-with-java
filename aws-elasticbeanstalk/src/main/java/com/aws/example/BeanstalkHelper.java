package com.aws.example;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.ApplicationDescription;
import software.amazon.awssdk.services.elasticbeanstalk.model.ConfigurationOptionDescription;
import software.amazon.awssdk.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateApplicationVersionRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateEnvironmentRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.CreateEnvironmentResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.DeleteApplicationRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeApplicationsResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeConfigurationOptionsRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeConfigurationOptionsResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeEnvironmentsRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeEnvironmentsResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.ElasticBeanstalkException;
import software.amazon.awssdk.services.elasticbeanstalk.model.EnvironmentDescription;
import software.amazon.awssdk.services.elasticbeanstalk.model.OptionSpecification;
import software.amazon.awssdk.services.elasticbeanstalk.model.S3Location;

public class BeanstalkHelper {

	public static void createNewApplication(Scanner sc) {
		System.out.println("enter application name");
		String appName = sc.next();
		Region region = Region.AP_SOUTH_1;
		ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder().region(region).build();

		try {
			CreateApplicationRequest applicationRequest = CreateApplicationRequest.builder()
					.description("An AWS Elastic Beanstalk app created using the AWS Java API").applicationName(appName)
					.build();

			CreateApplicationResponse applicationResponse = beanstalkClient.createApplication(applicationRequest);
			System.out.println("The ARN of the application is " + applicationResponse.application().applicationArn());

		} catch (ElasticBeanstalkException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	public static void createNewEnvironment(Scanner sc) {
		
		System.out.println("enter a application name");
		String appName = sc.next();
		System.out.println("enter a environment name");
		String envName = sc.next();
		System.out.println("enter a version");
		String version = sc.next();
		Region region = Region.AP_SOUTH_1;
		ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder().region(region).build();
		try {
//			String s3Key = "deployment.war";
//			String s3Bucket = "beanstalkcreatedcucket1";
			// Create an S3 source bundle (WAR file with your application)
//			S3Location sourceBundle = S3Location.builder().s3Bucket(s3Bucket).s3Key(s3Key).build();
//			System.out.println(sourceBundle);
			// Create an application version
			beanstalkClient.createApplicationVersion(CreateApplicationVersionRequest.builder().applicationName(appName)
					.versionLabel(version).build());

			CreateEnvironmentRequest applicationRequest = CreateEnvironmentRequest.builder()
					.description("An AWS Elastic Beanstalk environment created using the AWS Java API")
					.environmentName(envName)
					.solutionStackName("64bit Amazon Linux 2023 v4.2.0 running Corretto 8")
					.applicationName(appName)
					.optionSettings(Arrays.asList(
							ConfigurationOptionSetting.builder().namespace("aws:autoscaling:launchconfiguration")
									.optionName("InstanceType").value("t2.micro").build(),
							ConfigurationOptionSetting.builder().namespace("aws:autoscaling:launchconfiguration")
									.optionName("IamInstanceProfile").value("EC2InstanceProfileRole").build()))
					.build();

			CreateEnvironmentResponse response = beanstalkClient.createEnvironment(applicationRequest);

			System.out.println("The ARN of the environment is " + response.environmentArn());

		} catch (ElasticBeanstalkException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

	}

	public static void describeApplication() {
		Region region = Region.AP_SOUTH_1;
		ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder().region(region).build();

		try {
			DescribeApplicationsResponse applicationsResponse = beanstalkClient.describeApplications();
			List<ApplicationDescription> apps = applicationsResponse.applications();
			for (ApplicationDescription app : apps) {
				System.out.println("The application name is " + app.applicationName());
				DescribeEnvironmentsRequest desRequest = DescribeEnvironmentsRequest.builder()
						.applicationName(app.applicationName()).build();

				DescribeEnvironmentsResponse res = beanstalkClient.describeEnvironments(desRequest);
				List<EnvironmentDescription> envDesc = res.environments();
				for (EnvironmentDescription desc : envDesc) {
					System.out.println("The Environment ARN is " + desc.environmentArn());
					System.out.println("The Endpoint URL is " + desc.endpointURL());
					System.out.println("The status is " + desc.status());
				}
			}

		} catch (ElasticBeanstalkException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

//	public static void describeEnvironment(Scanner sc) {
//
//		System.out.println("enter a app name");
//		String appName = sc.next();
//		Region region = Region.AP_SOUTH_1;
//		ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder().region(region).build();
//
//		try {
//			DescribeEnvironmentsRequest request = DescribeEnvironmentsRequest.builder()
//					.environmentNames("Joblisting-env").build();
//
//			DescribeEnvironmentsResponse response = beanstalkClient.describeEnvironments(request);
//			List<EnvironmentDescription> envs = response.environments();
//			for (EnvironmentDescription env : envs) {
//				System.out.println("The environment name is  " + env.environmentName());
//				System.out.println("The environment ARN is  " + env.environmentArn());
//			}
//
//		} catch (ElasticBeanstalkException e) {
//			System.err.println(e.getMessage());
//			System.exit(1);
//		}
//
//	}

	public static void deleteApplication(Scanner sc) {
		System.out.println("enter a app name");
		String appName = sc.next();
		Region region = Region.AP_SOUTH_1;
		ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder().region(region).build();

		try {
			DeleteApplicationRequest applicationRequest = DeleteApplicationRequest.builder().applicationName(appName)
					.terminateEnvByForce(true).build();

			beanstalkClient.deleteApplication(applicationRequest);
			System.out.println("The Elastic Beanstalk application was successfully deleted!");

		} catch (ElasticBeanstalkException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	public static void describeConfigurationOption(Scanner sc) {
		Region region = Region.AP_SOUTH_1;
		ElasticBeanstalkClient beanstalkClient = ElasticBeanstalkClient.builder().region(region).build();

		try {
			OptionSpecification spec = OptionSpecification.builder().namespace("aws:ec2:instances").build();

			DescribeConfigurationOptionsRequest request = DescribeConfigurationOptionsRequest.builder()
					.environmentName("Joblisting-env").options(spec).build();

			DescribeConfigurationOptionsResponse response = beanstalkClient.describeConfigurationOptions(request);
			List<ConfigurationOptionDescription> options = response.options();
			for (ConfigurationOptionDescription option : options) {
				System.out.println("The namespace is " + option.namespace());
				String optionName = option.name();
				System.out.println("The option name is " + optionName);
				if (optionName.compareTo("InstanceTypes") == 0) {
					List<String> valueOptions = option.valueOptions();
					for (String value : valueOptions) {
						System.out.println("The value is " + value);
					}
				}
			}

		} catch (ElasticBeanstalkException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

	}

}
