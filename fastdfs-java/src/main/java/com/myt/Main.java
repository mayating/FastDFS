package com.myt;

import com.myt.utils.FastDFSUtil;

import javax.annotation.Resource;

/**
 * @program: javademo
 * @description:
 * @author: Ma YaTing
 * @create: 2020-11-30 18:09
 */
public class Main {

    public static void main(String[] args) {

        //delete();
        //upload();
        download();
    }

    /**
     * 上传文件
     */
    private static void upload(){
        String[] results = FastDFSUtil.upload("D:\\Projects\\code\\javacode\\javademo\\fastdfs-demo\\overflow.jpg");
        if(results != null && results.length == 2){
            String group = results[0];
            String remoteFileName = results[1];
            System.out.println("group: " + group);
            System.out.println("remoteFileName: " + remoteFileName);
            System.out.println("链接地址： http://192.168.235.143/" + group +"/" + remoteFileName);
        }
    }

    /**
     * 下载文件
     */
    public static void download(){
        boolean result = FastDFSUtil.download("group1", "M00/00/00/wKjrj1_EzBGAIIMIAAEnOwVzhFA991.jpg", "a1.jpg");

        if(result){
            System.out.println("文件下载成功！");
        }else {
            System.out.println("文件下载失败！");
        }
    }

    /**
     * 删除文件
     */
    public static void delete(){
        boolean result = FastDFSUtil.delete("group1", "");
        if(result){
            System.out.println("文件删除成功！");
        }else {
            System.out.println("文件删除失败！");
        }
    }
}
