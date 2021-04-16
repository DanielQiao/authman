package com.laoxu.java.authman.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laoxu.java.authman.mapper.SysDeptMapper;
import com.laoxu.java.authman.model.SysDept;
import com.laoxu.java.authman.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Autowired
    private SysDeptMapper deptMapper;

    @Override
    public Page<SysDept> selectDeptsPage(Page<SysDept> page, Wrapper<SysDept> wrapper) {
        page.setRecords(deptMapper.selectDeptsPage(page, wrapper));
        return page;
    }

    /**
     * 查询部门分页列表
     * @param page
     * @param deptId
     * @return
     */
    @Override
    public Page<SysDept> selectDeptList(Page<SysDept> page, Integer deptId) {
        page.setRecords(deptMapper.selectDeptList(page, deptId));
        return page;
    }

    /**
     *  根据部门ID查询下属部门
     * @param id
     * @return
     */
    @Override
    public SysDept selectDeptsById(Integer id) {
        return deptMapper.selectDeptsById(id);

    }
}
