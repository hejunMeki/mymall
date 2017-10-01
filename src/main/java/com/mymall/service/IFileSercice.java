package com.mymall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2017/9/24 0024.
 * Description:文件
 */
public interface IFileSercice {
    //文件上传
    String pictureUpload(MultipartFile file, String path);
}
