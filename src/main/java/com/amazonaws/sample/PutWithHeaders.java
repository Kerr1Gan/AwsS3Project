package com.amazonaws.sample;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import java.io.File;

public class PutWithHeaders {

    public static void main(String[] args) {
        AmazonS3 s3 = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
        Region usWest2 = Region.getRegion(Regions.CN_NORTH_1);
        s3.setRegion(usWest2);
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");
        TransferManager tm = new TransferManager(s3);
        // TransferManager processes all transfers asynchronously,
        // so this call will return immediately.
        // 上传失败可重新使用PutObjectRequest请求再次上传，上传成功的将不再重新上传，失败的则重新上传
        PutObjectRequest request = new PutObjectRequest("firststorage0001", "key01", new File("build.gradle"));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader("header-test", "tttttttttttt"); // 无法在s3上保存下来，只能通过UserMetaData才能保存下来
        metadata.addUserMetadata("header-test", UriParser.encode("能够保存中文吗")); // 能够在s3上保存下来，但是不能保存中文，需要通过Url转码
        metadata.addUserMetadata("header-test2", "tttttttttttttt2");
        request.setMetadata(metadata);
        Upload upload = tm.upload(request);
        try {
            // Or you can block and wait for the upload to finish
            upload.waitForCompletion();
            System.out.println("Upload complete.");
        } catch (AmazonClientException amazonClientException) {
            System.out.println("Unable to upload file, upload was aborted.");
            amazonClientException.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        S3Object obj = s3.getObject(new GetObjectRequest("firststorage0001", "key01"));
        UriParser.decode(obj.getObjectMetadata().getUserMetadata().get("header-test"));
        System.out.println("Listing objects");
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName("firststorage0001"));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + "  " +
                    "(size = " + objectSummary.getSize() + ")");
        }
    }
}
