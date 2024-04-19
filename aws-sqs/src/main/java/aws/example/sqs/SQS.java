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
		Scanner sc = new Scanner(System.in);
		int option;
		do {
			printMenu();
			option = sc.nextInt();
			if (option > 0 && option <= 7) {
				switch (option) {
				case 0:
					System.out.println("\nBye");
					break;
				case 1: // list all queues
					SQSHelper.listAllQueues();
					break;
				case 2: // create new queue
					SQSHelper.createNewQueue(sc);
					break;
				case 3: // delete queue
					SQSHelper.deleteQueue(sc);
					break;
				case 4: // send message
					SQSHelper.sendMessage(sc);
					break;
				case 5: // receive message
					SQSHelper.receiveMessage(sc);
					break;
				case 6: // delete message after consumption
					SQSHelper.deleteMessageafterConsumption(sc);
					break;
				case 7: // create dead letter queue
					SQSHelper.createDeadLetterQueue(sc);
					break;
				default:
					System.out.println("ERROR: Enter a valid option!!");
				}
			} else {
				System.out.println("ERROR: Enter a valid option!!");
			}

		} while (option != 0);
	}

}
