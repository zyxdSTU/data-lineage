package org.zjvis.dp.data.lineage.parser.database;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.zjvis.dp.data.lineage.constant.DatabaseConstant;
import org.zjvis.dp.data.lineage.enums.DataType;
import org.zjvis.dp.data.lineage.enums.SQLType;

/**
 * @author zhouyu
 * @create 2023-06-05 16:46
 */
@Component("dataLineageMySQLService")
public class MySQLService extends RDMSService{
    public static final Map<String, String> MYSQL_DATA_TYPE_MAP = new HashMap<String, String>() {
        {
            put("bit", DataType.NUMBER.name());
            put("tinyint", DataType.NUMBER.name());
            put("smallint", DataType.NUMBER.name());
            put("int", DataType.NUMBER.name());
            put("mediumint", DataType.NUMBER.name());
            put("bigint", DataType.NUMBER.name());

            put("float", DataType.NUMBER.name());
            put("double", DataType.NUMBER.name());
            put("decimal", DataType.NUMBER.name());

            put("char", DataType.STRING.name());
            put("binary", DataType.STRING.name());
            put("varchar", DataType.STRING.name());
            put("varbinary", DataType.STRING.name());
            put("tinytext", DataType.STRING.name());
            put("text", DataType.STRING.name());
            put("mediumtext", DataType.STRING.name());
            put("longtext", DataType.STRING.name());
            put("tinyblob", DataType.STRING.name());
            put("mediumblob", DataType.STRING.name());
            put("blob", DataType.STRING.name());
            put("longblob", DataType.STRING.name());

            put("date", DataType.DATE.name());
            put("datetime", DataType.DATE.name());
            put("timestamp", DataType.DATE.name());
        }
    };

    @Override
    public String getUrlFormat() {
        return DatabaseConstant.mysqlUrlFormat;
    }

    @Override
    public String getUrlFormatWithoutDatabase() {
        return DatabaseConstant.mysqlUrlWithoutDbNameFormat;
    }

    @Override
    public String getAllFieldSqlFormat() {
        return DatabaseConstant.mysqlAllFieldsSqlFormat;
    }

    @Override
    public String getIsTableExistSqlFormat() {
        return DatabaseConstant.mysqlIsTableExistSqlFormat;
    }

    @Override
    public String getDatabaseType() {
        return SQLType.MYSQL.name();
    }

    @Override
    public String getAllDatabaseSqlFormat() {
        return DatabaseConstant.mysqlAllDatabaseSql;
    }

    @Override
    public Map<String, String> getDataTypeMap() {
        return MYSQL_DATA_TYPE_MAP;
    }

}