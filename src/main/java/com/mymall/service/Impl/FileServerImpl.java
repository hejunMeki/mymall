package com.mymall.service.Impl;

import com.google.common.collect.Lists;
import com.mymall.service.IFileSercice;
import com.mymall.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2017/9/24 0024.
 * Description:...
 */
@Service("iFileSercice")
public class FileServerImpl implements IFileSercice{
    private static Logger logger= LoggerFactory.getLogger(FileServerImpl.class);


    /*
        上传
        file 要上传的文件 file文件的路径
        返回上传的文件名
        todo 对上传结果的处理
     */
    public String pictureUpload(MultipartFile file,String path)
    {
        //获取原始文件名
        String fileName=file.getOriginalFilename();
        //获取文件扩展名
        String extension=fileName.substring(fileName.lastIndexOf(".")+1);
        //给文件重新命名    为什么这样做   这样可以上传相同的图片  而不同考虑覆盖  但是这样真的好吗？？？
        String newFileName= UUID.randomUUID().toString()+"."+extension;
        //如果临时文件夹不存在则创建
        File tempFile=new File(path);
        if(!tempFile.exists())
        {
            tempFile.setWritable(true);    //设置可写
            tempFile.mkdirs();
        }
        //够建上传文件
        File uploadFile=new File(path,newFileName);
        //然后就可以先上传到服务器临时文件里
        try {
            file.transferTo(uploadFile);
            //文件上传到临时文件夹成功 接下来传到ftp文件服务器里面
            Boolean isUpload=FtpUtil.uploadFile(Lists.newArrayList(uploadFile));
            if(isUpload)
                tempFile.delete();
            //删除临时文件

        } catch (IOException e) {
            logger.error("上传失败",e);
            e.printStackTrace();
        }
        return uploadFile.getName();
    }

}
