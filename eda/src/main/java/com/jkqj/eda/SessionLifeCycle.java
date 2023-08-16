package com.jkqj.eda;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.Connection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class SessionLifeCycle implements LifeCycle {
    private final SqlSessionFactory factory;
    private SqlSession session;

    public SessionLifeCycle(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void begin() {
        session = factory.openSession();
        Connection connection = session.getConnection();
        try {
            connection.setAutoCommit(false);
        } catch (Exception e) {
            throw new EdaException(e);
        }
    }

    @Override
    public <T> T bind(Class<T> clazz) {
        checkArgument(clazz != null);
        checkState(session != null);

        return session.getMapper(clazz);
    }

    @Override
    public void commit() {
        checkState(session != null);

        Connection connection = session.getConnection();
        try {
            connection.commit();
        } catch (Exception e) {
            throw new EdaException(e);
        }
    }

    @Override
    public void rollback() {
        Connection connection = session.getConnection();
        try {
            connection.rollback();
        } catch (Exception e) {
            throw new EdaException(e);
        }
    }

    @Override
    public void close() {
        try {
            Connection connection = session.getConnection();
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            session.close();
        } catch (Exception e) {
            throw new EdaException(e);
        }
    }
}
