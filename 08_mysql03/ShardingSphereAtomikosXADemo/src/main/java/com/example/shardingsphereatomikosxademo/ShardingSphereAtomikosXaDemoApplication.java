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
        DataSource dataSource = getShardingDatasource();
        cleanupData(dataSource);

        TransactionTypeHolder.set(TransactionType.XA);

        Connection conn = dataSource.getConnection();
        String sql = "insert into t_order (u_id, o_id) VALUES (?, ?);";

        System.out.println("First XA Start insert data");
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (int i = 1; i < 11; i++) {
                statement.setLong(1, i);
                statement.setLong(2, (long) (Math.random() * 100));
                statement.executeUpdate();
            }
            //conn.commit();
        }
        System.out.println("First XA inserted successful");
    }

    private static void cleanupData(DataSource dataSource) {
        System.out.println("Delete all Data...");
        try (Connection conn = dataSource.getConnection(); Statement statement = conn.createStatement()) {
            statement.execute("delete from t_order;");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Delete all Data successful");
    }

    /**
     * 生成DataSource，注意文件路径
     */
    static private DataSource getShardingDatasource() throws IOException, SQLException {
        String fileName = "sharding-config.yml";
        File yamlFile = new File(ShardingSphereAtomikosXaDemoApplication.class.getClassLoader().getResource(fileName).getPath());
        return YamlShardingSphereDataSourceFactory.createDataSource(yamlFile);
    }
}
