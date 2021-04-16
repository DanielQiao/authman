package com.laoxu.java.authman.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feilong.core.bean.ConvertUtil;
import com.feilong.core.util.CollectionsUtil;
import com.laoxu.java.authman.mapper.SysRoleUserMapper;
import com.laoxu.java.authman.mapper.SysUserMapper;
import com.laoxu.java.authman.model.SysRole;
import com.laoxu.java.authman.model.SysRoleUser;
import com.laoxu.java.authman.model.SysUser;
import com.laoxu.java.authman.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    SysUserMapper userMapper;

    @Autowired
    SysRoleUserMapper roleUserMapper;

    @Override
    public Page<SysUser> selectUserList(Page<SysUser> page,String name,Integer deptId) {
        page.setRecords(userMapper.selectUserList(page,name,deptId));
        return page;
    }

    @Override
    public SysUser selectUserRole(Map<String, Object> parameter) {
        return userMapper.selectUserRole(parameter);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Integer userId) {
        return userMapper.selectRoleListByUserId(userId);
    }

    @Transactional
    @Override
    public void saveRole(Integer userId, List<Integer> roleIds) {
        List<SysRoleUser> userRoles = roleUserMapper.selectByMap(ConvertUtil.toMap("user_id",(Object)userId));
        List<SysRoleUser> newRes = new ArrayList<>();
        for(Integer sid : roleIds){
            SysRoleUser rr = new SysRoleUser();
            rr.setUserId(userId);
            rr.setRoleId(sid);
            newRes.add(rr);
        }

        //查找出需要删除的
        List<SysRoleUser> removeRes = CollectionsUtil.selectRejected(userRoles,"roleId",roleIds);
        //查找需要新增的
        List<SysRoleUser> addRes = CollectionsUtil.selectRejected(newRes,"roleId",
                CollectionsUtil.getPropertyValueList(userRoles, "roleId"));
        for(SysRoleUser r:removeRes){
            roleUserMapper.deleteById(r.getId());
        }
        for(SysRoleUser r:addRes){
            r.setCreateTime(new Date(System.currentTimeMillis()));
            roleUserMapper.insert(r);
        }
    }
}
