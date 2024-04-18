package example;

import java.util.Scanner;

public class S3Service {

	private static void printMenu() {
		System.out.println("\nMENU");
		System.out.println("0 = Quit");
		System.out.println("1 = list all s3 bucket");
		System.out.println("2 = describe s3 bucket");
		System.out.println("3 = upload object on s3 bucket");
		System.out.println("4 = create new s3 bucket");
		System.out.println("5 = download object from s3 bucket");
		System.out.println("6 = delete object from s3 bucket ");
		System.out.println("7 = delete s3 bucket");
		System.out.println("8 = move object from one bucket to another bucket");
		System.out.println("9 = copy object from one bucket to another bucket");
		System.out.println("10 = open object of bucket using presignedURL");
		System.out.println("11 = create static website from bucket");
		System.out.println("12 = get bucket policy");
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
				case 1: // list all s3 bucket
					AWSHelper.allS3Bucket();
					break;
				case 2: // describe s3 bucket
					AWSHelper.describeS3Bucket(sc);
					break;
				case 3: // upload object on s3 bucket
					AWSHelper.uploadObjectOnS3(sc);
					break;
				case 4: // create new s3 bucket
					AWSHelper.createS3Bucket(sc);
					break;
				case 5: // download object from s3 bucket
					AWSHelper.downloadObjectFromS3(sc);
					break;
				case 6: // delete object from s3 bucket
					AWSHelper.deleteObjectFromS3(sc);
					break;
				case 7: // delete s3 bucket
					AWSHelper.deleteS3Bucket(sc);
					break;
				case 8: // move object from one bucket to another bucket
					AWSHelper.moveObject(sc);
					break;
				case 9: // copy object from one bucket to another bucket
					AWSHelper.copyObject(sc);
					break;
				case 10: // open object of bucket using presignedURL
					AWSHelper.generatePresignedUrl(sc);
					break;
				case 11: // create static website from bucket
					AWSHelper.createWebConfiguration(sc);
				case 12: // get bucket policy
					AWSHelper.getBucketPolicy(sc);
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
