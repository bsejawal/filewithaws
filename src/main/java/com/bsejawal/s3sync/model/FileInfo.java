package com.bsejawal.s3sync.model;

import java.util.Date;

public class FileInfo {


    /**
     * example for S3: bsejawal-us-east-1-123456987452/inventorysystem/daily-logs/04-14-2018/categories/catalina.log
     * example for Local: C:\log\S3\inventorysystem\daily-logs\04-14-2018\categories\catalina.log
     * example for Network: \\NetworkComputerName\publicTEam\$TEAM\Public\someLocatin\production\folder\04-14-2018\categories\catalina.log
     */
    private String absolutePath;

    /**
     * example for S3: /inventorysystem/daily-logs/
     * example for Local:  C:\log\S3\
     * example for Network: \\NetworkComputerName\publicTEam\$TEAM\Public\someLocatin\production\folder
     */
    private String basePath;

    /**
     * example for S3: /04-14-2018/categories
     * example for Local: \04-14-2018\categories
     * example for Network: \04-14-2018\categories
     */
    private String relativePath;

    /**
     * example for S3: catalina.log
     * example for Local: catalina.log
     * example for Network: catalina.log
     */
    private String name;


    private Date lastModified;


    private long size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    public void setLastModified(long lastModified) {
        this.lastModified = new Date(lastModified);
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String s3Key() {
        return absolutePath.replace(absolutePath.split("/")[0]+"/","");

    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", lastModified=" + lastModified +
                ", absolutePath='" + absolutePath + '\'' +
                ", relativePath='" + relativePath + '\'' +
                ", size=" + size +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        FileInfo destFileInfo = (FileInfo)obj;
        if(!this.getName().equals(destFileInfo.getName()))
            return false;
        if(!relativePath.equals(destFileInfo.getRelativePath()))
            return false;
        if(lastModified.compareTo(destFileInfo.getLastModified())>0)
            return false;
        if(size!=destFileInfo.getSize())
            return false;
        return true;
    }


}
