package com.amazonaws.sample;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import java.io.File;

public class UploadObjectMultipartUploadUsingHighLevelAPI {
    public static void main(String[] args) throws Exception {
        String existingBucketName = "firststorage0001";
        String keyName = "key01";
        String filePath = "build.gradle";

        TransferManager tm = new TransferManager(new ProfileCredentialsProvider());
        System.out.println("Hello");
        // TransferManager processes all transfers asynchronously,
        // so this call will return immediately.
        // 上传失败可重新使用PutObjectRequest请求再次上传，上传成功的将不再重新上传，失败的则重新上传
        Upload upload = tm.upload(
                existingBucketName, keyName, new File(filePath));
        System.out.println("Hello2");
        try {
            // Or you can block and wait for the upload to finish
            upload.waitForCompletion();
            System.out.println("Upload complete.");
        } catch (AmazonClientException amazonClientException) {
            System.out.println("Unable to upload file, upload was aborted.");
            amazonClientException.printStackTrace();
        }
    }
}
