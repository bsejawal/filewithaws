package com.bsejawal.s3sync;

import com.bsejawal.s3sync.task.LocalToS3;
import com.bsejawal.s3sync.task.S3ToLocal;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class S3Main {


    private static final Logger LOG = Logger.getLogger(S3Main.class);
    public static void main(String[] args) {
        for (; ; ) {
            try {
                LOG.info("Waiting 10 seocnd");
                TimeUnit.SECONDS.sleep(10);
                LocalToS3 localToS3 = new LocalToS3();
                localToS3.invoke();
                LOG.info("Waiting 10 seocnd");
                TimeUnit.SECONDS.sleep(10);
                S3ToLocal s3ToLocal = new S3ToLocal();
                s3ToLocal.invoke();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}


