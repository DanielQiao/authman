package com.laoxu.java.authman.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laoxu.java.authman.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {
    List<SysRole> selectRoleList(Page page, @Param("deptId") Integer deptId);
    List<SysRole> selectRoleList(@Param("deptId") Integer deptId);

}
