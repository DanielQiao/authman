package com.laoxu.java.authman.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laoxu.java.authman.model.SysRole;
import com.laoxu.java.authman.model.SysUser;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
public interface SysUserService extends IService<SysUser> {
    Page<SysUser> selectUserList(Page<SysUser> page, String name, Integer deptId);
    SysUser selectUserRole(Map<String, Object> parameter);
    List<SysRole> selectRolesByUserId(Integer userId);
    void saveRole(Integer userId, List<Integer> roleIds);
}
