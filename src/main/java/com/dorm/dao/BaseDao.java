package com.dorm.dao;

import com.dorm.util.DBUtil;
import com.dorm.util.PageBean;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BaseDao {
    private static final RowProcessor ROW_PROCESSOR = new BasicRowProcessor(new CompatibleBeanProcessor());
    protected final QueryRunner runner = new QueryRunner(DBUtil.getDataSource());
    protected final QueryRunner txRunner = new QueryRunner();

    protected <T> T queryBean(String sql, Class<T> clazz, Object... params) {
        try {
            return runner.query(sql, new BeanHandler<T>(clazz, ROW_PROCESSOR), params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T queryBean(Connection conn, String sql, Class<T> clazz, Object... params) throws SQLException {
        return txRunner.query(conn, sql, new BeanHandler<T>(clazz, ROW_PROCESSOR), params);
    }

    protected <T> List<T> queryList(String sql, Class<T> clazz, Object... params) {
        try {
            return runner.query(sql, new BeanListHandler<T>(clazz, ROW_PROCESSOR), params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected Number queryNumber(String sql, Object... params) {
        try {
            Number value = runner.query(sql, new ScalarHandler<Number>(), params);
            return value == null ? 0 : value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected int update(String sql, Object... params) {
        try {
            return runner.update(sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected int update(Connection conn, String sql, Object... params) throws SQLException {
        return txRunner.update(conn, sql, params);
    }

    protected int insertAndGetKey(String sql, Object... params) {
        try {
            Number key = runner.insert(sql, new ScalarHandler<Number>(), params);
            return key == null ? 0 : key.intValue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected int insertAndGetKey(Connection conn, String sql, Object... params) throws SQLException {
        Number key = txRunner.insert(conn, sql, new ScalarHandler<Number>(), params);
        return key == null ? 0 : key.intValue();
    }

    protected <T> PageBean<T> queryPage(String sql, String countSql, Class<T> clazz, int page, int size, Object... params) {
        int currentPage = Math.max(page, 1);
        int pageSize = Math.max(size, 1);
        int totalCount = queryNumber(countSql, params).intValue();
        Object[] pageParams = Arrays.copyOf(params, params.length + 2);
        pageParams[pageParams.length - 2] = (currentPage - 1) * pageSize;
        pageParams[pageParams.length - 1] = pageSize;
        List<T> list = queryList(sql + " LIMIT ?, ?", clazz, pageParams);
        return new PageBean<T>(list, totalCount, currentPage, pageSize);
    }

    private static class CompatibleBeanProcessor extends GenerousBeanProcessor {
        @Override
        protected Object processColumn(ResultSet rs, int index, Class<?> propType) throws SQLException {
            Object value = super.processColumn(rs, index, propType);
            if (value == null) {
                return value;
            }
            if (Integer.class.equals(propType) || Integer.TYPE.equals(propType)) {
                return toNumber(value).intValue();
            }
            if (Long.class.equals(propType) || Long.TYPE.equals(propType)) {
                return toNumber(value).longValue();
            }
            if (Double.class.equals(propType) || Double.TYPE.equals(propType)) {
                return toNumber(value).doubleValue();
            }
            if (!Date.class.equals(propType)) {
                return value;
            }
            if (value instanceof LocalDateTime) {
                LocalDateTime dateTime = (LocalDateTime) value;
                return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
            if (value instanceof LocalDate) {
                LocalDate date = (LocalDate) value;
                return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
            return value;
        }

        private Number toNumber(Object value) throws SQLException {
            if (value instanceof Number) {
                return (Number) value;
            }
            if (value instanceof String) {
                try {
                    return new BigDecimal((String) value);
                } catch (NumberFormatException e) {
                    throw new SQLException("Cannot convert value to number: " + value, e);
                }
            }
            throw new SQLException("Cannot convert " + value.getClass().getName() + " to number");
        }
    }
}
