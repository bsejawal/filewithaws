package com.bsejawal.s3sync.task;

import com.amazonaws.services.s3.model.S3Object;
import com.bsejawal.s3sync.utils.Locations;
import com.bsejawal.s3sync.model.FileInfo;
import com.bsejawal.s3sync.model.Location;
import com.bsejawal.s3sync.utils.AWSUtils;
import com.bsejawal.s3sync.utils.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class S3ToLocal {
    private static final Logger LOG = Logger.getLogger(S3ToLocal.class);
    private static List<String> csvPaths = Arrays.asList("s3-to-local.csv");
/*
    public static void main(String[] args) {
//        S3ToLocal toLocal = new S3ToLocal();
//        toLocal.invoke();
        for(;;){
            try {
                LOG.info("Waiting 10 seocnd");
                TimeUnit.SECONDS.sleep(10);
                S3ToLocal toLocal = new S3ToLocal();
                toLocal.invoke();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
*/

    public void invoke() {
        Set<Location> locations = Locations.getLocations(csvPaths);

        for (Location location: locations){
            System.out.println(location);
            Set<FileInfo> localFileInfos = new HashSet<>();
            Set<FileInfo> s3FileInfos = new HashSet<>();
            AWSUtils.s3FileInfos(location.getEnv(), location.getSource(), s3FileInfos);

            File[] files = new File(location.getDestination()).listFiles();
            if (files == null) {
                LOG.error("\n"+location.getSource() +": either empty or have no proper access/permission\n");
                continue;
            }
            FileUtils.getLocalFileInfos(files, localFileInfos, location.getSource());


            for (FileInfo s3FileInfo : s3FileInfos){
                boolean download=true;
                for(FileInfo localFileInfo : localFileInfos){
                    if(s3FileInfo.equals(localFileInfo)){
                        download = false;
                        continue;
                    }
                }
                if(download){
                    S3Object s3Obj = AWSUtils.download(location.getEnv(), s3FileInfo.s3Key());
                    String destLoc = location.getDestination();
                    if(s3FileInfo.getRelativePath().startsWith("/"))
                        s3FileInfo.setRelativePath(s3FileInfo.getRelativePath().substring(1));
                    if(!s3FileInfo.getRelativePath().isEmpty()) {
                        destLoc += s3FileInfo.getRelativePath().replace("/", File.separator);
                    }
                    destLoc+=s3FileInfo.getName();
                    if(FileUtils.s3ObjToFile(s3Obj, destLoc)){
                        LOG.info("DOWNLOAD : "+s3FileInfo.getAbsolutePath()+" => "+destLoc);
                    }
                }
            }
        }
    }


}
