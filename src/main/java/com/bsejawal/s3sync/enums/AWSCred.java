package com.bsejawal.s3sync.enums;

public enum AWSCred {

    PRODUCTION("PROD","ACCESS_KEY", "SECRET_KEY", "","DEFAULT_BUCKET", ""),
    NP_PRODUCTION("NP","ACCESS_KEY", "SECRET_KEY","", "",""),
    SANDBOX("SANDBOX","ACCESS_KEY", "SECRET_KEY","","","");

    private String env;
    private String accessKey;
    private String secretKey;
    private String defaultRegion;
    private String bucket;
    private String defaultS3Key; //s3 path

    AWSCred(String env, String accessKey, String secretKey, String defaultRegion, String bucket, String defaultS3Key) {
        this.env = env;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.defaultRegion = defaultRegion;
        this.bucket = bucket;
        this.defaultS3Key = defaultS3Key;
    }

    public static AWSCred getByEnv(String env){
        for(AWSCred c : AWSCred.values()){
            if(c.env.equalsIgnoreCase(env))
                return c;
        }
        return null;
    }


    public String getEnv() {
        return env;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getDefaultRegion() {
        return defaultRegion;
    }

    public String getBucket() {
        return bucket;
    }

    public String getDefaultS3Key() {
        return defaultS3Key;
    }
}
