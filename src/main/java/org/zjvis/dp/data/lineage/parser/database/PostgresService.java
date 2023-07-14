package org.zjvis.dp.data.lineage.parser.database;

import java.util.Map;
import org.springframework.stereotype.Component;
import org.zjvis.dp.data.lineage.constant.DatabaseConstant;
import org.zjvis.dp.data.lineage.enums.SQLType;

/**
 * @author zhouyu
 * @create 2023-07-11 11:01
 */
@Component("dataLineagePostgresService")
public class PostgresService extends RDMSService{
    @Override
    public String getUrlFormat() {
        return DatabaseConstant.postgresUrlFormat;
    }

    @Override
    public String getUrlFormatWithoutDatabase() {
        return DatabaseConstant.postgresUrlWithoutDbNameFormat;
    }

    @Override
    public String getAllFieldSqlFormat() {
        return DatabaseConstant.postgreAllFieldsSqlFormat;
    }

    @Override
    public String getIsTableExistSqlFormat() {
        //todo 后面需要完善
        return null;
    }

    @Override
    public String getDatabaseType() {
        return SQLType.POSTGRES.name();
    }

    @Override
    public String getAllDatabaseSqlFormat() {
        return DatabaseConstant.postgresAllDatabaseSql;
    }

    @Override
    public Map<String, String> getDataTypeMap() {
        return null;
    }
}
