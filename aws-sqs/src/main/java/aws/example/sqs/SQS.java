package aws.example.sqs;

import java.util.Scanner;

/**
 * Author :Pranay
 */
public class SQS {
	/**
	 * Print a menu in the screen with the available options
	 */
	private static void printMenu() {
		System.out.println("\nMENU");
		System.out.println("0 = Quit");
		System.out.println("1 = list all queues");
		System.out.println("2 = create new queue");
		System.out.println("3 = delete queue");
		System.out.println("4 = send message");
		System.out.println("5 = receive message from queue");
		System.out.println("6 = delete message after consumption");
		System.out.println("7 = create dead letter queue");
		System.out.println("Enter an option?");
	}

	public static void main(String[] args) {

	}

}
