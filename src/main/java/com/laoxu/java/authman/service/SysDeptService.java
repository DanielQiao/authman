package com.laoxu.java.authman.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laoxu.java.authman.model.SysDept;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
public interface SysDeptService extends IService<SysDept> {
    Page<SysDept> selectDeptsPage(Page<SysDept> page, Wrapper<SysDept> wrapper);

    Page<SysDept> selectDeptList(Page<SysDept> page, Integer deptId);

    SysDept selectDeptsById(Integer id);

}
