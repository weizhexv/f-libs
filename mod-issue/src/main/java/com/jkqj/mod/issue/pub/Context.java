package com.jkqj.mod.issue.pub;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

class Context {
    static Context NONE = new Context();
    Pub pub;
    Mapper mapper;

    String mapperClassName;

    /**
     * Mybatis配置
     */
    Configuration configuration;

    String getMethodName(String method) {
        return mapperClassName + "." + method;
    }

    /**
     * 变动SQL
     */
    boolean isChange(String method) {
        if(configuration.getClass().getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration") && "selectOne".equals(method)){
            return false;
        }
        MappedStatement mp = configuration.getMappedStatement(getMethodName(method));
        SqlCommandType sqlCommandType = mp.getSqlCommandType();
        return sqlCommandType == SqlCommandType.INSERT
                || sqlCommandType == SqlCommandType.UPDATE
                || sqlCommandType == SqlCommandType.DELETE;
    }

    /**
     * 是否抓取完成
     */
    boolean isComplete() {
        return pub != null && mapper != null;
    }
}
