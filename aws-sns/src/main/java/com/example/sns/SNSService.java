package com.example.sns;

import java.util.Scanner;

public class SNSService {
	 /**
     * Print a menu in the screen with the available options
     */
    private static void printMenu() {
        System.out.println("\nMENU");
        System.out.println("0 = Quit");
        System.out.println("1 = list all topic");
        System.out.println("2 = create new topic");
        System.out.println("3 = delete topic");
        System.out.println("4 = list all subscription");
        System.out.println("5 = publish Topic Message");
        System.out.println("6 = subscribe to mail");
        System.out.println("7 = subscribe to sqs");
        System.out.println("8 = Unsubscribe topic");

        System.out.println("Enter an option?");
    }
public static void main(String[] args) {
	Scanner sc = new Scanner(System.in);
    int option;
    do {
    	 printMenu();
         option = sc.nextInt();
         if (option>0&&option<=8) {
       	  switch (option) {
             case 0:
                 System.out.println("\nBye");
                 break;
             case 1:  
                 SNSHelper.listAllTopics();
                 break;
             case 2:  
                 SNSHelper.createNewTopic(sc);
                 break;
             case 3:  
                 SNSHelper.deleteTopic(sc);
                 break;
             case 4:  
                 SNSHelper.listSubscription();
                 break;
             case 5:
            	 SNSHelper.publishTopicMessagee(sc);
            	 break;
             case 6:
            	 SNSHelper.subscribeToMail(sc);
            	 break;
             case 7:
            	 SNSHelper.subscribeToSqs(sc);
            	 break;
             case 8:  // create dead letter queue
                 SNSHelper.unsubscribeTopic(sc);
                 break;    
             default:
                 System.out.println("ERROR: Enter a valid option!!");
         }
       } else {
       	System.out.println("ERROR: Enter a valid option!!");
       }
     
	} while (option!=0);
}
}
