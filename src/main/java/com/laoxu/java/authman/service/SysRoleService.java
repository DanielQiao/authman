package com.laoxu.java.authman.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laoxu.java.authman.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author laoxu
 * @since 2021.3.20
 */
public interface SysRoleService extends IService<SysRole> {
    Page<SysRole> selectRoleList(Page<SysRole> page, Integer deptId);
    List<SysRole> selectRoleList(@Param("deptId") Integer deptId);
    boolean deleteRoleMenu(Integer roleId);
    void saveMenu(Integer roleId, List<Integer> menuIds);
}
