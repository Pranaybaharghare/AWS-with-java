/**
     * AWSHelper class with methods for managing AWS EC2 instances.
     */

package example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.util.Base64;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

public final class AWSHelper {
	private static final String REGION = "ap-south-1"; // Region name
	private static final String AMI_ID = "ami-00952f27cf14db9cd"; // AMI Id
	private static final String INSTANCE_TYPE = "t2.micro"; // Instance Type
	// private static final String SECURITY_GROUP = "default"; //"sg-068f967e";

	private AWSHelper() {
	}

	/**
	 * Describes all EC2 instances associated with an AWS account
	 */
	public static void describeInstances() {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(REGION).build();
		boolean done = false;

		System.out.println("Describing EC2 instances ...");

		while (!done) {
			DescribeInstancesRequest request = new DescribeInstancesRequest();
			DescribeInstancesResult response = ec2.describeInstances(request);

			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					System.out.printf(
							"Found reservation with id \"%s\", " + "AMI \"%s\", " + "type \"%s\", " + "state \"%s\" "
									+ "and monitoring state \"%s\"\n",
							instance.getInstanceId(), instance.getImageId(), instance.getInstanceType(),
							instance.getState().getName(), instance.getMonitoring().getState());

					List<Tag> tags = instance.getTags();
					System.out.println("      Tags:   " + tags);
				}
			}

			request.setNextToken(response.getNextToken());

