package com.amazonaws.sample;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.util.Set;

public class ListBucketsObject {

    public static void main(String[] args) {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setProtocol(Protocol.HTTP);
        AmazonS3 s3 = new AmazonS3Client(new DefaultAWSCredentialsProviderChain(),configuration);
        Region usWest2 = Region.getRegion(Regions.CN_NORTH_1);
        s3.setRegion(usWest2);
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");
        System.out.println("Listing objects");
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName("fleshbucketimage"));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.print(" - " + objectSummary.getKey() + "  " +
                    "(size = " + objectSummary.getSize() + ")");
            AccessControlList controlList = s3.getObjectAcl(objectSummary.getBucketName(), objectSummary.getKey());
            Set<Grant> grants = controlList.getGrants();
            System.out.print(" permission ");
            for (Grant grant : grants) {
                System.out.print(grant.getGrantee().toString());
                Permission permission = grant.getPermission();
                System.out.print(" " + permission.getHeaderName() + " " + permission.toString()+" ");
            }
            System.out.println("");

            // 设置资源为所有用户均能访问
//            controlList.revokeAllPermissions(GroupGrantee.AllUsers);
            controlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            s3.setObjectAcl(objectSummary.getBucketName(), objectSummary.getKey(),
                    controlList);
        }
    }
}
