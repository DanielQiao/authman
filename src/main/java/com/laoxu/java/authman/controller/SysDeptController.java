package com.laoxu.java.authman.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laoxu.java.authman.annotation.OperLog;
import com.laoxu.java.authman.common.Result;
import com.laoxu.java.authman.common.ResultUtil;
import com.laoxu.java.authman.model.SysDept;
import com.laoxu.java.authman.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部门表 前端控制器
 * </p>
 *
 * @author laoxu
 * @since 2020-11-04
 */
@Controller
@RequestMapping("/sysDept/")
public class SysDeptController {
    @Autowired
    private SysDeptService sysDeptService;

    @GetMapping("listUI")
    public String listUI() {
        return "dept/list";
    }

    @RequestMapping("form")
    public String form(Map<String,Object> map) {
        return "dept/form";
    }

    @GetMapping("toSelectTree")
    public String toSelectTree() {
        return "dept/selectTree";
    }

    @PostMapping("list")
    @ResponseBody
    public Result<IPage<SysDept>> list(@RequestParam(defaultValue = "1") Integer pageNo,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        // 构造分页查询条件
        QueryWrapper<SysDept> queryWrapper = new QueryWrapper<>();


        Page<SysDept> page = new Page<>(pageNo,pageSize);

        IPage<SysDept> result = sysDeptService.selectDeptList(page, null);
        // 设置总记录数
        result.setTotal(sysDeptService.count(queryWrapper));

        sysDeptService.count(queryWrapper);
        return ResultUtil.ok(result);
    }

    @GetMapping("listTree")
    @ResponseBody
    public Object listTree() {
        // User user = getUserEntity();
        // 构建查询条件，从根查找
        QueryWrapper<SysDept> queryWrapper = new QueryWrapper<>();
        // queryWrapper.like("pids", user.getDept().getPids()+user.getDeptId()).or().eq("id",user.getDeptId());
        queryWrapper.like("pids", 0);
        List<SysDept> list = sysDeptService.list(queryWrapper);
        return list;
    }

    @OperLog(operModule = "部门管理",operType = "修改",operDesc = "修改部门")
    @PostMapping("save")
    @ResponseBody
    public Result<String> add(@RequestBody SysDept dept){
        // 新增
        if(dept.getId()==null){
            dept.setCreateTime(new Date());
            dept.setCreateUser("TODO");
        }
        // 设置上级部门ID
        if(dept.getPid()!=0){
            // 获取上级部门的ids然后拼接上级部门id
            SysDept parentDept = sysDeptService.getById(dept.getPid());
            dept.setPids(parentDept.getPids()+dept.getPid()+",");
        }
        if(!sysDeptService.saveOrUpdate(dept)){
            return ResultUtil.fail("添加失败");
        }
        return ResultUtil.ok("添加成功");
    }

    @OperLog(operModule = "部门管理",operType = "删除",operDesc = "删除部门")
    @PostMapping("/remove")
    @ResponseBody
    public Result<String> remove(@RequestParam Integer id){
        sysDeptService.removeById(id);

        return ResultUtil.ok("删除成功！");
    }

    @RequestMapping(value="{id}/select",method= RequestMethod.GET)
    public String select(Map<String,Object> map,@PathVariable Integer id) {
        SysDept sysDept = sysDeptService.getById(id);
        map.put("record",sysDept);
        return "sysDept/edit";
    }

}