			if (response.getNextToken() == null) {
				done = true;
			}
		}
		System.out.println();
	}

	public static String getUserDataScript(String password) {
		// This script sets the password for the default user (e.g., ec2-user)
		// Modify this script based on your Linux distribution
		return "#!/bin/bash\n" + "echo 'ec2-user:" + password + "' | sudo chpasswd\n"
				+ "sudo sed -i 's/PasswordAuthentication no/PasswordAuthentication yes/' /etc/ssh/sshd_config\n"
				+ "sudo service sshd restart\n";
	}

	private static void tagInstance(AmazonEC2 ec2, String instanceId, String tagName, String value) {
		CreateTagsRequest tagRequest = new CreateTagsRequest().withResources(instanceId)
				.withTags(new Tag(tagName, value));
		ec2.createTags(tagRequest);
	}

	public static String runInstance(Scanner sc) {

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(REGION).build();

		System.out.println("enter a key-pair name");
		String keyPairValue = sc.next();
		System.out.println("enter the password");
		String instancePassword = sc.next();
		CreateKeyPairRequest keyPairRequest = new CreateKeyPairRequest().withKeyName(keyPairValue);
		CreateKeyPairResult response = ec2.createKeyPair(keyPairRequest);
		KeyPair keyPair = response.getKeyPair();
		String privateKeyContent = keyPair.getKeyMaterial();
		String privateKeyFilePath = "C:\\Users\\Pranay B\\Downloads\\" + keyPairValue + ".pem";

		try (FileWriter writer = new FileWriter(privateKeyFilePath)) {
			writer.write(privateKeyContent);
		} catch (IOException e) {
			System.err.println("Error saving private key: " + e.getMessage());
		}
		// Print the private key
		System.out.println("Successfully created key pair named " + keyPairValue);

		System.out.println("Creating E2 instance ...");
		String getUserDataScript = getUserDataScript(instancePassword);
		String base64UserDataScript = java.util.Base64.getEncoder()
				.encodeToString(getUserDataScript.getBytes(StandardCharsets.UTF_8));
		RunInstancesRequest runRequest = new RunInstancesRequest().withImageId(AMI_ID).withInstanceType(INSTANCE_TYPE)
				.withMaxCount(1).withMinCount(1).withKeyName(keyPairValue).withUserData(base64UserDataScript);
		// .withSecurityGroups(SECURITY_GROUP);

		RunInstancesResult instancesResult = ec2.runInstances(runRequest);

		String reservationId = instancesResult.getReservation().getReservationId();

		System.out.println(" Reservation Id:   " + reservationId);

		List<Instance> instances = instancesResult.getReservation().getInstances();
		Instance instance = instances.get(0);
		String instanceId = instance.getInstanceId();
		String publicIpAddress = instance.getPublicIpAddress();
		String publicDnsName = instance.getPublicDnsName();
		String keyPairName = instance.getKeyName();

		// Tag EC2 Instance
		System.out.println("enter instance name");
		String tagName = sc.next();
		tagInstance(ec2, instanceId, "Name", tagName);
		System.out.printf("Added Tag: Name with Value: %s\n", tagName);

		System.out.println("PUBLIC IP ADDRESS: " + publicIpAddress);
		System.out.println("PUBLIC DNS NAME: " + publicDnsName);
		System.out.println("Key Pair: " + keyPairName);
		return instanceId;
	}

	public static void describeInstance(Scanner sc) {
		System.out.println("enter instance id to describe instance");
		String existingInstanceId = sc.next();

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(REGION).build();

		System.out.println("Describing EC2 instance ...");

		DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(existingInstanceId);
		DescribeInstancesResult response = ec2.describeInstances(request);

		List<Reservation> reservations = response.getReservations();
		Reservation reservation = reservations.get(0);

		List<Instance> instances = reservation.getInstances();
		Instance instance = instances.get(0);

		String imageId = instance.getImageId();
		String instanceType = instance.getInstanceType();
		String state = instance.getState().getName();
		List<GroupIdentifier> groups = instance.getSecurityGroups();
		String privateDnsName = instance.getPrivateDnsName();
		String privateIpAddress = instance.getPrivateIpAddress();
		String publicDnsName = instance.getPublicDnsName();
		String publicIpAddress = instance.getPublicIpAddress();
		List<Tag> tags = instance.getTags();

		System.out.println("Instance Id:       " + existingInstanceId);
		System.out.println("Image Id:          " + imageId);
		System.out.println("Instance Type:     " + instanceType);
		System.out.println("Security Groups:   " + groups);
		for (int i = 0; i < groups.size(); i++) {
			System.out.println("Security Group:    " + groups.get(i));
		}
		System.out.println("State:             " + state);
		System.out.println("Private DNS Name:  " + privateDnsName);
		System.out.println("Private IP Name:   " + privateIpAddress);
		System.out.println("Public DNS Name:   " + publicDnsName);
		System.out.println("Public IP Name:    " + publicIpAddress);
		System.out.println("Tags:              " + tags);
		for (int i = 0; i < tags.size(); i++) {
			System.out.println("Tags:              " + tags.get(i));
		}
		System.out.println();
	}

	public static void startInstance(Scanner sc) {
		System.out.println("enter instance id to start instance");
		String existingInstanceId = sc.next();

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(REGION).build();

		System.out.println("Starting EC2 instance ...");

		StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(existingInstanceId);

		ec2.startInstances(request);

		DescribeInstancesRequest requestInstanceDetails = new DescribeInstancesRequest()
				.withInstanceIds(existingInstanceId);
		DescribeInstancesResult response = ec2.describeInstances(requestInstanceDetails);

		List<Reservation> reservations = response.getReservations();
		Reservation reservation = reservations.get(0);

		List<Instance> instances = reservation.getInstances();
		Instance instance = instances.get(0);

		String instanceId = instance.getInstanceId();
		String publicIpAddress = instance.getPublicIpAddress();
		String publicDnsName = instance.getPublicDnsName();
		String keyPairName = instance.getKeyName();

		System.out.println("INSTANCE ID: " + instanceId);
		System.out.println("PUBLIC IP ADDRESS: " + publicIpAddress);
		System.out.println("PUBLIC DNS NAME: " + publicDnsName);
		System.out.println("Key Pair: " + keyPairName);

	}

	public static void stopInstance(Scanner sc) {
		System.out.println("enter instance id to stop instance");
		String existingInstanceId = sc.next();

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(REGION).build();

		System.out.println("Stopping EC2 instance ...");

		StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(existingInstanceId);

		ec2.stopInstances(request);
	}

	public static void rebootInstance(Scanner sc) {

		System.out.println("enter instance id to reboot instance");
		String existingInstanceId = sc.next();

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(REGION).build();

		System.out.println("Rebooting EC2 instance ...");

		RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(existingInstanceId);

		ec2.rebootInstances(request);
	}

	public static void terminateInstance(Scanner sc) {

		System.out.println("enter instance id to terminate instance");
		String existingInstanceId = sc.next();

		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(REGION).build();

		System.out.println("Terminating EC2 instance ...");

		TerminateInstancesRequest request = new TerminateInstancesRequest().withInstanceIds(existingInstanceId);

		ec2.terminateInstances(request);
	}

}
