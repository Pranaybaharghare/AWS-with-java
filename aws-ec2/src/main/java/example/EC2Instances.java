
package example;

import java.io.IOException;
import java.util.Scanner;

import com.jcraft.jsch.SftpException;

public class EC2Instances {

	private static void printMenu() {
		System.out.println("\nMENU");
		System.out.println("0 = Quit");
		System.out.println("1 = Describe all instances");
		System.out.println("2 = Run new instance");
		System.out.println("3 = Describe instance");
		System.out.println("4 = Start instance");
		System.out.println("5 = Stop instance");
		System.out.println("6 = Reboot instance");
		System.out.println("7 = Terminate instance");
		System.out.println("8 = connect instance");
		System.out.println("Enter an option?");
	}

	public static void main(String[] args) throws IOException, SftpException {
		String instanceId = null;
		Scanner sc = new Scanner(System.in);
		int option;

		do {
			printMenu();
			option = sc.nextInt();
			if (option > 0 && option <= 8) {
				switch (option) {
				case 0:
					System.out.println("\nBye");
					break;
				case 1: // Describe all instances
					AWSHelper.describeInstances();
					break;
				case 2: // Run instance
					instanceId = AWSHelper.runInstance(sc);
					System.out.println("Instance Id: " + instanceId);
					break;
				case 3: // Describe instance
					AWSHelper.describeInstance(sc);
					break;	
				case 4: // Start instance
					AWSHelper.startInstance(sc);
					break;
				case 5: // Stop instance
					AWSHelper.stopInstance(sc);
					break;	
				case 6: // Reboot instance
					AWSHelper.rebootInstance(sc);
					break;	
				case 7: // Terminate instance
					AWSHelper.terminateInstance(sc);
					instanceId = null;
					break;	
				default:
					System.out.println("ERROR: Enter a valid option!!");
				}
			} else {
				System.out.println("ERROR: Enter a valid option!!");
			}

		} while (option != 0);
		sc.close();
	}
}
