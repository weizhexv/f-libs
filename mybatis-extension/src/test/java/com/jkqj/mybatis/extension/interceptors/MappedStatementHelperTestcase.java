package com.jkqj.mybatis.extension.interceptors;

import com.jkqj.mybatis.extension.helper.MappedStatementHelper;
import org.junit.Test;

public class MappedStatementHelperTestcase {

    @Test
    public void testBeautySql() {
        String sql = "select * from t where name=\"a   b\n'c\\n\\\"    d\";";
        String nsql =  MappedStatementHelper.beautySQL(sql);
        System.out.println(nsql);
    }

    @Test
    public void testBeautySql1() {
        String correct = "select * from t where name=\"a   b\n'c\\n\\\"    d\";";
        String sql = "select     *     \nfrom t\n  where\n name=\"a   b\n'c\\n\\\"    d\";";
        String nsql =  MappedStatementHelper.beautySQL(sql);
        System.out.println(correct.equals(nsql));
        System.out.println(nsql);
    }

    @Test
    public void testBeautySql2() {
        String correct = "select * from t where name='a b\\'c\\n\"  d';";
        String sql = "select *\n     \n  from\nt   \nwhere\n    name='a b\\'c\\n\"  d';";
        String nsql =  MappedStatementHelper.beautySQL(sql);
        System.out.println(correct.equals(nsql));
        System.out.println(nsql);
        System.out.println(correct);
    }
}
