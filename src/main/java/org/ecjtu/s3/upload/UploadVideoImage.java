package org.ecjtu.s3.upload;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;

@SuppressWarnings("Duplicates")
public class UploadVideoImage {

    private static final String S3_IMAGE_FORMAT = "%s_image_%s.png";
    private static final String BUCKET_NAME = "fleshbucketimage";

    public static void main(String[] args) {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setProtocol(Protocol.HTTP);
        AmazonS3 s3 = new AmazonS3Client(new DefaultAWSCredentialsProviderChain(), configuration);
        Region usWest2 = Region.getRegion(Regions.CN_NORTH_1);
        s3.setRegion(usWest2);
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName("fleshbucketimage"));
        File file = new File("res\\videoImage");
        File[] fileList = file.listFiles();
        if (fileList != null) {
            loop:
            for (int i = 0; i < fileList.length; i++) {
                File f = fileList[i];
                String name = f.getName();
                String key = String.format(S3_IMAGE_FORMAT, BUCKET_NAME, name);
                for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {
                    if (summary.getKey().equals(key)) {
                        continue loop;
                    }
                }
                try {
                    s3.putObject(BUCKET_NAME, key, f);
                } catch (Exception e) {
                    e.printStackTrace();
                    i--;
                    continue;
                }
            }
        }

    }
}
