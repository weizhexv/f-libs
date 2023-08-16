package com.jkqj.mybatis.extension.handler;

import com.jkqj.common.page.PageParam;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author lingchangmeng
 * @version 1.0.0
 * @ClassName PageInterceptor.java
 * @Description ： 自定义分页插件
 * @Signature 拦截器的签名
 * type 拦截的类型 四大对象之一( Executor,ResultSetHandler,ParameterHandler,StatementHandler)
 * method 拦截的方法
 * args 参数,高版本需要加个Integer.class参数,不然会报错
 * @createTime 2021/12/26 18:28:00
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PageInterceptor implements Interceptor {

    /**
     * 每页显示的条目数
     */
    private int pageSize;

    /**
     * 当前现实的页数
     */
    private int pageNo;

    /**
     * 数据库类型
     */
    private String dbType;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取StatementHandler，默认是RoutingStatementHandler
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        // 获取statementHandler包装类
        MetaObject MetaObjectHandler = SystemMetaObject.forObject(statementHandler);

        // 分离代理对象链
        while (MetaObjectHandler.hasGetter("h")) {
            Object obj = MetaObjectHandler.getValue("h");
            MetaObjectHandler = SystemMetaObject.forObject(obj);
        }

        while (MetaObjectHandler.hasGetter("target")) {
            Object obj = MetaObjectHandler.getValue("target");
            MetaObjectHandler = SystemMetaObject.forObject(obj);
        }

        // 获取连接对象
        // Connection connection = (Connection) invocation.getArgs()[0];
        // object.getValue("delegate");  获取StatementHandler的实现类

        //获取查询接口映射的相关信息
        MappedStatement mappedStatement = (MappedStatement) MetaObjectHandler.getValue("delegate.mappedStatement");
        String mapId = mappedStatement.getId();

        // statementHandler.getBoundSql().getParameterObject();

        // 拦截以listPage开头的请求，分页功能的统一实现
        if (mapId.substring(mapId.lastIndexOf('.') + 1).matches("listPage.+$")) {
            // 获取进行数据库操作时管理参数的handler
            ParameterHandler parameterHandler = (ParameterHandler) MetaObjectHandler.getValue("delegate.parameterHandler");
            // 获取请求时的参数
            PageParam pageParam = (PageParam) parameterHandler.getParameterObject();
            // 参数名称和在service中设置到map中的名称一致
            pageNo = pageParam.getPageNo();
            pageSize = pageParam.getPageSize();

            String sql = (String) MetaObjectHandler.getValue("delegate.boundSql.sql");
            // 也可以通过statementHandler直接获取
            // sql = statementHandler.getBoundSql().getSql();

            // 构建分页功能的sql语句
            String limitSql;
            sql = sql.trim();
            limitSql = sql + " limit " + (pageNo - 1) * pageSize + "," + pageSize;

            // 将构建完成的分页sql语句赋值个体'delegate.boundSql.sql'，偷天换日
            MetaObjectHandler.setValue("delegate.boundSql.sql", limitSql);
        }
        // 调用原对象的方法，进入责任链的下一级
        return invocation.proceed();
    }

    /**
     * 获取代理对象
     *
     * @param o
     * @return
     */
    @Override
    public Object plugin(Object o) {
        // 生成object对象的动态代理对象
        return Plugin.wrap(o, this);
    }

    /**
     * 设置默认代理对象的参数
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        // 如果项目中分页的pageSize是统一的，也可以在这里统一配置和获取，这样就不用每次请求都传递pageSize参数了。参数是在配置拦截器时配置的。
        String limit1 = properties.getProperty("limit", "10");
        this.pageSize = Integer.parseInt(limit1);
        this.dbType = properties.getProperty("dbType", "mysql");
    }
}