package com.laoxu.java.authman.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laoxu.java.authman.mapper.SysOperLogMapper;
import com.laoxu.java.authman.model.SysOperLog;
import com.laoxu.java.authman.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author laoxu
 * @since 2020-11-15
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {

}
