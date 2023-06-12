package org.zjvis.dp.data.lineage.parser.database;

import org.springframework.stereotype.Component;
import org.zjvis.dp.data.lineage.constant.DatabaseConstant;
import org.zjvis.dp.data.lineage.enums.SQLType;

/**
 * @author zhouyu
 * @create 2023-03-07 16:55
 */
@Component("dataLineageClickhouseService")
public class ClickhouseService extends RDMSService {

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
}
