package org.zjvis.dp.data.lineage.parser.database;

import com.alibaba.fastjson.JSONArray;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import org.zjvis.dp.data.lineage.data.ColumnInfo;
import org.zjvis.dp.data.lineage.data.DatabaseConfig;

/**
 * @author zhouyu
 * @create 2023-06-05 15:57
 */
public interface DatabaseService {

    List<ColumnInfo> getAllFields(DatabaseConfig databaseConfig, String databaseName, String tableName);

    Connection getConnection(DatabaseConfig databaseConfig);

    void connectionTest(DatabaseConfig databaseConfig);

    JSONArray executeSQL(Connection connection, String sql);

    String getUrlFormat();

    String getUrlFormatWithoutDatabase();

    String constructCompleteUrl(DatabaseConfig databaseConfig);

    String getAllFieldSqlFormat();

    String getAllDatabaseSqlFormat();

    String getIsTableExistSqlFormat();

    String getDatabaseType();

    List<String> getAllDatabase(DatabaseConfig databaseConfig);

    /***
     * 说明：目前只有postgres有schemaName
     * @param databaseConfig
     * @param databaseName
     * @return
     */
    List<String> getAllSchema(DatabaseConfig databaseConfig, String databaseName);

    Map<String, String> getDataTypeMap();

    String dataTypeConvert(String dataType);
}
