package org.example;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private String url = "jdbc:mysql://localhost:3306/test_java7?useSSL=false&useUnicode=true&characterEncoding=UTF8&characterSetResults=UTF8&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true";
    private String username = "root";
    private String password = "abc123456";

    @Test
    public void testJdbcBatchInsert() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            String sql = "INSERT INTO order_detail(id,order_id,product_id,product_name,product_cnt,product_price,average_cost,weight,fee_money,actual_money,modified_time) " +
                    "VALUES(null,1,1,?,8,8.88,6.66,6.88,1.88,88.88,?)";
            preparedStatement = connection.prepareStatement(sql);
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                preparedStatement.setString(1, "商品" + i);
                preparedStatement.setString(2, String.valueOf(System.currentTimeMillis()));
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            long end = System.currentTimeMillis();
            System.out.println("插入百万条数据用时：" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
