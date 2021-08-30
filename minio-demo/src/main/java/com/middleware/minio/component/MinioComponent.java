package com.middleware.minio.component;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import io.minio.BucketExistsArgs;
import io.minio.DownloadObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.UploadObjectArgs;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: MinIO service impl
 * @author: cuiweiman
 * @date: 2021/8/26 14:49
 */
@Slf4j
@Component
@AllArgsConstructor
public class MinioComponent {

    private MinioClient minioClient;

    public List<Bucket> listBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean bucketExists(String bucketName) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            return minioClient.bucketExists(bucketExistsArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void makeBucket(String bucketName) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (minioClient.bucketExists(bucketExistsArgs)) {
                throw new RuntimeException(bucketName + " already exists");
            }
            final MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();
            minioClient.makeBucket(makeBucketArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void makeBucketNoCheck(String bucketName) {
        try {
            final MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();
            minioClient.makeBucket(makeBucketArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void removeBucket(String bucketName) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                throw new RuntimeException(bucketName + " not exists");
            }
            final RemoveBucketArgs removeBucketArgs = RemoveBucketArgs.builder().bucket(bucketName).build();
            minioClient.removeBucket(removeBucketArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 列出桶内的所有对象
     *
     * @param bucketName 存储桶名称
     * @param prefix     对象名称的前缀
     * @param recursive  是否递归查找，如果是false,就模拟文件夹结构查找
     * @param maxKeys    符合的对象的个数
     */
    public List<Result<Item>> listObjects(String bucketName, String prefix,
                                          boolean recursive, Integer maxKeys) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                throw new RuntimeException(bucketName + " not exists");
            }

            final ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(recursive)
                    .maxKeys(maxKeys)
                    .build();
            final Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);
            return Lists.newArrayList(results);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> listObjectsName(String bucketName, String prefix) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                throw new RuntimeException(bucketName + " not exists");
            }
            final ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .build();
            final Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);
            List<String> objectsNameList = new ArrayList<>();
            for (Result<Item> result : results) {
                final String objectName = result.get().objectName();
                objectsNameList.add(objectName);
            }
            return objectsNameList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void uploadObject(String bucketName, String prefix, String objectName, String objectPath) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                this.makeBucketNoCheck(bucketName);
            }
            final UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(Paths.get(prefix, objectName).toString())
                    .filename(objectPath)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void putObject(String bucketName, String prefix, File file) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                this.makeBucketNoCheck(bucketName);
            }
            final FileInputStream inputStream = new FileInputStream(file);
            final PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(Paths.get(prefix, file.getName()).toString())
                    .stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getObjectsStream(String bucketName, String prefix) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                throw new RuntimeException(bucketName + " not exists");
            }
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(prefix).build();
            return minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getObjectsBytes(String bucketName, String prefix) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                throw new RuntimeException(bucketName + " not exists");
            }
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(prefix).build();
            final GetObjectResponse response = minioClient.getObject(getObjectArgs);
            return ByteStreams.toByteArray(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadObjects(String bucketName, String prefix, String destDir) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                throw new RuntimeException(bucketName + " not exists");
            }
            DownloadObjectArgs downloadObjectArgs = DownloadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(prefix)
                    .filename(destDir).build();
            minioClient.downloadObject(downloadObjectArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 下载 minio 路径下所有的 文件 到 指定的本地目录
     * 1. 先查出 minio 路径下所有的文件名
     * 2. 使用路径和文件名拼接出正确的 minio 文件前缀
     * 3. 逐个下载文件到指定的本地目录
     *
     * @param bucketName 桶名
     * @param prefix     前缀
     * @param destDir    指定的本地目录
     */
    public void downloadAllObjects(String bucketName, String prefix, String destDir) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                throw new RuntimeException(bucketName + " not exists");
            }
            final List<String> objectsName = this.listObjectsName(bucketName, prefix);
            List<DownloadObjectArgs> downloadObjectArgsList = objectsName.stream().map(
                    item -> DownloadObjectArgs.builder().bucket(bucketName).object(Paths.get(prefix, item).toString())
                            .filename(Paths.get(destDir, item).toString()).build()
            ).collect(Collectors.toList());
            for (DownloadObjectArgs downloadArgs : downloadObjectArgsList) {
                try {
                    minioClient.downloadObject(downloadArgs);
                } catch (Exception e) {
                    log.info("{} 下载失败，错误原因：{}", downloadArgs.filename(), e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeObject(String bucketName, String prefix) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                throw new RuntimeException(bucketName + " not exists");
            }

            final RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName).object(prefix).build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeObjects(String bucketName, List<String> prefixList) {
        try {
            final BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                throw new RuntimeException(bucketName + " not exists");
            }

            List<DeleteObject> objects =
                    prefixList.stream().map(DeleteObject::new).collect(Collectors.toList());
            final RemoveObjectsArgs removeObjectsArgs =
                    RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build();
            final Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            for (Result<DeleteError> result : results) {
                final DeleteError deleteError = result.get();
                log.info("bucketName: {}, objectName:{} ", deleteError.bucketName(), deleteError.objectName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
