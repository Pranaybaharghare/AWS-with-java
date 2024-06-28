package com.example.pipeline;

import java.util.Scanner;

public class CodePipelineOperation {

	private static void printMenu() {
		System.out.println("\nMENU");
		System.out.println("0 = Quit");
		System.out.println("1 = create pipeline with code deploy");
		System.out.println("2 = create pipeline with beanstalk with java");
		System.out.println("3= list all pipelines");
		System.out.println("4 = list pipeline execution");
		System.out.println("5 = get pipeline");
		System.out.println("6 = delete pipeline");
		System.out.println("7 = start pipeline execution");
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
				case 1: // create new application
					CodePipelineHelper.createPipelineWithCodeDeploy(sc);
					break;
				case 2: // create new application
					CodePipelineHelper.createPipelineWithBeanstalk(sc);
					break;	
				case 3: // create new environment
					CodePipelineHelper.listAllPipeline();
					break;
				case 4: // describe application
					CodePipelineHelper.listPipelineExecution(sc);
					break;
				case 5: // describe environment
					CodePipelineHelper.getPipeline(sc);
					break;
				case 6: // delete application
					CodePipelineHelper.deletePipeline(sc);
					break;
				case 7: // describe configuration option
					CodePipelineHelper.startPipelineExecution(sc);
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
