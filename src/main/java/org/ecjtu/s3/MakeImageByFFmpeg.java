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

import java.io.*;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MakeImageByFFmpeg {

    private static final String FFMPEG_PATH = "C:\\Users\\KerriGan\\Desktop\\ffmpeg-20170821-d826951-win64-static\\ffmpeg-20170821-d826951-win64-static\\bin\\ffmpeg.exe";
    private static final String BUCKET_NAME = "xkorean";
    private static final String KEY_FORMAT = "%s_image_%s.png";
    public static void main(String[] args) {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setProtocol(Protocol.HTTP);
        AmazonS3 s3 = new AmazonS3Client(new DefaultAWSCredentialsProviderChain(), configuration);
        Region usWest2 = Region.getRegion(Regions.CN_NORTH_1);
        s3.setRegion(usWest2);
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(BUCKET_NAME));
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.HOUR, 1);
        File file = new File("res\\videoImage");
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        List<S3ObjectSummary> summaryList = objectListing.getObjectSummaries();
        for (int i = 0; i < summaryList.size(); i++) {
            S3ObjectSummary objectSummary = summaryList.get(i);
            System.out.println(" - " + objectSummary.getKey());
//            boolean flag =false;
//            try {
//                s3.getObject("fleshbucketimage", objectSummary.getKey());
//            }catch (Exception e){
//                e.printStackTrace();
//                flag = true;
//            }
//            if(!flag){
//                continue;
//            }
            String key = String.format(KEY_FORMAT,BUCKET_NAME,objectSummary.getKey());
            File image = new File("res\\videoImage\\" + key);
            if(image.exists()){
                continue;
            }
            String url = s3.generatePresignedUrl(BUCKET_NAME, objectSummary.getKey(), endDate.getTime()).toString();
            Process process = null;
            BufferedReader reader = null;
            ProcessBuilder builder = new ProcessBuilder();
            String[] argument = new String[]{FFMPEG_PATH, "-y"/*, "-ss", "300"*//*, "-t", "20"*/, "-i", "\"" + url + "\"", "-f", "image2", "-s", "320x240", "res\\videoImage\\" + key };
            try {
                String str = "";
                for (String c : argument) {
                    str += c;
                    str += " ";
                }
                System.out.println("command " + str);
                builder.redirectErrorStream(true);
                builder.command(argument);
                process = builder.start();
                InputStream is = process.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                process.waitFor(20, TimeUnit.SECONDS);
                process.destroyForcibly();
            } catch (Exception e) {
                e.printStackTrace();
                i--;
            } finally {
                if (process != null) {
                    process.destroyForcibly();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

}
