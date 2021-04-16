package com.laoxu.java.authman.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 审计日志
 * @Author laoxu
 * @Date 2021/3/15 16:19
 **/
@Data
@TableName("sys_oper_log")
public class SysOperLog extends Model<SysOperLog> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作模块
     */
    private String operModule;

    /**
     * 操作方法
     */
    private String operMethod;

    /**
     * 操作类型
     */
    private String operType;

    /**
     * 操作描述
     */
    private String operDesc;

    /**
     * 请求方法
     */
    private String reqMethod;

    /**
     * 请求参数
     */
    private String operParam;

    /**
     * 请求参数
     */
    private String operIp;

    /**
     * 请求uri
     */
    private String operUri;

    /**
     * 操作人
     */
    private String operUser;

    /**
     * 操作时间
     */
    private String createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
