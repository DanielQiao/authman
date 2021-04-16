package com.laoxu.java.authman.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feilong.core.Validator;
import com.feilong.core.bean.ConvertUtil;
import com.feilong.json.JsonUtil;
import com.feilong.taglib.display.pager.command.Pager;
import com.laoxu.java.authman.annotation.OperLog;
import com.laoxu.java.authman.common.Result;
import com.laoxu.java.authman.common.ResultUtil;
import com.laoxu.java.authman.common.Select2Entity;
import com.laoxu.java.authman.model.SysMenu;
import com.laoxu.java.authman.model.SysRole;
import com.laoxu.java.authman.service.SysMenuService;
import com.laoxu.java.authman.util.TreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
@Controller
@RequestMapping("/sysMenu")
public class SysMenuController{
    @Autowired
    private SysMenuService menuService;

    @GetMapping("listUI")
    public String listUI(Map<String,Object> map) {
        List<SysMenu> list = menuService.list(null);

        List<Select2Entity> select2List = new TreeUtil().getSelectTree(list, 0);
        map.put("menus", select2List);

        return "menu/list";
    }

    @GetMapping("list")
    @ResponseBody
    public Result<List<SysMenu>> list() {
        List<SysMenu> list = menuService.list(null);

        return ResultUtil.ok(list);
    }

    @OperLog(operModule = "权限管理",operType = "修改",operDesc = "修改权限")
    @PostMapping("save")
    @ResponseBody
    public Result<String> add(@RequestBody SysMenu entity){
        if(entity.getId()==null){
            entity.setCreateTime(new Date(System.currentTimeMillis()));
            entity.setUpdateTime(new Date(System.currentTimeMillis()));
        }else
        {
            entity.setUpdateTime(new Date(System.currentTimeMillis()));
        }

        if(!menuService.saveOrUpdate(entity)){
            return ResultUtil.fail("保存失败！");
        }
        return ResultUtil.ok();
    }

    @OperLog(operModule = "权限管理",operType = "删除",operDesc = "删除权限")
    @PostMapping("/remove")
    @ResponseBody
    public Result<String> remove(@RequestParam Integer id) {
        List<SysMenu> childrens = (List<SysMenu>) menuService.listByMap(ConvertUtil.toMap("pid",(Object)id));
        if(childrens!=null && childrens.size()>0){
            return ResultUtil.fail("权限下包含子权限！");
        }else{
            menuService.removeById(id);
        }
        return ResultUtil.ok();
    }

    @GetMapping(value="{id}/select")
    public String select(Map<String,Object> map,@PathVariable(required=true) Integer id) {
        SysMenu sysMenu = menuService.getById(id);
        List<SysMenu> list = (List<SysMenu>) menuService.listByMap(null);
        List<Select2Entity> select2List = new TreeUtil().getSelectTree(list, 0);
        map.put("sysMenus", select2List);
        map.put("sysMenu", sysMenu);
        return "sysMenu/edit";
    }

    @GetMapping("icon")
    public String icon() {
        return "menu/icon";
    }

}

