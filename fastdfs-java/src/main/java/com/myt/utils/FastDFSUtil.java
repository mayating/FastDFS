package com.myt.utils;

import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.IOException;

/**
 * @program: javademo
 * @description:
 * @author: Ma YaTing
 * @create: 2020-11-30 17:09
 */
public class FastDFSUtil {
    /**
     * 文件上传
     */
    public static String[] upload(String localFileName) {
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        try {
            //读取配置文件，用于将所有tracker server的地址读取到内存中
            ClientGlobal.init("fastdfs.conf");
            TrackerClient trackerClient = new TrackerClient();
            trackerServer = trackerClient.getConnection();
            storageServer = trackerClient.getStoreStorage(trackerServer);
            //定义Storage的客户端对象，需要使用这个对象来完成具体的文件上传、下载和删除操作
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            //上传，参数一：需要上传的文件的绝对路径，参数二：需要上传的文件的扩展名，参数三：文件的属性文件，通常不上传
            //返回一个String数组，这个数据对我们非常有用，必须妥善保管，建议保存到数据库
            //返回结果数组：第一个元素为文件所在的组名，第二个元素为文件所在的远程路径名称
            return storageClient.upload_file(localFileName, "jpg", null);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                if (storageServer != null) {
                    storageServer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (trackerServer != null) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 下载
     *
     * @param groupName      组名
     * @param remoteFileName 远程文件名称
     * @param saveFileName   保存的文件名称
     * @return 返回是否下载成功
     */
    public static boolean download(String groupName, String remoteFileName, String saveFileName) {
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        try {
            //读取配置文件，用于将所有tracker server的地址读取到内存中
            ClientGlobal.init("fastdfs.conf");
            TrackerClient trackerClient = new TrackerClient();
            trackerServer = trackerClient.getConnection();
            storageServer = trackerClient.getStoreStorage(trackerServer);
            //定义Storage的客户端对象，需要使用这个对象来完成具体的文件上传、下载和删除操作
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            //文件下载，参数一：文件的组名，参数二：文件的远程文件名，参数三：保存到本地文件的名称
            //返回0，则为文件下载成功，其他值表示下载失败
            int code = storageClient.download_file(groupName, remoteFileName, saveFileName);
            return code == 0;
        } catch (IOException | MyException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                if (storageServer != null) {
                    storageServer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (trackerServer != null) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 文件删除
     *
     * @param groupName      组名
     * @param remoteFileName 远程文件名称
     */
    public static boolean delete(String groupName, String remoteFileName) {
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;
        try {
            //读取配置文件，用于将所有tracker server的地址读取到内存中
            ClientGlobal.init("fastdfs.conf");
            TrackerClient trackerClient = new TrackerClient();
            trackerServer = trackerClient.getConnection();
            storageServer = trackerClient.getStoreStorage(trackerServer);
            //定义Storage的客户端对象，需要使用这个对象来完成具体的文件上传、下载和删除操作
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            //文件删除，参数一：需要删除的文件组名，参数二：文件的远程名称，返回int结果，为0则成功，其他则为失败
            int code = storageClient.delete_file(groupName, remoteFileName);
            return code == 0;
        } catch (IOException | MyException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                if (storageServer != null) {
                    storageServer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (trackerServer != null) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
