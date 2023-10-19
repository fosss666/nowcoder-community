package com.fosss.community.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author: fosss
 * Date: 2023/10/19
 * Time: 22:16
 * Description:
 */
public interface OssService {
    /**
     * 上传文件
     */
    String uploadFile(MultipartFile file);
}
