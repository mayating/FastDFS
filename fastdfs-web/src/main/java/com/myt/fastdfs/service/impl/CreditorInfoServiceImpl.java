package com.myt.fastdfs.service.impl;

import com.myt.fastdfs.mapper.CreditorInfoMapper;
import com.myt.fastdfs.model.CreditorInfo;
import com.myt.fastdfs.service.CreditorInfoService;
import com.myt.fastdfs.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: 10-fastdfs-web
 * @description:
 * @author: Ma YaTing
 * @create: 2020-12-01 20:46
 */
@Service
public class CreditorInfoServiceImpl implements CreditorInfoService {

    @Autowired
    private CreditorInfoMapper creditorInfoMapper;


    @Override
    public List<CreditorInfo> getAllCreditorInfo() {
        return creditorInfoMapper.selectAllCredtiorInfo();
    }

    @Override
    public int updateCreditorInfo(CreditorInfo creditorInfo) {
        return creditorInfoMapper.updateByPrimaryKeySelective(creditorInfo);
    }

    @Override
    public CreditorInfo getCreditorInfoById(Integer id) {
        return creditorInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional //加上该注解控制事务
    public int deleteContract(Integer id) {
        //1 删除事变 0 删除成功
        int result = 1;
        //根据债权 id 获取债权信息
        CreditorInfo creditorInfo = creditorInfoMapper.selectByPrimaryKey(id);
        /**
         * 注意：事务控制的数据库，我们先对数据库进行更新，再操作FastDFS
         * 如果操作FastDFS失败了，那么对数据库的操作回滚
         */
        //更新数据库债权表的合同路径及组
        int updateRow = creditorInfoMapper.updateConstractById(id);
        if(updateRow > 0){
            //如果数据库更新成功，那么删除FastDFS上的文件
            int num = FastDFSUtil.fileDelete(creditorInfo.getGroupname(),creditorInfo.getRemotefilepath());
            if(num == 0){
                //如果删除成功，那么将整个操作结果设置为0，表示成功
                result = 0;
            }else {
                //如果删除FastDFS上的文件失败，我们抛出一个运行异常，回滚事务
                throw new RuntimeException("FastDFS 文件删除失败");
            }
        }
        return result;
    }
}
