package com.ly.core.db;

import java.util.List;
import java.util.Map;

/**
 * 数据库操接口
 * @author luoy
 *
 */
 public interface BaseDbServer {

  /**
   * PreparedStatement 改
   * @param sql
   * @param params
   * @return
   */
   int update(String sql, String... params);

  /**
   * PreparedStatement 增
   * @param sql
   * @param params
   * @return
   */
   int insert(String sql, String... params);

  /**
   * PreparedStatement 删
   * @param sql
   * @param params
   * @return
   */
   int delete(String sql, String... params);

  /**
   * PreparedStatement 查单行
   * @param sql
   * @param params
   * @return
   */
   Map<String, String> select(String sql, String... params);

  /**
   * PreparedStatement 多行数据中的第row行 row从0开始
   * @param sql
   * @param params
   * @return
   */
   Map<String, String> select(String sql, int row, String... params);

  /**
   * PreparedStatement 查所有数据
   * @param sql
   * @param params
   * @return
   */
   List<Map<String, String>> selects(String sql, String... params);

  /**
   * PreparedStatement 返回查询到的结果集行数量
   * @param sql
   * @return
   */
   int selectRow(String sql, String... params);
}
