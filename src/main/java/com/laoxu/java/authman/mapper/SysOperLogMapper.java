package com.laoxu.java.authman.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laoxu.java.authman.model.SysOperLog;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 操作日志表 Mapper 接口
 * </p>
 *
 * @author laoxu
 * @since 2020-11-15
 */
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
    int selectPVData(@Param(value = "day") String day);
    int selectIpNum(@Param(value = "day") String day);
}
