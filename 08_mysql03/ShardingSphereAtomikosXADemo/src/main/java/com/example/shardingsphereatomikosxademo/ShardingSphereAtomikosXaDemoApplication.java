package com.example.shardingsphereatomikosxademo;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author luf
 */
public class ShardingSphereAtomikosXaDemoApplication {
    public static void main(String[] args) throws IOException, SQLException {
        // 获取数据源，dataSource的类型为ShardingDataSource
        DataSource dataSource = getShardingDatasource();
        // 清空t_order表数据
        cleanupTableData(dataSource);
        // 支持TransactionType.LOCAL, TransactionType.XA, TransactionType.BASE
        TransactionTypeHolder.set(TransactionType.XA);

        Connection conn = dataSource.getConnection();
        String sql = "insert into t_order (order_id, user_id) VALUES (?, ?);";

        System.out.println("The first XA starts to execute");
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            // 关闭自动提交
            conn.setAutoCommit(false);
            for (int i = 1; i < 11; i++) {
                // .setLong：第一个参数，是标明SQL第一个参数，第二个参数才是实际值
                statement.setLong(1, i);
                statement.setLong(2, i);
                statement.executeUpdate();
            }
            conn.commit();
        }
        System.out.println("The first XA is executed successfully");

        System.out.println("The second XA starts to execute");
        // 表t_order的主键为 order_id
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (int i = 1; i < 11; i++) {
                // 制造主键order_id冲突
                statement.setLong(1, i + 5);
                statement.setLong(2, i + 1000);
                statement.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            System.out.println("The second XA failed to execute successfully");
            conn.rollback();
        } finally {
            conn.close();
        }
    }

    private static void cleanupTableData(DataSource dataSource) {
        System.out.println("Delete all data...");
        try (Connection conn = dataSource.getConnection(); Statement statement = conn.createStatement()) {
            statement.execute("delete from t_order;");
            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Delete all data successful");
    }

    /**
     * 生成DataSource，注意文件路径
     */
    static private DataSource getShardingDatasource() throws IOException, SQLException {
        String fileName = "sharding-config.yml";
        // File：文件和目录路径名的抽象表示。此类的实例可能表示也可能不表示实际的文件系统对象，例如文件或目录。
        //File 类的实例是不可变的；也就是说，一旦创建，由 File 对象表示的抽象路径名将永远不会改变
        // .getResource()：查找具有给定名称的资源，返回用于读取资源的 URL 对象
        // .getPath()：获取此 URL 的路径部分
        File yamlFile = new File(ShardingSphereAtomikosXaDemoApplication.class.getClassLoader().getResource(fileName).getPath());
        //System.out.println("****************************");
        //// /Users/luf/IdeaProjects/JavaCourseCodes7/08_mysql03/ShardingSphereAtomikosXADemo/target/classes/sharding-config.yml
        //System.out.println(yamlFile.toPath());
        //// /Users/luf/IdeaProjects/JavaCourseCodes7/08_mysql03/ShardingSphereAtomikosXADemo/target/classes/sharding-config.yml
        //System.out.println(yamlFile.toString());
        //// file:/Users/luf/IdeaProjects/JavaCourseCodes7/08_mysql03/ShardingSphereAtomikosXADemo/target/classes/sharding-config.yml
        //System.out.println(yamlFile.toURI());
        //System.out.println("****************************");
        return YamlShardingSphereDataSourceFactory.createDataSource(yamlFile);
    }
}