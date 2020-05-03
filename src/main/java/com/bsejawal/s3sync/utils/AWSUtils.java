package com.bsejawal.s3sync.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.bsejawal.s3sync.enums.AWSCred;
import com.bsejawal.s3sync.model.FileInfo;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Set;

public class AWSUtils {
    private static final Logger LOG = Logger.getLogger(AWSUtils.class);

    /**
     *
     * @param cred
     * @return
     */
    public static AmazonS3 getAmazonS3(AWSCred cred) {

        AWSCredentials credentials = new BasicAWSCredentials(cred.getAccessKey(), cred.getSecretKey());
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(cred.getDefaultRegion())
                .build();
        return s3Client;
    }
    /**
     *
     * @param env
     * @return
     */
    public  static Set<FileInfo> s3FileInfos(String env, String prefix, Set<FileInfo> s3FileInfos){
        if(null==env || env.isEmpty())
            env = AWSCred.PRODUCTION.getEnv();
        AWSCred cred = AWSCred.getByEnv(env);

        if(null==prefix || prefix.isEmpty())
            return null;
        AmazonS3 s3 = getAmazonS3(cred);
        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest()
                        .withBucketName(cred.getBucket())
                        .withPrefix(prefix);
        ObjectListing objects = s3.listObjects(listObjectsRequest);
        for (; ;) {
            List<S3ObjectSummary> summaries = objects.getObjectSummaries();
            if (summaries.size() < 1) {
                break;
            }
            for (S3ObjectSummary s : summaries){
                FileInfo fileInfo = new FileInfo();
                String fileName = s.getKey();
                if(fileName.endsWith("/"))
                    continue;
                if(fileName.contains("/")) {
                    String sp[] = fileName.split("/");
                    fileName = sp[sp.length - 1];
                }
                fileInfo.setName(fileName);
                fileInfo.setRelativePath(s.getKey().substring(prefix.length()).replace(fileName,""));
                fileInfo.setAbsolutePath(s.getBucketName()+"/"+s.getKey());
                fileInfo.setSize(s.getSize());
                fileInfo.setLastModified(s.getLastModified());
                s3FileInfos.add(fileInfo);
            }
            objects = s3.listNextBatchOfObjects(objects);
        }
        return s3FileInfos;
    }

    public static S3Object download(String env, String filePath){
        if(null==env || env.isEmpty())
            env = AWSCred.PRODUCTION.getEnv();
        AWSCred cred = AWSCred.getByEnv(env);

        if(null==filePath || filePath.isEmpty()){
            LOG.error("S3 source path is required ");
            return null;
        }
        S3Object s3Object = null;
        LOG.info("s3:"+cred.getBucket()+"/"+ filePath);
        try{
            AmazonS3 s3Client = getAmazonS3(cred);

            s3Object = s3Client.getObject(cred.getBucket(), filePath);
            LOG.info("s3 download Completed !!!");
        }catch (AmazonServiceException e){
            LOG.error("s3 exception\n"+ e.getMessage());
            e.printStackTrace();
        }
        return s3Object;
    }

    public static int sendToS3(String env, FileInfo sourceFileInfo, String destPrefix){
        if(null==env || env.isEmpty())
            env = AWSCred.PRODUCTION.getEnv();
        AWSCred cred = AWSCred.getByEnv(env);
        int status = 0;
        try{
            AmazonS3 s3Client = getAmazonS3(cred);
            File file = new File(sourceFileInfo.getAbsolutePath());
            String key = destPrefix+"/"+sourceFileInfo.getRelativePath()+file.getName();

            PutObjectResult putObjectResult = s3Client.putObject(new PutObjectRequest(cred.getBucket(), key, file));
            if(!putObjectResult.getContentMd5().isEmpty()) {
                LOG.info("UPLOAD : "+sourceFileInfo.getAbsolutePath() + " => " + s3Client.getUrl(cred.getBucket(), key).toString());
            }else{
                LOG.error("ERROR sending file from "+sourceFileInfo.getAbsolutePath() + " to " + cred.getBucket()+"/"+key);
            }
        }catch (AmazonServiceException e){
            LOG.error("s3 exception!!\n"+e.getMessage());
            status = 1;
            e.printStackTrace();
        }
        return status;
    }


}
