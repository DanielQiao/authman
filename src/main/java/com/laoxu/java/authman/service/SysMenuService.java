package com.laoxu.java.authman.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laoxu.java.authman.model.SysMenu;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> findAllByUserId(int userId);
    List<SysMenu> findTreeMenusByUserId(int userId);
    List<SysMenu> queryMenuList(Map<String, Object> parameter);
    Page<SysMenu> selectMenuPage(Page<SysMenu> page, Wrapper<SysMenu> wrapper);
    void deleteRoleMenu(int menuId);

}
