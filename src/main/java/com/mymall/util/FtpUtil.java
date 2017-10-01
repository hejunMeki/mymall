package com.mymall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/9/24 0024.
 * Description:文本服务器工具
 */
public class FtpUtil {
    private static final Logger logger=LoggerFactory.getLogger("FtpUtil.class");


    //加载配置文件属性
    private static String ftpIp=PropertiesUtil.getProperty("ftp.server.ip","127.0.0.1");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user","root");
    private static String ftpPass=PropertiesUtil.getProperty("ftp.pass","root");


    //属性设置
     private String ip;
     private String user;
     private String pass;
     private int port;
     private FTPClient ftpClient;      //ftp客户端


    //构造方法
    public FtpUtil(String ip, String user, String pass, int port) {
        this.ip = ip;
        this.user = user;
        this.pass = pass;
        this.port = port;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
       //创建FtpUtil对象    ftp默认端口21
        FtpUtil ftpUtil=new FtpUtil(ftpIp,ftpUser,ftpPass,21);
        //开始上传
       Boolean isSuccessUpload= ftpUtil.upload("img",fileList);
       return isSuccessUpload;
    }
    //remotePath指定远程服务器的文件目录  相对于根目录
    private boolean upload(String remotePath,List<File> fileList) throws IOException {
        boolean isUpload=true;
        FileInputStream fileInputStream=null;
        //先连接
        isUpload=connect(this.ip,this.user,this.pass,this.port);
        //如果连接成功   开始上传
        if(isUpload)
        {
            try {
                //转换工作目录  也就是打开服务器中接收文件的文件夹
                ftpClient.changeWorkingDirectory(remotePath);
                //设置缓冲
                ftpClient.setBufferSize(1024);
                //设置上传编码格式
                ftpClient.setControlEncoding("UTF-8");
                //设置上传的文件类型  上传图片指定二进制类型
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //服务器模型  安装服务器是指定
                ftpClient.enterLocalPassiveMode();

                //开始遍历上传
                for(File fileItem:fileList)
                {
                    fileInputStream=new FileInputStream(fileItem);
                    //上传
                    ftpClient.storeFile(fileItem.getName(),fileInputStream);
                }

            } catch (IOException e) {
                //文件上传失败
                isUpload=false;
                logger.error("文件上传失败",e);
            }
            finally {
                //关闭流   并不是所有异常都要捕获3  有些异常应该抛出统一管理
                fileInputStream.close();
                //关闭连接
                ftpClient.disconnect();
            }

        }
      return isUpload;
    }

    //连接服务器
    private boolean connect(String ip, String user, String pass, int port)
    {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            //连接
           /// ftpClient.connect(ip);
            ftpClient.connect(ip,port);
            isSuccess = ftpClient.login(user,pass);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }
}
