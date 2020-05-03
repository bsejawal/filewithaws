package com.bsejawal.s3sync.task;

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

public class LocalToS3 {
    private static final Logger LOG = Logger.getLogger(LocalToS3.class);
    private static List<String> csvPaths = Arrays.asList("local-to-s3.csv");
/*    public static void main(String[] args) {
        for(;;){
            try {
                LOG.info("Waiting 10 seocnd");
                TimeUnit.SECONDS.sleep(10);
                LocalToS3 toS3 = new LocalToS3();
                toS3.invoke();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/



    public void invoke() {
        Set<Location> locations = Locations.getLocations(csvPaths);
        for (Location location: locations){
            LOG.info(location);
            Set<FileInfo> localFileInfos = new HashSet<>();
            Set<FileInfo> s3FileInfos = new HashSet<>();
            File[] files = new File(location.getSource()).listFiles();
            if (files == null) {
                LOG.error("\n"+location.getSource() +": either empty or have no proper access/permission\n");
                continue;
            }
            FileUtils.getLocalFileInfos(files, localFileInfos, location.getSource());
            AWSUtils.s3FileInfos(location.getEnv(), location.getDestination(), s3FileInfos);

            for (FileInfo localFileInfo : localFileInfos){
                boolean upload=true;
                for(FileInfo s3FileInfo : s3FileInfos){
                    if(localFileInfo.equals(s3FileInfo)){
                        upload = false;
                        continue;
                    }
                }
                if(upload){
                    AWSUtils.sendToS3(location.getEnv(), localFileInfo, location.getDestination());
                }
            }
        }
    }


}
