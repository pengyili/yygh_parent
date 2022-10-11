package com.atguigu.yygh.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.prop.OSSProperties;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/user/oss")
public class OSSController {

    @Autowired
    private OSSProperties ossProperties ;
    @PostMapping("/upload")
    public Result upload(MultipartFile file){

        String endpoint = ossProperties.getEndpoint();
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ossProperties.getAccessKeyId();
        String accessKeySecret = ossProperties.getAccessKeySecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ossProperties.getBucketName();
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。

        String dir = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        String fileName = System.currentTimeMillis() + file.getOriginalFilename();
        try {
            String objectName = dir+ "/" + fileName;

            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, file.getInputStream());

            StringBuilder sb = new StringBuilder("https://")
                    .append(bucketName).append(".").append(endpoint).append("/").append(objectName);
            return Result.ok().data("url"  , sb.toString());
        } catch (Exception oe) {
           oe.printStackTrace();
           return Result.fail();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
