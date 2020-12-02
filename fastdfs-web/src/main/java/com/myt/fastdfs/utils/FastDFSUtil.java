package com.myt.fastdfs.utils;

import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.IOException;

/**
 * @program: 10-fastdfs-web
 * @description:
 * @author: Ma YaTing
 * @create: 2020-12-01 21:43
 */
public class FastDFSUtil {

    //定义两个全局变量
    private static TrackerServer trackerServer = null;
    private static StorageServer storageServer = null;

    /**
     * 抽取获取 StorageClient的方法
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static StorageClient getStorageClient() throws IOException, MyException {
        //1.加载配置文件，默认去 classpath下加载
        ClientGlobal.init("fdfs_client.conf");
        //2.创建 TrackerClient 对象
        TrackerClient trackerClient = new TrackerClient();
        //3.创建 TrackerServer 对象
        trackerServer = trackerClient.getConnection();
        //4.创建 StorageServlet 对象
        storageServer = trackerClient.getStoreStorage(trackerServer);
        //5.创建 StorageClient 对象，这个对象完成对文件的操作
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        return storageClient;
    }


    /*
    抽取关闭资源的方法
     */
    public static void closeFastDFS(){
        if(storageServer != null){
            try{
                storageServer.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        if(trackerServer != null){
            try {
                trackerServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件上传
     */
    public static String[] fileUpload(byte[] fileBytes,String fileExt){
        String[] uploadArray = null;
        try {
            //1.获取 StorageClient 对象
            StorageClient storageClient = getStorageClient();
            //2.上传文件 第一个参数：本地文件路径 第二个参数：上传文件的后缀 第三个参数：文件信息
            uploadArray = storageClient.upload_file(fileBytes,fileExt,null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }finally {
            closeFastDFS();
        }
        return uploadArray;
    }

    /**
     * 文件下载
     */
    public static byte[] fileDownload(String groupName,String filePath){
        byte[] fileBytes = null;
        try {
            //1. 获取 StorageClient 对象
            StorageClient storageClient = getStorageClient();
            //2. 下载文件 返回0表示成功，其他均表示失败
            fileBytes = storageClient.download_file(groupName, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }finally {
            closeFastDFS();
        }
        return fileBytes;
    }

    /**
     * 文件删除
     */
    public static int fileDelete(String group,String remoteFile){
        int num = 1;
        try {
            //1. 获取 StorageClient 对象
            StorageClient storageClient = getStorageClient();
            //2. 删除文件 返回0表示成功，其他均表示失败
            num = storageClient.delete_file(group, remoteFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }finally {
            closeFastDFS();
        }
        return num;
    }

}
