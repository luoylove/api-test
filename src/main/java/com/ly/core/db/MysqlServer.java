package com.ly.core.db;

import com.ly.core.exception.BizException;
import com.ly.core.utils.SpringContextUtil;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.collections.Maps;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;


/**
 * Mysql 操作类
 * @author luoy
 */
public class MysqlServer implements BaseDbServer {
    private static final Logger log = LoggerFactory.getLogger(MysqlServer.class);

    private Connection conn;

    private PreparedStatement stmt;
    private ResultSet rs;

    /**
     * 指定数据库dataSource
     * @param dataSource
     */
    public MysqlServer(DataSource dataSource) {
        try {
            this.conn = dataSource.getConnection();
        } catch (SQLException e) {
            throw new BizException("获取数据库连接失败");
        }
    }

    /**
     * 需要指定dataSourceBeanName
     * @param dataSourceBeanName
     * @return
     */
    public static MysqlServer create(String dataSourceBeanName) {
        DataSource dataSource = (DataSource) SpringContextUtil.getBean(dataSourceBeanName);
        return new MysqlServer(dataSource);
    }

    /**
     * 需要指定dataSource
     * @param dataSource
     * @return
     */
    public static MysqlServer create(DataSource dataSource) {
        return new MysqlServer(dataSource);
    }

    @Override
    public int insert(String sql, String... params) {
        return update(sql, params);
    }

    @Override
    public int update(String sql, String... params) {
        int i = 0;
        try {
            stmt = conn.prepareStatement(sql);
            for (int j = 1; j <= params.length; j++) {
                stmt.setString(j, params[j - 1]);
            }
            i = stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("执行该条sql失败：{}", sql);
            throw new BizException("sql执行异常", e);
        }
        return i;
    }

    @Override
    public int delete(String sql, String... params) {
        return update(sql, params);
    }

    @Override
    public Map<String, String> select(String sql, String... params) {
        Map<String, String> map;
        try {
            stmt = conn.prepareStatement(sql);
            for (int j = 1; j <= params.length; j++) {
                stmt.setString(j, params[j - 1]);
            }
            rs = stmt.executeQuery();
            map = resultSet2Map(rs);
        } catch (Exception e) {
            log.error("执行该条sql失败：{}", sql);
            throw new BizException("sql执行异常", e);
        }
        return map;
    }

    @Override
    public Map<String, String> select(String sql, int row, String... params) {
        Map<String, String> map;
        try {
            stmt = conn.prepareStatement(sql);
            for (int j = 1; j <= params.length; j++) {
                stmt.setString(j, params[j - 1]);
            }
            rs = stmt.executeQuery();
            map = getResultSetRowMap(rs, row - 1);
        } catch (Exception e) {
            log.error("执行该条sql失败：{}", sql);
            throw new BizException("sql执行异常", e);
        }
        return map;
    }

    @Override
    public List<Map<String, String>> selects(String sql, String... params) {
        List<Map<String, String>> list;
        try {
            stmt = conn.prepareStatement(sql);
            for (int j = 1; j <= params.length; j++) {
                stmt.setString(j, params[j - 1]);
            }
            rs = stmt.executeQuery();
            list = getResultSetList(rs);
        } catch (Exception e) {
            log.error("执行该条sql失败：{}", sql);
            throw new BizException("sql执行异常", e);
        }
        return list;
    }


    @Override
    public int selectRow(String sql, String... params) {
        int i = 0;
        try {
            stmt = conn.prepareStatement(sql);
            for (int j = 1; j <= params.length; j++) {
                stmt.setString(j, params[j - 1]);
            }
            rs = stmt.executeQuery();
            i = getResultSetRow(rs);
        } catch (Exception e) {
            log.error("执行该条sql失败：{}", sql);
            throw new BizException("sql执行异常", e);
        }
        return i;
    }

    private Map<String, String> resultSet2Map(ResultSet rs) throws Exception {
        Map<String, String> map = Maps.newHashMap();
        while (rs.next()) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String key = rsmd.getColumnLabel(i);
                String value = rs.getString(i);
                map.put(key, value);
            }
        }
        return map;
    }

    private Map<String, String> getResultSetRowMap(ResultSet rs, int row) throws SQLException {
        return getResultSetList(rs).get(row);
    }

    private List<Map<String, String>> getResultSetList(ResultSet rs) throws SQLException {
        List<Map<String, String>> list = Lists.newArrayList();

        ResultSetMetaData rsmd = rs.getMetaData();

        int columnCount = rsmd.getColumnCount();

        while (rs.next()) {
            Map<String, String> rowData = Maps.newHashMap();

            for (int i = 1; i <= columnCount; i++) {
                rowData.put(rsmd.getColumnName(i), rs.getString(i));
            }
            list.add(rowData);
        }
        return list;
    }

    private int getResultSetRow(ResultSet rs) throws Exception {
        int row = 0;
        while (rs.next()) {
            row = row + 1;
        }
        return row;
    }
}
