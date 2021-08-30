package com.middleware.minio.component;

import com.google.common.collect.Lists;
import com.middleware.minio.MinioApplicationTest;
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@DisplayName("MinioComponent 单元测试")
public class MinioComponentTest extends MinioApplicationTest {

    private final String DIR_PATH = "/Users/cuiweiman";

    @Autowired
    private MinioComponent minioComponent;

    @Test
    public void listBuckets() {
        final List<Bucket> buckets = minioComponent.listBuckets();
        for (Bucket bucket : buckets) {
            log.info(bucket.name() + " " + bucket.creationDate());
        }
    }

    @Test
    public void bucketExists() {
        String bucketName = UUID.randomUUID().toString();
        final boolean test = minioComponent.bucketExists(bucketName);
        assertFalse(test);
    }

    @Test
    public void makeBucket() {
        String bucketName = "minio-demo";
        try {
            minioComponent.makeBucket(bucketName);
        } catch (Exception e) {
            assertEquals("java.lang.RuntimeException: " + bucketName + " already exists", e.getMessage());
        }
        final boolean bucketExists = minioComponent.bucketExists(bucketName);
        assertTrue(bucketExists);
    }

    @Test
    public void removeBucket() {
        String bucketName = UUID.randomUUID().toString();
        this.minioComponent.makeBucket(bucketName);
        boolean bucketExists = this.minioComponent.bucketExists(bucketName);
        assertTrue(bucketExists);

        this.minioComponent.removeBucket(bucketName);

        bucketExists = this.minioComponent.bucketExists(bucketName);
        assertFalse(bucketExists);
    }

    @Test
    public void listObjects() throws Exception {
        String bucketName = "local-test";
        String prefix = "s";
        boolean recursive = true;
        int maxKey = 10;
        final List<Result<Item>> results =
                this.minioComponent.listObjects(bucketName, prefix, recursive, maxKey);
        for (Result<Item> result : results) {
            final Item item = result.get();
            log.info(item.owner().displayName()
                    + " " + item.objectName()
                    + " " + item.lastModified()
                    + " " + item.size());
        }
    }

    @Test
    public void listObjectsName() {
        String bucketName = "local-test";
        String prefix = "";
        final List<String> objectsNameList = this.minioComponent.listObjectsName(bucketName, prefix);
        objectsNameList.forEach(System.out::println);
    }

    @Test
    public void uploadObject() {
        String bucketName = "local-test";
        String fileName = "application.properties";
        String prefix = "";
        String path = DIR_PATH.concat("/Desktop/application.properties");
        minioComponent.uploadObject(bucketName, prefix, fileName, path);
    }

    @Test
    public void putObject() {
        String bucketName = "local-test";
        String prefix = "";
        String path = DIR_PATH.concat("/Desktop/application.properties");
        File file = new File(path);
        minioComponent.putObject(bucketName, prefix, file);
    }


    @Test
    public void getObjectsStream() {
        String bucketName = "local-test";
        String prefix = "/saber.png";
        try (final InputStream objectsStream = minioComponent.getObjectsStream(bucketName, prefix);
             OutputStream outputStream = new FileOutputStream(DIR_PATH.concat("/Downloads/a.png"))) {
            IOUtils.copy(objectsStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getObjectsBytes() {
        String bucketName = "local-test";
        String prefix = "/saber.png";
        final byte[] bytes = minioComponent.getObjectsBytes(bucketName, prefix);
        try (final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
             OutputStream outputStream = new FileOutputStream(DIR_PATH.concat("/Downloads/b.png"))) {
            IOUtils.copy(arrayInputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void downloadObjects() {
        String bucketName = "local-test";
        String prefix = "/saber.png";
        String destDir = DIR_PATH.concat("/Downloads/saber.png");
        minioComponent.downloadObjects(bucketName, prefix, destDir);
    }


    @Test
    public void downloadAllObjects() {
        String bucketName = "local-test";
        String prefix = "";
        String destDir = DIR_PATH.concat("/Downloads");
        minioComponent.downloadAllObjects(bucketName, prefix, destDir);
    }

    @Test
    public void removeObject() {
        String bucketName = "local-test";
        String prefix = "saber.png";
        minioComponent.removeObject(bucketName, prefix);
    }

    @Test
    public void removeObjects() {
        String bucketName = "local-test";
        List<String> prefix = Lists.newArrayList("infinity-3328331.jpg", UUID.randomUUID().toString());
        minioComponent.removeObjects(bucketName, prefix);
    }


}
