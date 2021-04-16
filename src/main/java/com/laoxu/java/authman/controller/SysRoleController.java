package com.laoxu.java.authman.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feilong.core.Validator;
import com.feilong.core.bean.ConvertUtil;
import com.feilong.json.JsonUtil;
import com.feilong.taglib.display.pager.command.Pager;
import com.laoxu.java.authman.annotation.OperLog;
import com.laoxu.java.authman.common.*;
import com.laoxu.java.authman.model.SysDept;
import com.laoxu.java.authman.model.SysMenu;
import com.laoxu.java.authman.model.SysRole;
import com.laoxu.java.authman.model.SysRoleUser;
import com.laoxu.java.authman.service.SysDeptService;
import com.laoxu.java.authman.service.SysMenuService;
import com.laoxu.java.authman.service.SysRoleService;
import com.laoxu.java.authman.service.SysRoleUserService;
import com.laoxu.java.authman.util.TreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
@Controller
@RequestMapping("/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysRoleUserService roleUserService;

    @GetMapping("listUI")
    public String listUI() {
        return "role/list";
    }

    @GetMapping("selectMenu/{id}")
    public String selectMenu(@PathVariable Integer id, Map<String,Object> map) {
        SysRole role = roleService.getById(id);
        map.put("role", role);
        return "role/selectMenu";
    }

    @PostMapping("list")
    @ResponseBody
    public Result<IPage<SysRole>> list(@RequestParam(defaultValue = "1") Integer pageNo,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        Page<SysRole> page = new Page<>(pageNo,pageSize);
        IPage<SysRole> result = roleService.selectRoleList(page, null);
        // 设置总记录数
        result.setTotal(roleService.count(queryWrapper));

        /*Pager pager = JsonUtil.getObjectFromJson(gridPager, Pager.class);
        Map<String, Object> parameters = null;
        if(Validator.isNullOrEmpty(pager.getParameters())){
            parameters = new HashMap<>();
        }else{
            parameters = pager.getParameters();
        }
        Integer deptId = getUserEntity().getDeptId();
        if(Validator.isNotNullOrEmpty(parameters.get("deptId"))){
            deptId = Integer.parseInt(parameters.get("deptId").toString());
        }
        Page<SysRole> list = roleService.selectRoleList(new Page<SysRole>(pager.getNowPage(), pager.getPageSize()),deptId);
        makeGridResult(parameters, pager, list);*/
        return ResultUtil.ok(result);
    }

    @GetMapping("form")
    public String form(Map<String,Object> map) {
        return "role/form";
    }

    @OperLog(operModule = "用户管理",operType = "修改",operDesc = "修改角色")
    @PostMapping("save")
    @ResponseBody
    public Result<String> add(@RequestBody SysRole role){
        if(role.getId()==null){
            role.setCreateTime(new Date(System.currentTimeMillis()));
            role.setUpdateTime(new Date(System.currentTimeMillis()));
        }else
        {
            role.setUpdateTime(new Date(System.currentTimeMillis()));
        }

        if(!roleService.saveOrUpdate(role)){
            return ResultUtil.fail("保存失败！");
        }
        return ResultUtil.ok();
    }

    @OperLog(operModule = "角色管理",operType = "删除",operDesc = "删除角色")
    @PostMapping("/remove")
    @ResponseBody
    public Result<String> remove(@RequestParam Integer id) {
        // 判断该角色下是否有用户
        QueryWrapper<SysRoleUser> roleUserQuery = new QueryWrapper<>();
        roleUserQuery.eq("role_id",id);
        int roleUserCount = roleUserService.count(roleUserQuery);
        if(roleUserCount > 0){
            return ResultUtil.fail("该角色下存在用户！");
        }

        // 先删除角色和权限关系记录
        roleService.deleteRoleMenu(id);
        // 删除角色记录
        roleService.removeById(id);

        return ResultUtil.ok();
    }

    /**
     * 根据角色ID查询角色
     * @param map
     * @param roleId
     * @return
     */
    @GetMapping("{roleId}/select")
    public String select(Map<String,Object> map,@PathVariable(required=true) Integer roleId) {
        SysRole role = roleService.getById(roleId);
        map.put("role", role);
        return "role/edit";
    }

    @GetMapping("{roleId}/menu")
    public String permission(Map<String,Object> map,@PathVariable(required=true) Integer roleId) {
        SysRole role = roleService.getById(roleId);
        List<SysMenu> resources = menuService.queryMenuList(ConvertUtil.toMap("roleId",(Object)roleId));
        List<JSTreeEntity> jstreeList = new TreeUtil().generateJSTree(resources);
        map.put("role", role);
        map.put("menus", jstreeList);
        return "role/menu";
    }

    /**
     *  根据角色ID查询角色拥有的菜单权限
     * @param roleId
     * @return
     */
    @GetMapping("/getMenu/{roleId}")
    @ResponseBody
    public List<SysMenu> getMenu(@PathVariable(required=true) Integer roleId) {
        List<SysMenu> menus = menuService.queryMenuList(ConvertUtil.toMap("roleId",(Object)roleId));
        return menus;
    }

    @OperLog(operModule = "角色管理",operType = "修改",operDesc = "分配权限")
    @PostMapping("saveMenu")
    @ResponseBody
    public Result<String> saveMenu(Integer roleId, @RequestParam(value = "menuIds[]") Integer[] menuIds){
        roleService.saveMenu(roleId, Arrays.asList(menuIds));
        return ResultUtil.ok();
    }

    @GetMapping("listAll")
    @ResponseBody
    public List<SysRole> listAll(){
        List<SysRole> sysRoles = roleService.selectRoleList(null);
        /*List<SelectEntity> selectList = new ArrayList<>();
        SelectEntity entity = null;
        for (SysRole role: sysRoles
             ) {
            entity = new SelectEntity();
            entity.setId(role.getId());
            entity.setName(role.getRoleName());
            selectList.add(entity);
        }*/

        return sysRoles;
    }
}

