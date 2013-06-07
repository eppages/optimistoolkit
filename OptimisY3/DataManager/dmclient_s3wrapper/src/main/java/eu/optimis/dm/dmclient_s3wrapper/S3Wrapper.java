package eu.optimis.dm.dmclient_s3wrapper;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.TransferManager;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author oriol.collell
 */
public class S3Wrapper {

    // Time (in milis) within which the generated URL for an image is valid
    private static int OBJECT_URL_VALIDITY = 8*60*60*1000; // 8 hours

    private Transfer transfer;
    private int progress = 0;

    private class TransferProgressListener implements ProgressListener {

        public void progressChanged(ProgressEvent progressEvent) {
            if (transfer == null) {
                return;
            }
            progress = (int) (transfer.getProgress().getPercentTransfered());

            switch (progressEvent.getEventCode()) {
            case ProgressEvent.COMPLETED_EVENT_CODE:
                progress = 100;
                System.out.println("Upload complete successfully!!");
                break;
            case ProgressEvent.FAILED_EVENT_CODE:
                progress = -1;
                try {
                    AmazonClientException e = transfer.waitForException();
                    printClientException(e);
                } catch (InterruptedException e) {}
                break;
            }
        }
    }

    public static String BUCKET_PREFIX = "euntuaoptimis";
    private static TransferManager tx;

    private String publicKey;
    private String secretKey;

    public S3Wrapper(String publicKey, String secretKey) {
        this.publicKey = publicKey;
        this.secretKey = secretKey;

        tx = new TransferManager(new BasicAWSCredentials(publicKey, secretKey));
    }

    public void terminate()
    {
     if( tx != null )
         tx.shutdownNow();
    }

    public URL uploadImage(String serviceId, String image) throws Exception {
        AmazonS3 s3 = getAmazonS3Client();
        String bucketName = getBucketName(serviceId);
        String key = "";
        try {

            System.out.println("Owner = " + s3.getS3AccountOwner());

            System.out.println("@Bucket exits?: " + bucketName + " -> " + (s3.doesBucketExist(bucketName) ? "exists" : "no") );

            if (!s3.doesBucketExist(bucketName)) {
                System.out.println("Creating bucket " + bucketName + "...");
                s3.createBucket(bucketName, Region.EU_Ireland);
            }

            File fileToUpload = new File(image);
            if (!fileToUpload.exists()) {
                throw new Exception("The specified file does not exists");
            }
            System.out.println("Uploading file...");
            key = fileToUpload.getName();
            PutObjectRequest req = new PutObjectRequest(bucketName, key, fileToUpload)
                    .withProgressListener(new TransferProgressListener());
            transfer = tx.upload(req);
        } catch (AmazonServiceException se) {
            printServiceException(se);
        } catch (AmazonClientException ce) {
           printClientException(ce);
        } catch (SecurityException e) {
            System.out.println("[ERROR] Exception when trying to read the file: " + e.getMessage());
        } catch(Exception e) {
            System.out.println("[ERROR]: " + e.toString());
        }
        
        Date d = new Date();
        d.setTime(d.getTime() + OBJECT_URL_VALIDITY);
        return s3.generatePresignedUrl(bucketName, key, d);
    }
    
    public void downloadImage(String serviceId, String imageName, String destPath) throws Exception {
        AmazonS3 s3 = getAmazonS3Client();
        String bucketName = getBucketName(serviceId);
        try {
            if (!s3.doesBucketExist(bucketName)) {
                throw new Exception("Image file not found!");
            }
            File downloadPath = new File(destPath);
            System.out.println("Downloading file...");
            GetObjectRequest req = new GetObjectRequest(bucketName, imageName).withProgressListener(
                    new TransferProgressListener());
            transfer = tx.download(req, downloadPath);
        } catch (AmazonServiceException se) {
            printServiceException(se);
        } catch (AmazonClientException ce) {
            printClientException(ce);
        } catch (SecurityException e) {
            System.out.println("[ERROR] Exception when trying to read the download path: " + e.getMessage());
        }
    }
    
    public List<String> listImages(String serviceId) {        
        List<String> res = new LinkedList<String>();
        AmazonS3 s3 = getAmazonS3Client();
        String bucketName = getBucketName(serviceId);
        try {
            if (s3.doesBucketExist(bucketName)) {
                System.out.println("Listing files in bucket...");
                
                ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName);
                ObjectListing objectListing;
                do {
                    objectListing = s3.listObjects(lor);
                    for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                        res.add(objectSummary.getKey());
                    }
                    lor.setMarker(objectListing.getNextMarker());
                } while (objectListing.isTruncated());
            }
        } catch (AmazonServiceException se) {
            printServiceException(se);
        } catch (AmazonClientException ce) {
            printClientException(ce);
        }
        return res;
    }
    
    public void deleteImage(String serviceId, String imageName) throws Exception {
        AmazonS3 s3 = getAmazonS3Client();
        String bucketName = getBucketName(serviceId);
        try {
            if (!s3.doesBucketExist(bucketName)) {
                throw new Exception("File does not exist");
            }
            
            System.out.println("Deleting file...");  
            DeleteObjectRequest dor = new DeleteObjectRequest(bucketName, imageName);
            s3.deleteObject(dor);
        } catch (AmazonServiceException se) {
            printServiceException(se);
        } catch (AmazonClientException ce) {
            printClientException(ce);
        }
    }
    
    /** 
     * 
     * @returns the percentage of progress completed (0-100) or -1 if an exception was thrown
     */
    public int getProgress() {
        return progress;
    }
    
    
    private AmazonS3 getAmazonS3Client() {
        System.out.println("[S3 Wrapper] Getting Amazon S3 Client...");
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(publicKey, secretKey)); 
        return s3;
    }
    
    private String getBucketName(String serviceId) {
        // The name is valid since the service_id is a 15 characters lenght UUID, thus complies
        // with all naming rules requried for bucket names
        return S3Wrapper.BUCKET_PREFIX + serviceId.toLowerCase();
    }
    
    private void printServiceException(AmazonServiceException se) {
        System.out.println("[ERROR] Service Exception when processing the request: " + se.getMessage()
                       + "(HTTP Code: " + se.getStatusCode() + " - AWS Code: " + se.getErrorCode()
                       + " - Error Type: " + se.getErrorType() + " )");
    }
    
    private void printClientException(AmazonClientException ce) {
        System.out.println("[ERROR] Client Exception when processing the request: " + ce.getMessage());
    }
}
