package com.messenger.assetservice.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    public String uploadFile(MultipartFile multipartFile, String username) {
        File fileObj = convertMultipartFileToFile(multipartFile);
        String fileName = multipartFile.getOriginalFilename();
        String fileContentType = multipartFile.getContentType();
        String folderName;
        if (fileContentType.equals("image/gif")) {
            folderName = "gifs/";
        } else if (fileContentType.startsWith("image")) {
            folderName = "images/";
        } else if (fileContentType.startsWith("audio")) {
            folderName = "audio/";
        } else if (fileContentType.startsWith("video")) {
            folderName = "videos/";
        } else if (fileContentType.startsWith("text")) {
            folderName = "texts/";
        } else {
            folderName = "other/";
        }
        s3Client.putObject(new PutObjectRequest(bucketName, username + "/" + folderName + fileName, fileObj));
        fileObj.delete();
        return "File uploaded : " + fileName;
    }

    public byte[] downloadFile(String url) {
        String bucketName = getBucketNameFromS3URI(url);
        String objectKey = getObjectKeyFromS3URI(url);
        S3Object s3Object = s3Client.getObject(bucketName, objectKey);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteFile(String url) {
        String bucketName = getBucketNameFromS3URI(url);
        String objectKey = getObjectKeyFromS3URI(url);
        s3Client.deleteObject(bucketName, objectKey);
        return objectKey + " removed ...";
    }

    public static File convertMultipartFileToFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static String getBucketNameFromS3URI(String s3Uri) {
        return s3Uri.substring(5, s3Uri.indexOf('/', 5));
    }

    private static String getObjectKeyFromS3URI(String s3Uri) {
        return s3Uri.substring(s3Uri.indexOf('/', 5) + 1);
    }
}
