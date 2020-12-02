package com.myt.fastdfs.mapper;

import com.myt.fastdfs.model.CreditorInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CreditorInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CreditorInfo record);

    int insertSelective(CreditorInfo record);

    CreditorInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CreditorInfo record);

    int updateByPrimaryKey(CreditorInfo record);

    List<CreditorInfo> selectAllCredtiorInfo();

    /**
     * 根据债权的id，将组和合同路径更新为null
     * @param id
     * @return
     */
    int updateConstractById(Integer id);
}