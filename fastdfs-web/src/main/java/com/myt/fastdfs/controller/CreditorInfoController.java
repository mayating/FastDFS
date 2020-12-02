package com.myt.fastdfs.controller;


import com.myt.fastdfs.model.CreditorInfo;
import com.myt.fastdfs.service.CreditorInfoService;
import com.myt.fastdfs.utils.FastDFSUtil;
import org.apache.naming.factory.ResourceLinkFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


/**
 * @program: 10-fastdfs-web
 * @description:
 * @author: Ma YaTing
 * @create: 2020-12-01 20:45
 */
@Controller
public class CreditorInfoController {

    @Autowired //默认按类型装配依赖对象
    private CreditorInfoService creditorInfoService;

    //跳转到index页面
    @GetMapping("/fastdfs/index")
    public String index(Model model){
        List<CreditorInfo> creditorInfoList = creditorInfoService.getAllCreditorInfo();
        model.addAttribute("creditorInfoList",creditorInfoList);
        //模板页面，不是jsp
        return "index";
    }

    //跳转到上传页面
    @GetMapping("/fastdfs/toUpload")
    public String upload(Model model, @RequestParam("id") Integer id){
        model.addAttribute("id",id);
        return "upload";
    }

    @PostMapping("/fastdfs/upload")
    @ResponseBody
    public String upload(@RequestParam("id") Integer id, @RequestParam("fileName") MultipartFile file){
        //原来文件上传是将文件写到本地或者远程服务器的某个目录下
        //现在的文件上传是将文件上传到fastdfs文件服务器上

        //1 表示上传失败 0 表示成功
        int result = 1;
        //abc.txt --> txt
        String fileExt = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".") + 1);
        try {
            String[] uploadArray = FastDFSUtil.fileUpload(file.getBytes(), fileExt);
            if(uploadArray != null && uploadArray.length == 2){
                //文件上传到FastDFS成功，将合同文件路径更新到债权记录中
                CreditorInfo creditorInfo = new CreditorInfo();
                creditorInfo.setId(id);
                creditorInfo.setGroupname(uploadArray[0]);//组名
                creditorInfo.setRemotefilepath(uploadArray[1]);//远程文件路径
                int updateRow = creditorInfoService.updateCreditorInfo(creditorInfo);
                //数据库更新成功
                if(updateRow > 0){
                    result = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<script>window.parent.uploadOK('" + result + "')</script>";
    }


    @GetMapping("/fastdfs/download")
    public ResponseEntity<byte[]> download(@RequestParam("id") Integer id){
        //根据债权 id 获取 债券对象
        CreditorInfo creditorInfo = creditorInfoService.getCreditorInfoById(id);
        String extName = creditorInfo.getRemotefilepath().substring(creditorInfo.getRemotefilepath().indexOf("."));
        byte[] fileBytes = FastDFSUtil.fileDownload(creditorInfo.getGroupname(), creditorInfo.getRemotefilepath());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM); //流类型
        httpHeaders.setContentDispositionFormData("attachment",System.currentTimeMillis() + extName);

        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(fileBytes, httpHeaders, HttpStatus.OK);
        return responseEntity;
    }


    @RequestMapping("/fastdfs/fileDelete")
    @ResponseBody
    public String fileDelete(@RequestParam("id") Integer id){
        int result = 1;
        try{
            result = creditorInfoService.deleteContract(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return String.valueOf(result);
    }
}
