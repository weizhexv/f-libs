package com.jkqj.mybatis.extension.helper;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.jkqj.mybatis.extension.backstage.AuoBackstageProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 解析sql，更改表名，用于自动分表的实现
 *
 * @author rolandhe
 */
public class MySqlParserHelper {
    /**
     * 生成新表名的回调接口
     */
    public interface TableNameChanger {
        String changeName(String oldName);
    }


    /**
     * 解析sql，修改表名为分区表名，生成新sql
     *
     * @param sql
     * @param tableNameChanger
     * @return
     */
    public static String parseAndConvertSql(String sql, final TableNameChanger tableNameChanger, final Set<Integer> nullColumnIndexSet, BackstageContext backstageContext ) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
        boolean hasNullColumn = null != nullColumnIndexSet && nullColumnIndexSet.size() > 0;
        Set<Integer> removed = hasNullColumn ? new HashSet<>() : Collections.emptySet();

        stmtList.get(0).accept(new ChangeVisitor(tableNameChanger, nullColumnIndexSet, removed,backstageContext));
        if (hasNullColumn) {
            nullColumnIndexSet.clear();
            nullColumnIndexSet.addAll(removed);
        }
        return SQLUtils.toMySqlString(stmtList.get(0), VisitorFeature.OutputUseInsertValueClauseOriginalString);
    }
}
