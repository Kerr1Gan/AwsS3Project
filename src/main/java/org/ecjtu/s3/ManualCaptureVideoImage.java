package org.ecjtu.s3;

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
import java.util.ArrayList;
import java.util.List;

public class ManualCaptureVideoImage {
    private static final String[] BUCKET_NAME = new String[]{"xkorean", "firststorage0001"};
    private static final String KEY_FORMAT = "%s_image_%s.png";

    public static void main(String[] args) {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setProtocol(Protocol.HTTP);
        AmazonS3 s3 = new AmazonS3Client(new DefaultAWSCredentialsProviderChain(), configuration);
        Region usWest2 = Region.getRegion(Regions.CN_NORTH_1);
        s3.setRegion(usWest2);
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");
        List<S3ObjectSummary> summaryList = new ArrayList<>();
        for (String bucket : BUCKET_NAME) {
            ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                    .withBucketName(bucket));
            List<S3ObjectSummary> inner = objectListing.getObjectSummaries();
            summaryList.addAll(inner);
        }
        File root = new File("res\\videoImage");
        File[] fileList = root.listFiles();
        if (fileList == null) {
            return;
        }
        for (int i = 0; i < summaryList.size(); i++) {
            S3ObjectSummary objectSummary = summaryList.get(i);
            String key = objectSummary.getKey();
            boolean flag = true;
            for (File child : fileList) {
                if (child.getName().contains(key)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                System.out.println("manual capture " + key);
            }
        }
    }
}
