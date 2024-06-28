package com.aws.example;

import java.util.Scanner;

public class BeanstalkOperations {

	private static void printMenu() {
		System.out.println("\nMENU");
		System.out.println("0 = Quit");
		System.out.println("1 = create new application");
		System.out.println("2 = create new environment");
		System.out.println("3 = describe application");
//		System.out.println("4 = describe environment");
		System.out.println("5 = delete application");
//		System.out.println("6 = describe configuration options");
		System.out.println("Enter an option?");
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int option;
		do {
			printMenu();
			option = sc.nextInt();
			if (option > 0 && option <= 6) {
				switch (option) {
				case 0:
					System.out.println("\nBye");
					break;
				case 1: // create new application
					BeanstalkHelper.createNewApplication(sc);
					break;
				case 2: // create new environment
					BeanstalkHelper.createNewEnvironment(sc);
					break;
				case 3: // describe application
					BeanstalkHelper.describeApplication();
					break;
//				case 4: // describe environment
//					BeanstalkHelper.describeEnvironment(sc);
//					break;
				case 5: // delete application
					BeanstalkHelper.deleteApplication(sc);
					break;
//				case 6: // describe configuration option
//					BeanstalkHelper.describeConfigurationOption(sc);
//					break;
				default:
					System.out.println("ERROR: Enter a valid option!!");
				}
			} else {
				System.out.println("ERROR: Enter a valid option!!");
			}

		} while (option != 0);
	}

}
