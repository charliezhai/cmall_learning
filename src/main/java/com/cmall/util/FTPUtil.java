package com.cmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String password;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }


    //上传是否成功
    public static boolean uploadFile(List<File> fileList) throws IOException{
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("开始连接服务器，结束上传，上传结果:{}");
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException{
        boolean uploaded = true;
        FileInputStream is = null;
        //连接ftp服务器
        if(connectServer(this.ip,this.port,this.user,this.password)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);//更改工作目录
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置成二进制的文件类型
                ftpClient.enterLocalPassiveMode();
                for(File fileItem:fileList){
                    is = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),is);
                }
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                e.printStackTrace();
            }finally{
                is.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;

    }

    //连接ftp服务器
    private boolean connectServer(String ip,int port,String user,String password){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,password);
        } catch (IOException e) {
            logger.error("连接ftp服务器异常",e);
            e.printStackTrace();
        }
        return isSuccess;
    }



    public static String getFtpIp() {
        return ftpIp;
    }

    public static void setFtpIp(String ftpIp) {
        FTPUtil.ftpIp = ftpIp;
    }

    public static String getFtpUser() {
        return ftpUser;
    }

    public static void setFtpUser(String ftpUser) {
        FTPUtil.ftpUser = ftpUser;
    }

    public static String getFtpPass() {
        return ftpPass;
    }

    public static void setFtpPass(String ftpPass) {
        FTPUtil.ftpPass = ftpPass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
