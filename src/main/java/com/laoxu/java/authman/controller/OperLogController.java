package com.laoxu.java.authman.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laoxu.java.authman.annotation.OperLog;
import com.laoxu.java.authman.common.Result;
import com.laoxu.java.authman.common.ResultUtil;
import com.laoxu.java.authman.model.SysOperLog;
import com.laoxu.java.authman.service.SysOperLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 操作日志表 前端控制器
 * </p>
 *
 * @author test
 * @since 2021-02-16
 */
@Controller
@RequestMapping("/operlog")
public class OperLogController {
    @Autowired
    private SysOperLogService logService;

    @GetMapping("listUI")
    public String listUI(){
        return "operlog/list";
    }

    /**
     *  分页查询
     * @param operDesc
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public Result<IPage<SysOperLog>> list(@RequestParam(value = "operDesc", required = false) String operDesc,
                                          @RequestParam(defaultValue = "1") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize){
        // 构造查询条件
        QueryWrapper<SysOperLog> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(operDesc)){
            queryWrapper.like("oper_desc",operDesc);
        }
        Page<SysOperLog> page = new Page<>(pageNo,pageSize);

        IPage<SysOperLog> result = logService.page(page, queryWrapper);
        // 设置总记录数
        result.setTotal(logService.count(queryWrapper));

        return ResultUtil.ok(result);
    }

    @PostMapping("/remove")
    @ResponseBody
    public Result<String> remove(@RequestParam Integer id){
        logService.removeById(id);

        return ResultUtil.ok("删除成功！");
    }

    @PostMapping("/removes")
    @ResponseBody
    public Result<String> removes(@RequestBody Integer[] ids){
        logService.removeByIds(Arrays.asList(ids));

        return ResultUtil.ok("删除成功！");
    }
}
