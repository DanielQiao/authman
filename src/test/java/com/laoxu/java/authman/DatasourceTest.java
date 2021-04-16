package com.laoxu.java.authman;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;
import java.util.List;

/**
 * @Description:
 * @Author laoxu
 * @Date 2021/4/14 23:35
 **/


@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class DatasourceTest {
    @Autowired
    DataSourceProperties dataSourceProperties;

    @Autowired
    protected ApplicationContext applicationContext;


    @Test
    public void query() {
        // 获取配置的数据源
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        // 查看配置数据源信息
        System.out.println(dataSource);
        System.out.println(dataSource.getClass().getName());
        System.out.println(dataSourceProperties);
        //执行SQL,输出查到的数据
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<?> resultList = jdbcTemplate.queryForList("select * from sys_user");
        System.out.println("===>>>>>>>>>>>" + JSON.toJSONString(resultList));
    }
}
