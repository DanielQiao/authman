package com.laoxu.java.authman.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feilong.core.bean.ConvertUtil;
import com.laoxu.java.authman.mapper.SysMenuMapper;
import com.laoxu.java.authman.mapper.SysRoleMenuMapper;
import com.laoxu.java.authman.model.SysMenu;
import com.laoxu.java.authman.service.SysMenuService;
import com.laoxu.java.authman.util.TreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Autowired
    SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     *  通过用户ID获取用户权限
     * @param userId
     * @return
     */
    @Override
    public List<SysMenu> findAllByUserId(int userId) {
        return menuMapper.findAllByUserId(userId);
    }

    @Override
    public List<SysMenu> findTreeMenusByUserId(int userId) {
        List<SysMenu> menus = menuMapper.findMenusByUserId(userId);
        List<SysMenu> treeMenuList =new TreeUtil().treeMenuList(menus, 0);
        return treeMenuList;
    }

    @Override
    public List<SysMenu> queryMenuList(Map<String, Object> parameter) {
        return menuMapper.queryMenuList(parameter);
    }

    @Override
    public Page<SysMenu> selectMenuPage(Page<SysMenu> page, Wrapper<SysMenu> wrapper) {
        page.setRecords(menuMapper.selectMenuPage(page, wrapper));
        return page;
    }

    @Transactional
    @Override
    public void deleteRoleMenu(int menuId) {
        roleMenuMapper.deleteByMap(ConvertUtil.toMap("s_id",(Object)menuId));
        menuMapper.deleteById(menuId);
    }
}
