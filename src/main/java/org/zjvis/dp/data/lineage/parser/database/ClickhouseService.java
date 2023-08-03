package org.zjvis.dp.data.lineage.parser.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.zjvis.dp.data.lineage.constant.DatabaseConstant;
import org.zjvis.dp.data.lineage.enums.DataType;
import org.zjvis.dp.data.lineage.enums.SQLType;

/**
 * @author zhouyu
 * @create 2023-03-07 16:55
 */
@Component("dataLineageClickhouseService")
public class ClickhouseService extends RDMSService {

    public static final Map<String, String> CLICKHOUSE_DATA_TYPE_MAP = new HashMap<String, String>() {
        {
            put("Int8", DataType.NUMBER.name());
            put("Int16", DataType.NUMBER.name());
            put("Int32", DataType.NUMBER.name());
            put("Int64", DataType.NUMBER.name());
            put("Int128", DataType.NUMBER.name());
            put("Int256", DataType.NUMBER.name());

            put("Float32", DataType.NUMBER.name());
            put("Float64", DataType.NUMBER.name());
            put("Decimal", DataType.NUMBER.name());

            put("String", DataType.STRING.name());
            put("FixedString", DataType.STRING.name());

            put("Date", DataType.DATE.name());
            put("DateTime", DataType.DATE.name());
            put("DateTime64", DataType.DATE.name());
            put("Nullable(DateTime)", DataType.DATE.name());
        }
    };

    @Override
    public String getUrlFormat() {
        return DatabaseConstant.clickhouseUrlFormat;
    }

    @Override
    public String getUrlFormatWithoutDatabase() {
        return DatabaseConstant.clickhouseUrlWithoutDbNameFormat;
    }

    @Override
    public String getAllFieldSqlFormat() {
        return DatabaseConstant.mysqlAllFieldsSqlFormat;
    }

    @Override
    public String getIsTableExistSqlFormat() {
        return DatabaseConstant.clickhouseIsTableExistSqlFormat;
    }

    @Override
    public String getDatabaseType() {
        return SQLType.CLICKHOUSE.name();
    }

    @Override
    public String getAllDatabaseSqlFormat() {
        return DatabaseConstant.clickhouseAllDatabaseSql;
    }

    @Override
    public Map<String, String> getDataTypeMap() {
        return CLICKHOUSE_DATA_TYPE_MAP;
    }
}
