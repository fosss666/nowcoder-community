package com.fosss.community.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.fosss.community.properties.ApplicationProperty;
import com.fosss.community.service.OssService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {

    @Resource
    private ApplicationProperty property;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @Override
    public String uploadFile(MultipartFile file) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = property.getEndpoint();
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = property.getKeyid();
        String accessKeySecret = property.getKeysecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = property.getBucketname();
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = file.getOriginalFilename();

        //在文件名称中添加随机唯一的值，防止上传文件时由于文件名相同而导致文件覆盖
        //replace是替换掉uuid中的‘-’
        String uuid = UUID.randomUUID().toString().replace("-", "");
        objectName = uuid + objectName;

        //将文件根据时间进行分类
        String s = new DateTime().toString("yyyy/MM/dd");
        objectName = s + "/" + objectName;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            InputStream inputStream = file.getInputStream();
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }

        //拼接url字符串
        //"https://edu-fosss.oss-cn-beijing.aliyuncs.com/背景.png"
        String url = "https://" + bucketName + "." + endpoint + "/" + objectName;
        return url;
    }
}
