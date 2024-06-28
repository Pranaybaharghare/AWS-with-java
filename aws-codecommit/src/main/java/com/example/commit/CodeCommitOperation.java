package com.example.commit;

import java.util.Scanner;

public class CodeCommitOperation {
	private static void printMenu() {
		System.out.println("\nMENU");
		System.out.println("0 = Quit");
		System.out.println("1 = create new repository");
		System.out.println("2 = get repository");
		System.out.println("3 = list all repository");
		System.out.println("4 = delete repository");
		System.out.println("5 = create new branch");
		System.out.println("6 = merge branches");
		System.out.println("7 = delete branch");
		System.out.println("8 = get merge option");
		System.out.println("9 = create pull request");
		System.out.println("10 = get pull request");
		System.out.println("11 = put file");
		System.out.println("12 = get repository url");
		System.out.println("Enter an option?");
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int option;
		do {
			printMenu();
			option = sc.nextInt();
			if (option > 0 && option <= 12) {
				switch (option) {
				case 0:
					System.out.println("\nBye");
					break;
				case 1: // create new repository
					CodeCommitHelper.createNewRepository(sc);
					break;
				case 2: // get repository
					CodeCommitHelper.getRepository(sc);
					break;
				case 3: // list all repository
					CodeCommitHelper.listAllRepository();
					break;
				case 4: // delete repository
					CodeCommitHelper.deleteRepository(sc);
					break;
				case 5: // create new branch
					CodeCommitHelper.createNewBranch(sc);
					break;
				case 6: // merge branches"
					CodeCommitHelper.mergeBranch(sc);
					break;
				case 7: // delete branch
					CodeCommitHelper.deleteBranch(sc);
					break;
				case 8: // get merge option
					CodeCommitHelper.getMergeOption(sc);
					break;
				case 9: // create pull request
					CodeCommitHelper.createPullRequest(sc);
					break;
				case 10: // get pull request
					CodeCommitHelper.getPullRequest(sc);
					break;
				case 11: // put file
					CodeCommitHelper.putFile(sc);
					break;
				case 12: // get repository url
					CodeCommitHelper.getRepoUrl(sc);
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
