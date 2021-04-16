package com.laoxu.java.authman.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feilong.core.bean.ConvertUtil;
import com.feilong.core.util.CollectionsUtil;
import com.laoxu.java.authman.mapper.SysRoleMapper;
import com.laoxu.java.authman.mapper.SysRoleMenuMapper;
import com.laoxu.java.authman.model.SysRole;
import com.laoxu.java.authman.model.SysRoleMenu;
import com.laoxu.java.authman.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public Page<SysRole> selectRoleList(Page<SysRole> page, Integer deptId) {
        page.setRecords(roleMapper.selectRoleList(page,deptId));
        return page;
    }

    @Override
    public List<SysRole> selectRoleList(Integer deptId) {
        return roleMapper.selectRoleList(deptId);
    }

    @Override
    @Transactional
    public boolean deleteRoleMenu(Integer roleId) {
        return roleMenuMapper.deleteByMap(ConvertUtil.toMap("role_id",(Object)roleId))>0;
    }

    @Override
    @Transactional
    public void saveMenu(Integer roleId, List<Integer> menuIds) {
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectByMap(ConvertUtil.toMap("role_id",(Object)roleId));
        List<SysRoleMenu> newRes = new ArrayList<>();
        for(Integer sid : menuIds){
            SysRoleMenu rr = new SysRoleMenu();
            rr.setRoleId(roleId);
            rr.setMenuId(sid);
            newRes.add(rr);
        }

        //查找出需要删除的
        List<SysRoleMenu> removeRes = CollectionsUtil.selectRejected(roleMenus,"menuId",menuIds);
        //查找需要新增的
        List<SysRoleMenu> addRes = CollectionsUtil.selectRejected(newRes,"menuId",
                CollectionsUtil.getPropertyValueList(roleMenus, "menuId"));
        for(SysRoleMenu r:removeRes){
            roleMenuMapper.deleteById(r.getId());
        }
        for(SysRoleMenu r:addRes){
            r.setCreateTime(new Date(System.currentTimeMillis()));
            roleMenuMapper.insert(r);
        }
    }
}
