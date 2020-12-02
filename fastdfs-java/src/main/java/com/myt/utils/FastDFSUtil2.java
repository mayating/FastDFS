package com.myt.utils;

import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.IOException;

/**
 * @program: javademo
 * @description: 封装FastDFSUtil工具类
 * @author: Ma YaTing
 * @create: 2020-12-02 13:48
 */
public class FastDFSUtil2 {

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
        ClientGlobal.init("fastdfs.conf");
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
    public static void fileUpload(){
        try {
            //1.获取 StorageClient 对象
            StorageClient storageClient = getStorageClient();
            //2.上传文件 第一个参数：本地文件路径 第二个参数：上传文件的后缀 第三个参数：文件信息
            String[] uploadArray = storageClient.upload_file("C:\\Users\\枫枫\\Desktop\\a.txt", "txt", null);
            for (String str : uploadArray) {
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }finally {
            closeFastDFS();
        }
    }

    /**
     * 文件下载
     */
    public static void fileDownload(){
        try {
            //1. 获取 StorageClient 对象
            StorageClient storageClient = getStorageClient();
            //2. 下载文件 返回0表示成功，其他均表示失败
            int num = storageClient.download_file("group1", "", "b.txt");
            System.out.println(num);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }finally {
            closeFastDFS();
        }
    }

    /**
     * 文件删除
     */
    public static void fileDelete(){
        try {
            //1. 获取 StorageClient 对象
            StorageClient storageClient = getStorageClient();
            //2. 删除文件 返回0表示成功，其他均表示失败
            int num = storageClient.delete_file("group1", "");
            System.out.println(num);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }finally {
            closeFastDFS();
        }
    }
}
