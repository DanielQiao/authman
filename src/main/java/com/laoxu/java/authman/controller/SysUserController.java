package com.laoxu.java.authman.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feilong.core.Validator;
import com.feilong.core.bean.ConvertUtil;
import com.laoxu.java.authman.annotation.OperLog;
import com.laoxu.java.authman.common.MD5Util;
import com.laoxu.java.authman.common.Result;
import com.laoxu.java.authman.common.ResultUtil;
import com.laoxu.java.authman.model.SysRole;
import com.laoxu.java.authman.model.SysRoleUser;
import com.laoxu.java.authman.model.SysUser;
import com.laoxu.java.authman.service.SysDeptService;
import com.laoxu.java.authman.service.SysRoleService;
import com.laoxu.java.authman.service.SysRoleUserService;
import com.laoxu.java.authman.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
@Controller
@RequestMapping("/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysDeptService deptService;

    @Autowired
    private SysRoleUserService roleUserService;

    @GetMapping("listUI")
    public String listUI() {
        return "user/list";
    }

    @GetMapping("toSelectDept")
    public String SelectDept() {
        return "user/selectDept";
    }

    @GetMapping("selectRole/{userId}")
    public String SelectRole(@PathVariable Integer userId, Model model) {
        // 根据用户ID查询其拥有的角色
        //List<Map<String,Object>> roles = userService.selectRolesByUserId(userId);
        SysUser entity = userService.getById(userId);
        model.addAttribute("user", entity);

        return "user/selectRole";
    }

    @PostMapping("list")
    @ResponseBody
    public Result<IPage<SysUser>> list(@RequestParam(value = "username", required = false) String username,
                                       @RequestParam(value = "deptId", required = false) Integer deptId,
                                       @RequestParam(defaultValue = "1") Integer pageNo,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        // 获取当前已登录用户部门ID
        /*Integer currentDeptId = getUserEntity().getDeptId();
        if(deptId==null){
            deptId = currentDeptId;
        }*/
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        Page<SysUser> page = new Page<>(pageNo,pageSize);
        IPage<SysUser> result = userService.selectUserList(page, username, deptId);
        // 设置总记录数
        result.setTotal(userService.count(queryWrapper));


        return ResultUtil.ok(result);
    }

    @GetMapping("checkAccount")
    @ResponseBody
    public boolean checkAccount(@RequestParam(required=true) String username) {
        List<SysUser> user = (List<SysUser>) userService.listByMap(ConvertUtil.toMap("username",(Object)username));
        if(!Validator.isNullOrEmpty(user)){
            return false;
        }
        return true;
    }

    @OperLog(operModule = "用户管理",operType = "修改",operDesc = "修改用户")
    @PostMapping("save")
    @ResponseBody
    public Result<String> add(@RequestBody SysUser user, Map<String,Object> map){
        if(user.getId()==null){
            // 检查用户是否存在
            if(!checkAccount(user.getUsername())){
                return ResultUtil.fail("用户名已存在！");
            }
            // 设置添加用户的密码和加密盐
            user.setPassword(MD5Util.md5Password(user.getPassword(),2));
            //user.setSalt(userEntity.getSalt());
            // 设置创建者姓名
            user.setCreateUser("");
            user.setCreateTime(new Date());

            // 保存用户
            boolean result = userService.save(user);
            if(!result)
            {
                return ResultUtil.fail("添加失败！");
            }

            // 添加角色和用户关系记录
            /*SysRoleUser roleUser = new SysRoleUser();
            roleUser.setRoleId(user.getRole().getId());
            roleUser.setUserId(user.getId());
            roleUserService.save(roleUser);*/
        }else{
            user.setUpdateTime(new Date());
            userService.saveOrUpdate(user);
        }
        return ResultUtil.ok();
    }

    @OperLog(operModule = "用户管理",operType = "删除",operDesc = "删除用户")
    @PostMapping("remove")
    @ResponseBody
    public Result<String> remove(@RequestParam Integer id) {
        // 1.删除用户与角色的关联记录
        Map<String,Object> param = new HashMap<>();
        param.put("user_id", id);
        roleUserService.removeByMap(param);
        // 2.删除用户
        if(!userService.removeById(id)){
            return ResultUtil.fail("删除失败！");
        }
        return ResultUtil.ok();
    }

    @GetMapping("info/{userId}")
    public String selectInfo(Map<String,Object> map,@PathVariable(required=true) Integer userId) {
        SysUser user = userService.getById(userId);
        map.put("user", user);
        return "user/info";
    }

    // 根据用户Id查询其拥有的角色
    @GetMapping("/getRoles/{userId}")
    @ResponseBody
    public List<SysRole> getRoles(@PathVariable Integer userId){
        List<SysRole> roles = userService.selectRolesByUserId(userId);

        return roles;
    }

    // 保存用户角色
    @OperLog(operModule = "用户管理",operType = "修改",operDesc = "分配角色")
    @PostMapping("saveRole")
    @ResponseBody
    public Result<String> saveRole(@RequestParam Integer userId, @RequestParam(value = "roleIds[]") Integer[] roleIds){
        userService.saveRole(userId, Arrays.asList(roleIds));
        return ResultUtil.ok();
    }

}

