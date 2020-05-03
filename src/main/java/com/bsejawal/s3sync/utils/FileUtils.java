package com.bsejawal.s3sync.utils;

import com.amazonaws.services.s3.model.S3Object;
import com.bsejawal.s3sync.model.FileInfo;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FileUtils {
    private static final Logger LOG = Logger.getLogger(FileUtils.class);
    public static void getLocalFileInfos(File[] files, Set<FileInfo> localFileInfos, String basePath) {
        for (File file : files) {
            if (file.isDirectory()) {
                getLocalFileInfos(file.listFiles(), localFileInfos, basePath);
            } else {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setSize(file.length());
                fileInfo.setAbsolutePath(file.getAbsolutePath());
                fileInfo.setRelativePath(file.getAbsolutePath().replace(basePath, "").replace("\\","/").replace(file.getName(),""));
                fileInfo.setLastModified(file.lastModified());
                fileInfo.setName(file.getName());
                localFileInfos.add(fileInfo);
            }
        }
    }

    public static boolean delete(String file){
        return delete(new File(file));
    }
    public static boolean delete(File file){
        if(file.delete())
            return true;
        return false;
    }
    public static void copyFile(String sourceFileName, String destionFileName) {
        try {
            File sourceFile = new File(sourceFileName);
            File destinationFile = new File(destionFileName);
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean s3ObjToFile(S3Object s3Object, String filePath){
        createDirIfNotExists(filePath);

        InputStream is = s3Object.getObjectContent();
        File file = new File(filePath);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            is.close();
            outputStream.close();
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage() +"\n"+e.getStackTrace());
        }
        return false;
    }

    private static void createDirIfNotExists(String filePath){
        String path="";
        try {
            String ss[] = filePath.split(Pattern.quote(File.separator));
            path = filePath.replace(ss[ss.length - 1], "");
        }catch (PatternSyntaxException e){
            LOG.error(e.getMessage() +"\n"+e.getStackTrace());
        }
        File file = new File(path);
        if(!file.exists())
            file.mkdirs();
    }
}
