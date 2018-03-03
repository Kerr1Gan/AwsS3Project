package org.ecjtu.s3;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MakeImageByFFmpeg {

    public static void main(String[] args) {
        AmazonS3 s3 = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
        Region usWest2 = Region.getRegion(Regions.CN_NORTH_1);
        s3.setRegion(usWest2);
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName("firststorage0001"));
        Runtime runtime = Runtime.getRuntime();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.HOUR, 1);
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.print(" - " + objectSummary.getKey());
            String url = s3.generatePresignedUrl("firststorage0001", objectSummary.getKey(), endDate.getTime()).toString();
            String key = "image_" + objectSummary.getKey();
            try {
                runtime.exec(String.format("ffmpeg -y -ss 300 -t 5 -i %s -vf \"select=eq(pict_type\\\\,I)\" -vframes 1 -f image2 -s 250x140 %s", url, key));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
