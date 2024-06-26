
package aws.example.ec2;

import java.io.IOException;
import java.util.Scanner;

import com.jcraft.jsch.SftpException;

public class EC2Service {

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
					EC2Helper.describeInstances();
					break;
				case 2: // Run instance
					instanceId = EC2Helper.runInstance(sc);
					System.out.println("Instance Id: " + instanceId);
					break;
				case 3: // Describe instance
					EC2Helper.describeInstance(sc);
					break;	
				case 4: // Start instance
					EC2Helper.startInstance(sc);
					break;
				case 5: // Stop instance
					EC2Helper.stopInstance(sc);
					break;	
				case 6: // Reboot instance
					EC2Helper.rebootInstance(sc);
					break;	
				case 7: // Terminate instance
					EC2Helper.terminateInstance(sc);
					instanceId = null;
					break;	
				case 8: // connect with instance
					EC2Helper.connectInstance(sc);
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
