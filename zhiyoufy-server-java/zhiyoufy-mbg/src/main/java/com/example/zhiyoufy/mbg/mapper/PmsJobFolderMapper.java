package com.example.zhiyoufy.mbg.mapper;

import com.example.zhiyoufy.mbg.model.PmsJobFolder;
import com.example.zhiyoufy.mbg.model.PmsJobFolderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsJobFolderMapper {
    long countByExample(PmsJobFolderExample example);

    int deleteByExample(PmsJobFolderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsJobFolder record);

    int insertSelective(PmsJobFolder record);

    List<PmsJobFolder> selectByExample(PmsJobFolderExample example);

    PmsJobFolder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsJobFolder record, @Param("example") PmsJobFolderExample example);

    int updateByExample(@Param("record") PmsJobFolder record, @Param("example") PmsJobFolderExample example);

    int updateByPrimaryKeySelective(PmsJobFolder record);

    int updateByPrimaryKey(PmsJobFolder record);
}