package org.zjvis.dp.data.lineage.parser.database;

import com.alibaba.fastjson.JSONArray;
import java.sql.Connection;
import java.util.List;
import org.zjvis.dp.data.lineage.data.ColumnInfo;
import org.zjvis.dp.data.lineage.data.DatabaseConfig;

/**
 * @author zhouyu
 * @create 2023-06-05 15:57
 */
public interface DatabaseService {

    List<ColumnInfo> getAllFields(DatabaseConfig databaseConfig, String databaseName, String tableName);

    Connection getConnection(DatabaseConfig databaseConfig);

    JSONArray executeSQL(Connection connection, String sql);

    String getUrlFormat();

    String getUrlWithoutDatabase();

    String constructCompleteUrl(DatabaseConfig databaseConfig);

    String getAllFieldSqlFormat();

    String getIsTableExistSqlFormat();

    String getDatabaseType();
}
