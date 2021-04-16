package com.laoxu.java.authman.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laoxu.java.authman.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {
    List<SysDept> selectDeptsPage(Page page, @Param("ew") Wrapper<SysDept> wrapper);

    List<SysDept> selectDeptList(Page<SysDept> page, @Param("deptId") Integer deptId);

    SysDept selectDeptsById(@Param("id") Integer id);
}
