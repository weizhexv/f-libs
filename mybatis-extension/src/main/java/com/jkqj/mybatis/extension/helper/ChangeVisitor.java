package com.jkqj.mybatis.extension.helper;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.jkqj.mybatis.extension.backstage.AuoBackstageProvider;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 修改表名及删除空列的visitor
 *
 * @author rolandhe
 */
class ChangeVisitor extends MySqlASTVisitorAdapter {

    private final MySqlParserHelper.TableNameChanger tableNameChanger;
    private final Set<Integer> nullColumnIndexSet;
    private final Set<Integer> removedIndexSet;
    private final boolean noNullColumn;
    private final BackstageContext backstageContext ;

    ChangeVisitor(MySqlParserHelper.TableNameChanger tableNameChanger, Set<Integer> nullColumnIndexSet, Set<Integer> removedIndexSet, BackstageContext backstageContext ) {
        this.tableNameChanger = tableNameChanger;
        this.nullColumnIndexSet = nullColumnIndexSet;
        this.removedIndexSet = removedIndexSet;
        noNullColumn = (nullColumnIndexSet == null || nullColumnIndexSet.size() == 0);
        this.backstageContext = backstageContext;
    }

    public boolean visit(MySqlInsertStatement x) {
        if (noNullColumn) {
            addInsertAutoBackStage(x);
            return true;
        }
        List<SQLExpr> list = x.getColumns();
        SQLInsertStatement.ValuesClause valuesClause = x.getValues();
        List<SQLExpr> vList = valuesClause.getValues();

        Iterator<SQLExpr> viter = vList.iterator();
        Iterator<SQLExpr> iter = list.iterator();

        int index = 0;
        while (viter.hasNext()) {
            SQLExpr vExpr = viter.next();
            iter.next();
            if (!(vExpr instanceof SQLVariantRefExpr)) {
                continue;
            }
            if (nullColumnIndexSet.contains(index)) {
                iter.remove();
                viter.remove();
                removedIndexSet.add(index);
            }
            index++;
        }
        addInsertAutoBackStage(x);
        return true;
    }

    public boolean visit(MySqlUpdateStatement x) {
        if (noNullColumn) {
            addUpdateAutoBackstage(x);
            return true;
        }
        Iterator<SQLUpdateSetItem> iterator = x.getItems().iterator();
        int index = 0;
        while (iterator.hasNext()) {
            SQLUpdateSetItem item = iterator.next();
            if (!(item.getValue() instanceof SQLVariantRefExpr)) {
                continue;
            }
            if (nullColumnIndexSet.contains(index)) {
                iterator.remove();
                removedIndexSet.add(index);
            }
            index++;
        }
        addUpdateAutoBackstage(x);

        return true;
    }

    public boolean visit(SQLExprTableSource x) {
        if (tableNameChanger != null) {
            SQLName sqlName = x.getName();
            String oldName = sqlName.getSimpleName();
            String nName = tableNameChanger.changeName(oldName);
            x.setSimpleName(nName);
        }
        return true;
    }

    private void addInsertAutoBackStage(MySqlInsertStatement x) {
        if (!backstageContext.canAutoBackstage()) {
            return;
        }
        List<SQLExpr> list = x.getColumns();
        list.add(new SQLIdentifierExpr(backstageContext.autoFillBackstage.backstageColumn()));

        SQLInsertStatement.ValuesClause valuesClause = x.getValues();
        List<SQLExpr> vList = valuesClause.getValues();

        vList.add(new SQLIntegerExpr(backstageContext.auoBackstageProvider.provideBackstageIdValue(), valuesClause));

    }

    private void addUpdateAutoBackstage(MySqlUpdateStatement x) {
        if (!backstageContext.canAutoBackstage()) {
            return;
        }
        SQLUpdateSetItem item = new SQLUpdateSetItem();
        item.setParent(x);

        item.setColumn(new SQLIdentifierExpr(backstageContext.autoFillBackstage.backstageColumn()));
        item.setValue(new SQLIntegerExpr(backstageContext.auoBackstageProvider.provideBackstageIdValue(), item));

        x.addItem(item);
    }
}
