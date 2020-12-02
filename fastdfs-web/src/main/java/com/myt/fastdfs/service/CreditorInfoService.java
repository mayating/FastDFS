package com.myt.fastdfs.service;

import com.myt.fastdfs.model.CreditorInfo;

import java.util.List;

/**
 * @program: 10-fastdfs-web
 * @description:
 * @author: Ma YaTing
 * @create: 2020-12-01 20:46
 */
public interface CreditorInfoService {


    /**
     * 获取所有债权信息
     * @return
     */
    List<CreditorInfo> getAllCreditorInfo();

    /**
     * 更改债权信息
     * @param creditorInfo
     * @return
     */
    int updateCreditorInfo(CreditorInfo creditorInfo);

    /**
     * 根据合同 id 获取债权信息
     * @param id
     * @return
     */
    CreditorInfo getCreditorInfoById(Integer id);

    /**
     * 删除合同
     * @param id
     * @return
     */
    int deleteContract(Integer id);
}
