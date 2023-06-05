package org.zjvis.dp.data.lineage.parser.database;

import org.springframework.stereotype.Component;
import org.zjvis.dp.data.lineage.constant.DatabaseConstant;
import org.zjvis.dp.data.lineage.enums.SQLType;

/**
 * @author zhouyu
 * @create 2023-06-05 16:46
 */
@Component
public class MySQLService extends RDMSService{
    @Override
    public String getUrlFormat() {
        return DatabaseConstant.mysqlUrlFormat;
    }

    @Override
    public String getUrlWithoutDatabase() {
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
}
