package com.shanzha.service;

import cn.hutool.core.lang.UUID;

import com.shanzha.config.MinioConfigProperties;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class MinioService {
    @Autowired
    private final MinioClient minioClient;
    @Autowired
    private final MinioConfigProperties minioConfigProperties;

    @Value("${minio.bucket}")
    private String bucketName;
    @PostConstruct
    public void init() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    // 上传文件（支持大文件）
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        InputStream stream = file.getInputStream();

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioConfigProperties.getBucket())
                .object(fileName)
                .stream(stream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build());

        return fileName;
    }

    // 下载文件
    public InputStream download(String fileName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioConfigProperties.getBucket())
                .object(fileName)
                .build());
    }

    // 删除文件
    public void delete(String fileName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioConfigProperties.getBucket())
                .object(fileName)
                .build());
    }

    // 获取永久访问路径（需设置 bucket policy 为 public-read）
    public String getPublicUrl(String fileName) {
        return minioConfigProperties.getEndpoint() + "/" + minioConfigProperties.getBucket() + "/" + fileName;
    }

    // 获取签名 URL（私有桶）
    public String getSignedUrl(String fileName, Integer expireSeconds) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(minioConfigProperties.getBucket())
                .object(fileName)
                .expiry(expireSeconds)
                .build());
    }
    public String extractFileName(String url) {
        try {
            String path = new URL(url).getPath(); // /novel/abc.jpg
            return path.substring(path.lastIndexOf("/") + 1);
        } catch (MalformedURLException e) {
            return null;
        }
    }

}

