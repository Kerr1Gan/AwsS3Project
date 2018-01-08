package org.ecjtu.s3.upload;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import org.ecjtu.s3.UriParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.ecjtu.s3.Constants.RES_PATH;

public class UploadFile2S3 {

    /**
     * [
     * {
     * "name": "xxx.mp4",
     * "url": "C:\\download\\xxx.mp4"
     * },
     * {
     * "name": "xxx.mp4",
     * "url": "C:\\download\\xxx.mp4"
     * },
     * {
     * "name": "xxx.mp4",
     * "url": "C:\\download\\xxx.mp4"
     * }
     * ]
     */

    private static List<UploadModel> sUploadList = null;
    private static final String BUCKET_NAME = "firststorage0001";

    public static void main(String[] args) throws IOException {
        File cache = new File(RES_PATH, "tmp");
        if (!cache.exists()) {
            sUploadList = resolveJson(new File(RES_PATH, "files.json"));
        } else {
            readCache(cache);
        }
        AmazonS3 s3 = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
        Region usWest2 = Region.getRegion(Regions.CN_NORTH_1);
        s3.setRegion(usWest2);
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");
        TransferManager tm = new TransferManager(s3);

        checkFromS3(s3, BUCKET_NAME, sUploadList);
        Iterator<UploadModel> iterator = sUploadList.iterator();

        while (iterator.hasNext()) {
            UploadModel item = iterator.next();
            if (item.getHasFinished()) {
                continue;
            }
            // TransferManager processes all transfers asynchronously,
            // so this call will return immediately.
            // 上传失败可重新使用PutObjectRequest请求再次上传，上传成功的将不再重新上传，失败的则重新上传
            final PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, item.getName(), new File(item.getUrl()));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.addUserMetadata("name", UriParser.encode(item.getName()));
            request.setMetadata(metadata);
            final long[] num = new long[3];
            num[0] = new File(item.getUrl()).length();
            request.setGeneralProgressListener(event -> {
                if (event.getEventType() == ProgressEventType.TRANSFER_FAILED_EVENT) {
                    System.out.println(ProgressEventType.TRANSFER_FAILED_EVENT.toString());
                }
                num[2] += event.getBytesTransferred();
                if (System.currentTimeMillis() - num[1] > 5 * 1000) {
                    System.out.println("percent " + ((num[2] * 1.0f) / (num[0] * 1.0f) * 100) + "%" +
                            " url " + item.getUrl() +
                            " transferredBytes " + event.getBytesTransferred() +
                            " totalBytes " + event.getBytes() +
                            " eventType " + event.getEventType().toString());
                    num[1] = System.currentTimeMillis();
                }
            });
            Upload upload = tm.upload(request);
            try {
                // Or you can block and wait for the upload to finish
                upload.waitForCompletion();
                System.out.println("Upload complete." + item.getUrl());
                item.setHasFinished(true);
                writeCache(cache, sUploadList);

                publishObject(s3, BUCKET_NAME, item.getName());
            } catch (AmazonClientException amazonClientException) {
                System.out.println("Unable to upload file, upload was aborted.");
                amazonClientException.printStackTrace();
                item.setHasFinished(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
                item.setHasFinished(false);
            }
        }
        writeCache(cache, sUploadList);
        tm.shutdownNow(true);
    }

    @SuppressWarnings("unchecked")
    public static boolean readCache(File f) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(f))) {
            sUploadList = (List<UploadModel>) input.readObject();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void writeCache(File file, Object obj) {
        if (obj == null) return;
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<UploadModel> resolveJson(File jsonFile) {
        List<UploadModel> ret = new ArrayList<>();
        try (FileInputStream input = new FileInputStream(jsonFile)) {
            byte[] bytes = new byte[1024 * 1024];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int len = 0;
            while ((len = input.read(bytes)) > 0) {
                os.write(bytes, 0, len);
            }
            JSONArray array = new JSONArray(os.toString("utf-8"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");
                String url = obj.getString("url");
                ret.add(new UploadModel(name, url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void checkFromS3(AmazonS3 s3, String bucketName, List<UploadModel> list) {
        System.out.println("Listing objects");
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(bucketName));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String key = objectSummary.getKey();
            for (UploadModel model : list) {
                if (model.getName().equals(key)) {
                    System.out.println(objectSummary.getKey() + " exists " +
                            "(size = " + objectSummary.getSize() + ")");
                    model.setHasFinished(true);
                }
            }
        }
    }

    public static void publishObject(AmazonS3 s3, String bucketName, String key) {
        AccessControlList controlList = s3.getObjectAcl(bucketName, key);
        controlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        s3.setObjectAcl(bucketName, key,
                controlList);
    }
}
