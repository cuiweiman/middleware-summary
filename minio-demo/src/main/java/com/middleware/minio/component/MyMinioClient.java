package com.middleware.minio.component;

import io.minio.MinioClient;
import io.minio.S3Base;

/**
 * 由于 分片上传、断点续传 相关方法 是 protect 修饰，因此只能通过继承父类来实现调用
 * <p>
 * 1. 初始化上传方法，返回上传ID：{@link S3Base#createMultipartUpload(java.lang.String, java.lang.String,
 * java.lang.String, com.google.common.collect.Multimap, com.google.common.collect.Multimap)}
 * 2. 上传分片方法：{@link S3Base#uploadPart(java.lang.String, java.lang.String, java.lang.String, io.minio.PartSource,
 * int, java.lang.String, com.google.common.collect.Multimap, com.google.common.collect.Multimap)}
 * 3. 上传完成方法：{@link S3Base#completeMultipartUpload(java.lang.String, java.lang.String, java.lang.String,
 * java.lang.String, io.minio.messages.Part[], com.google.common.collect.Multimap, com.google.common.collect.Multimap)}
 * 4. 查看正在上传的分片文件：{@link S3Base#listMultipartUploads(java.lang.String, java.lang.String, java.lang.String,
 * java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String,
 * com.google.common.collect.Multimap, com.google.common.collect.Multimap)}
 * 5. 查看上传成功的分片文件：{@link S3Base#listParts(java.lang.String, java.lang.String, java.lang.String,
 * java.lang.Integer, java.lang.Integer, java.lang.String, com.google.common.collect.Multimap,
 * com.google.common.collect.Multimap)}
 * 6. 取消分片上传：{@link S3Base#abortMultipartUpload(java.lang.String, java.lang.String, java.lang.String,
 * java.lang.String, com.google.common.collect.Multimap, com.google.common.collect.Multimap)}
 *
 * @description: 自定义 分片上传的 minio 客户端
 * @author: cuiweiman
 * @date: 2021/8/30 10:15
 * @see <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/API_AbortMultipartUpload.html">取消分片上传 API</a>
 * @see <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-s3-objects.html#list-objects">
 * Amazon S3 SDK for Java</a>
 */
public class MyMinioClient extends MinioClient {

    protected MyMinioClient(MinioClient client) {
        super(client);
    }

}
