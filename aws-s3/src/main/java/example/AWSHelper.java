package example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeletePublicAccessBlockRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PublicAccessBlockConfiguration;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.SetPublicAccessBlockRequest;

public class AWSHelper {

	private static final String REGION = "ap-south-1";      // Region name
	  private static final int MAX_BUFFER_SIZE = 1024*1000;  // Maximum buffer size for the file

	public static void allS3Bucket() {
		// Instantiates a client
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withRegion(REGION).build();

        try {
            System.out.println("Listing S3 buckets and objects ...");
            // List Buckets
            List<Bucket> buckets = s3client.listBuckets();
            System.out.println("Your Amazon S3 buckets:");
            String bucketName;
            for (Bucket b : buckets) {
                bucketName = b.getName();
                System.out.println("* Bucket: " + bucketName);
                ObjectListing objectListing = s3client.listObjects(new ListObjectsRequest()
                        .withBucketName(bucketName));
                // List Objects
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    System.out.println("  - Object: " + objectSummary.getKey() + "  " +
                            "(size = " + objectSummary.getSize() + ")");
                }
            }
            System.out.println("Listed");
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        s3client.shutdown();
	}
	
	public static void describeS3Bucket(Scanner sc) {
		// The name for the bucket
        System.out.println("enter bucket name: ");
        String bucketName = sc.next();

        System.out.println("Bucket name: " + bucketName);

        // Instantiates a client
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withRegion(REGION).build();

        try {
            if (s3client.doesBucketExistV2(bucketName)) {
                System.out.println("Listing objects ...");
                // List objects in a Bucket
                ObjectListing objectListing = s3client.listObjects(new ListObjectsRequest()
                        .withBucketName(bucketName));
                
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    System.out.println(" - " + objectSummary.getKey() + "  " +
                            "(size = " + objectSummary.getSize() + ")");
                }
                System.out.println("Listed");
            } else {
                System.out.println("Error: Bucket does not exist!!");
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        s3client.shutdown();
	}
	
	public static void uploadObjectOnS3(Scanner sc) {
		String bucketName;              // Bucket name
        String keyName;                 // Key name, it is the object name
        String localFileName;           // Upload local file name

        System.out.println("enter bucket name: ");
        bucketName = sc.next();
        System.out.println("enter local file name");
        localFileName = sc.next();
        keyName = localFileName.substring(localFileName.lastIndexOf("\\")+1);
        System.out.println("Bucket:     " + bucketName);
        System.out.println("Object/Key: " + keyName);
        System.out.println("Local file: " + localFileName);

        // Instantiates a client
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withRegion(REGION).build();

        try {
            System.out.println("Uploading an object to S3 from a file ...");

            // Get local file
            File file = new File(localFileName);
            if (file.exists()) {
                // Upload object
                s3client.putObject(new PutObjectRequest(
                        bucketName, keyName, file));

                System.out.println("Uploaded");
                
                java.util.Date expiration = new java.util.Date();
	            long expTimeMillis = Instant.now().toEpochMilli();
	            expTimeMillis += 1000 * 60 * 60;
	            expiration.setTime(expTimeMillis);

	            // Generate the presigned URL.
	            System.out.println("Generating pre-signed URL.");
	            GeneratePresignedUrlRequest generatePresignedUrlRequest =
	                    new GeneratePresignedUrlRequest(bucketName, keyName)
	                            .withMethod(HttpMethod.GET)
	                            .withExpiration(expiration);
	            URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
	            String presignedURL = url.toString();
	            
	            System.out.println("Pre-Signed URL: " +presignedURL );
	            System.out.println("Object URL: "+presignedURL.substring(0, presignedURL.indexOf("?")));
            } else {
                System.out.printf("Error: Local file \"%s\" does NOT exist.", localFileName);
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error " +
                    "response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } 
        
        catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to " +
                    " communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        s3client.shutdown();
	}
	
	public static void createS3Bucket(Scanner sc) {
		// The name for the new bucket
        System.out.println("enter bucket name: ");
        String bucketName = sc.next();

        System.out.println("Bucket name: " + bucketName);

        // Instantiates a client
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withRegion(REGION).build();

        try {
            if (!s3client.doesBucketExistV2(bucketName)) {
                System.out.println("Creating bucket ...");

                s3client.createBucket(new CreateBucketRequest(
                        bucketName));
                
                //set block public access as false which is by default true
                s3client.setPublicAccessBlock(new SetPublicAccessBlockRequest()
                		.withBucketName(bucketName)
                		.withPublicAccessBlockConfiguration(new PublicAccessBlockConfiguration()
                				.withBlockPublicAcls(false)
                				.withIgnorePublicAcls(false)
                				.withBlockPublicPolicy(false)
                				.withRestrictPublicBuckets(false)));
                       
                
                //set bucket policy
                String bucketPolicy = "{" +
                        "  \"Version\": \"2012-10-17\"," +
                        "  \"Statement\": [" +
                        "    {" +
                        "      \"Effect\": \"Allow\"," +
                        "      \"Principal\": \"*\"," +
                        "      \"Action\": \"s3:GetObject\"," +
                        "      \"Resource\": \"arn:aws:s3:::" + bucketName + "/*\"" +
                        "    }" +
                        "  ]" +
                        "}";

                // Set the bucket policy to allow public access
                s3client.setBucketPolicy(bucketName, bucketPolicy);
                System.out.println("Created");

                // Get Bucket location
                String bucketLocation = s3client.getBucketLocation(new GetBucketLocationRequest(bucketName));
                System.out.println("Bucket location: " + bucketLocation);
            } else {
                System.out.println("Error: Bucket already exists!!");
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        s3client.shutdown();
	}
	

	public static void downloadObjectFromS3(Scanner sc) {
	        System.out.println("enter bucket name");
	        String bucketName    = sc.next();
	        System.out.println("enter key name");
	        String keyName       = sc.next();
	        System.out.println("enter a local file path ");
	        String localFileName = sc.next();

	        // Instantiates a client
	        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
	                .withRegion(REGION).build();

	        try {
	            System.out.println("Downloading an object from a S3 to a local file ...");

	            // Get object
	            S3Object s3object = s3client.getObject(bucketName, keyName);

	            // Get content object
	            S3ObjectInputStream inputStream = s3object.getObjectContent();

	            // Write content to the file
	            FileOutputStream fileOutputStream = new FileOutputStream(new File(localFileName));
	            byte[] readBuffer = new byte[MAX_BUFFER_SIZE];
	            int readLen = 0;
	            while ((readLen = inputStream.read(readBuffer)) > 0) {
	                fileOutputStream.write(readBuffer, 0, readLen);
	            }
	            fileOutputStream.close();
	            inputStream.close();
	            System.out.println("Downloaded");
	        } catch (IOException e) {
	            System.err.println(e.getMessage());
	        } catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, " +
	                    "which means your request made it " +
	                    "to Amazon S3, but was rejected with an error " +
	                    "response for some reason.");
				System.out.println("Error Message:    " + ase.getMessage());
				System.out.println("HTTP Status Code: " + ase.getStatusCode());
				System.out.println("AWS Error Code:   " + ase.getErrorCode());
				System.out.println("Error Type:       " + ase.getErrorType());
				System.out.println("Request ID:       " + ase.getRequestId());
	        } catch (AmazonClientException ace) {
				System.out.println("Caught an AmazonClientException, " + "which means the client encountered "
						+ "an internal error while trying to " + " communicate with S3, "
						+ "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
	        s3client.shutdown();
	}
	
	public static void deleteObjectFromS3(Scanner sc) {
		// The name for the bucket
    	System.out.println("enter bucket name");
        String bucketName = sc.next();

        // The name for the object
    	System.out.println("enter object name");
        String keyName = sc.next();

        System.out.println("Bucket name: " + bucketName);
        System.out.println("Object name: " + keyName);

        // Instantiates a client
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withRegion(REGION).build();

        try {
            boolean exists = s3client.doesObjectExist(bucketName, keyName);
            if (exists) {
                System.out.println("Deleting object ...");

                // Delete Object
                DeleteObjectRequest deleteObjRequest = new DeleteObjectRequest(
                        bucketName, keyName);
                s3client.deleteObject(deleteObjRequest);

                System.out.println("Deleted");
            }
            else {
                System.out.println("Error: Bucket/Object \"" + bucketName + "/" + keyName + "\" does not exist!!");
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        s3client.shutdown();
	}
	
	public static void deleteS3Bucket(Scanner sc) {
		System.out.println("enter bucket name");
        String bucketName = sc.next();

        System.out.println("Bucket name: " + bucketName);

        // Instantiates a client
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withRegion(REGION).build();

        try {
            if (s3client.doesBucketExistV2(bucketName)) {
                System.out.println("Deleting bucket ...");
                // Delete bucket
                s3client.deleteBucket(bucketName);
                System.out.println("Deleted");
            } else {
                System.out.println("Error: Bucket does not exist!!");
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } 
        
        catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        s3client.shutdown();
	}
	
	public static void moveObject(Scanner sc) {
		 String sourceBucketName;       // Source bucket name
	        String sourceKey;              // Source key
	        String destinationBucketName;  // Destination bucket name
	        String destinationKey;         // Destination key

	        System.out.println("enter source bucket name");
	        sourceBucketName      = sc.next();
	        System.out.println("enter source object name");
	        sourceKey             = sc.next();
	        System.out.println("enter destination bucket name");
	        destinationBucketName = sc.next();
	        destinationKey        = sourceKey;

	        System.out.println("From - bucket: " + sourceBucketName);
	        System.out.println("From - object: " + sourceKey);
	        System.out.println("To   - bucket: " + destinationBucketName);
	        System.out.println("To   - object: " + destinationKey);

	        // Instantiates a client
	        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
	                .withRegion(REGION).build();

	        try {
	            System.out.println("Moving object ...");

	            // Copy object
	            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
	                    sourceBucketName, sourceKey, destinationBucketName, destinationKey);
	            s3client.copyObject(copyObjRequest);

	            // Delete Object
	            DeleteObjectRequest deleteObjRequest = new DeleteObjectRequest(
	                    sourceBucketName, sourceKey);
	            s3client.deleteObject(deleteObjRequest);
	            //s3client.deleteObject(SourceBucketName, SourceKey);

	            System.out.println("Moved");

	        } 
	        
	        catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, " +
	                    "which means your request made it " +
	                    "to Amazon S3, but was rejected with an error " +
	                    "response for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } 
	        
	        catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, " +
	                    "which means the client encountered " +
	                    "an internal error while trying to " +
	                    " communicate with S3, " +
	                    "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
	        s3client.shutdown();
	}

	public static void copyObject(Scanner sc) {
	        System.out.println("enter a source bucket name");
	        String sourceBucketName      = sc.next();
	        System.out.println("enter a source object name");
	        String sourceKey             = sc.next();
	        System.out.println("enter destination bucket name");
	        String destinationBucketName = sc.next();
	        String destinationKey        = sourceKey;

	        // Instantiates a client
	        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
	                .withRegion(REGION).build();

	        try {
	            System.out.println("Copying object ...");

	            // Copy object
	            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
	                    sourceBucketName, sourceKey, destinationBucketName, destinationKey);
	            s3client.copyObject(copyObjRequest);

	            System.out.println("Copied");

	        } catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, " +
	                    "which means your request made it " +
	                    "to Amazon S3, but was rejected with an error " +
	                    "response for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } 
	        
	        catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, " +
	                    "which means the client encountered " +
	                    "an internal error while trying to " +
	                    " communicate with S3, " +
	                    "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
	        s3client.shutdown();
	    }

	public static void generatePresignedUrl(Scanner sc) {
		 String clientRegion = "us-east-2";
		 System.out.println("enter a bucket name");
	        String bucketName = sc.next();
	        System.out.println("enter a object name");
	        String objectKey = sc.next();

	        try {
	            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	                    .withRegion(clientRegion)
	                    .withCredentials(new ProfileCredentialsProvider())
	                    .build();

	            // Set the presigned URL to expire after one hour.
	            java.util.Date expiration = new java.util.Date();
	            long expTimeMillis = Instant.now().toEpochMilli();
	            expTimeMillis += 1000 * 60 * 60;
	            expiration.setTime(expTimeMillis);

	            // Generate the presigned URL.
	            System.out.println("Generating pre-signed URL.");
	            GeneratePresignedUrlRequest generatePresignedUrlRequest =
	                    new GeneratePresignedUrlRequest(bucketName, objectKey)
	                            .withMethod(HttpMethod.GET)
	                            .withExpiration(expiration);
	            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
	            String presignedURL = url.toString();
	            
	            System.out.println("Pre-Signed URL: " +presignedURL );
	            System.out.println("Object URL: "+presignedURL.substring(0, presignedURL.indexOf("?")));
	        } catch (AmazonServiceException e) {
	            // The call was transmitted successfully, but Amazon S3 couldn't process 
	            // it, so it returned an error response.
	            e.printStackTrace();
	        } catch (SdkClientException e) {
	            // Amazon S3 couldn't be contacted for a response, or the client
	            // couldn't parse the response from Amazon S3.
	            e.printStackTrace();
	        }
		
	}

	public static void createWebConfiguration(Scanner sc) {
		 String clientRegion = "ap-south-1";
		 System.out.println("enter bucket name");
	        String bucketName = sc.next();
	        System.out.println("enter .html file");
	        String indexDocName = sc.next();
//	        String errorDocName = "*** Error document name ***";

	        try {
	            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	                    .withRegion(clientRegion)
	                    .withCredentials(new ProfileCredentialsProvider())
	                    .build();

	            // Print the existing website configuration, if it exists.
	            printWebsiteConfig(s3Client, bucketName);

	            // Set the new website configuration.
	            s3Client.setBucketWebsiteConfiguration(bucketName, new BucketWebsiteConfiguration(indexDocName));

	            // Verify that the configuration was set properly by printing it.
	            printWebsiteConfig(s3Client, bucketName);

	            // Delete the website configuration.
//	            s3Client.deleteBucketWebsiteConfiguration(bucketName);

	            // Verify that the website configuration was deleted by printing it.
//	            printWebsiteConfig(s3Client, bucketName);
	        } catch (AmazonServiceException e) {
	            // The call was transmitted successfully, but Amazon S3 couldn't process 
	            // it, so it returned an error response.
	            e.printStackTrace();
	        } catch (SdkClientException e) {
	            // Amazon S3 couldn't be contacted for a response, or the client
	            // couldn't parse the response from Amazon S3.
	            e.printStackTrace();
	        }		
	}
	
	 private static void printWebsiteConfig(AmazonS3 s3Client, String bucketName) {
	        System.out.println("Website configuration: ");
	        BucketWebsiteConfiguration bucketWebsiteConfig = s3Client.getBucketWebsiteConfiguration(bucketName);
	        if (bucketWebsiteConfig == null) {
	            System.out.println("No website config.");
	        } else {
	            System.out.println("Index doc: " + bucketWebsiteConfig.getIndexDocumentSuffix());
	            System.out.println("Error doc: " + bucketWebsiteConfig.getErrorDocument());
	        }
	    }

	public static void getBucketPolicy(Scanner sc) {
//		final String USAGE = "\n" +
//                "Usage:\n" +
//                "    GetBucketPolicy <bucket>\n\n" +
//                "Where:\n" +
//                "    bucket - the bucket to get the policy from.\n\n" +
//                "Example:\n" +
//                "    GetBucketPolicy testbucket\n\n";
		System.out.println("enter bucket name");
        String bucket_name = sc.next();
        String policy_text = null;
        System.out.format("Getting policy for bucket: \"%s\"\n\n", bucket_name);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(REGION).build();
        try {
            BucketPolicy bucket_policy = s3.getBucketPolicy(bucket_name);
            policy_text = bucket_policy.getPolicyText();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        if (policy_text == null) {
            System.out.println("The specified bucket has no bucket policy.");
        } else {
            System.out.println("Returned policy:");
            System.out.println("----");
            System.out.println(policy_text);
            System.out.println("----\n");
        }
        System.out.println("Done!");		
	}
	
}
