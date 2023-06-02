package org.zjvis.dp.data.lineage.constant;

/**
 * @author baishuailei@zhejianglab.com
 * @date 2022/8/30 13:51
 */
public class DatabaseConstant {

    /**
     *  MySQL DDL
     */
    public static final String mysqlUrlFormat = "jdbc:mysql://%s:%s/%s?"
            + "useUnicode=true"
            + "&characterEncoding=utf8"
            + "&useSSL=false"
            + "&useLegacyDatetimeCode=false"
            + "&serverTimezone=Asia/Shanghai"
            + "&autoReconnect=true";

    public static final String mysqlUrlWithoutDbNameFormat = "jdbc:mysql://%s:%s?"
            + "useUnicode=true"
            + "&characterEncoding=utf8"
            + "&useSSL=false"
            + "&useLegacyDatetimeCode=false"
            + "&serverTimezone=Asia/Shanghai"
            + "&autoReconnect=true";

    public static final String mysqlAllDatabaseSql = "show databases";

    public static final String mysqlAllTableSql = "show tables";

    public static final String mysqlAllFieldsSqlFormat = "select "
            + "column_name as columnName, "
            + "data_type as columnType, "
            + "column_comment as columnComment "
            + "from information_schema.columns "
            + "where table_schema='%s' and table_name='%s';";

    public static final String isTableExistSqlFormat = "select "
            + "database, "
            + "name "
            + "from system.tables "
            + "where database = '%s' "
            + "and name = '%s' ";

    // %s=databaseName、%s=tableName  concat(round(sum(DATA_LENGTH/1024/1024), 4),' mb')
    public static final String mysqlInfoSqlFormat = "select UPDATE_TIME AS last_modified, TABLE_ROWS AS total_rows, (DATA_LENGTH+INDEX_LENGTH) AS total_size " +
            "from information_schema.TABLES where TABLE_SCHEMA='%s' and TABLE_NAME='%s';";

    /**
     * 支持MySQL、Postgre、Clickhouse
     * 注：postgre不支持``
     */
    public static final String getDataSqlFormat = "select %s from %s limit %s;";

    /**
     * 选取所有数据
     */
    public static final Integer noLimit = -1;
    public static final String getDataNoLimitSqlFormat = "select %s from %s;";


    /**
     *  PostgreSQL DDL
     */
    public static final String postgreUrlFormat = "jdbc:postgresql://%s:%s/%s";

    public static final String postgreUrlFormatWithSchemaName = "jdbc:postgresql://%s:%s/%s?currentSchema=%s";

    // 有 2 个数据库可用于初始连接 - “template1”和“postgres”。
    public static final String postgreUrlWithoutDbNameFormat = "jdbc:postgresql://%s:%s/postgres";

    public static final String postgreAllDatabaseSql = "select datname from pg_database;";

    public static final String postgreAllSchemaSql = "select nspname from pg_namespace;";

    public static final String postgreAllTableSql = "select tablename from pg_tables where schemaname='%s';";

    // 添加namesp.nspname = '%s',输出字段是当前schema.table
    public static final String postgreAllFieldsSqlFormat = "SELECT "
            + "attr.attname AS columnName, "
            + "format_type(attr.atttypid, attr.atttypmod) AS columnType, "
            + "col_description(attr.attrelid, attr.attnum) as columnComment "
            + "FROM pg_attribute attr "
            + "INNER JOIN pg_class cla ON attr.attrelid = cla.oid "
            + "INNER JOIN pg_namespace namesp ON cla.relnamespace = namesp.oid "
            + "where cla.relname = '%s' AND namesp.nspname = '%s' AND attr.attnum > 0;";

    public static final String postgreInfoSqlFormat = "select n_live_tup as total_rows, pg_table_size(pgc.oid) AS total_size from pg_class pgc " +
            "LEFT JOIN pg_stat_user_tables pgs on pgc.oid=pgs.relid " +
            "where pgc.relname = '%s';";

    /**
     * Clickhouse DDL
     */
    public static final String clickhouseUrlFormat = "jdbc:clickhouse://%s:%s/%s?";

    public static final String clickhouseUrlWithoutDbNameFormat = "jdbc:clickhouse://%s:%s";

    public static final String clickhouseAllDatabaseSql = "show databases";

    public static final String clickhouseAllTableSql = "show tables";

    public static final String clickhouseCreateDatabaseSql = "create database if not exists %s ON CLUSTER cluster01";

    public static final String systemDatabase = "system";

    public static final String clickhouseInfoSqlFormat = "select "
            + "sum(bytes) as total_size, "
            + "sum(rows) as total_rows, "
            + "max(modification_time) as last_modified "
            + "from remote('%s:%d', 'system', 'parts', '%s', '%s') "
            + "where database = '%s' and table = '%s' "
            + "and active;";

    public static final String clickhouseAsynchronousInfoSqlFormat = "select "
            + "metric, "
            + "value "
            + "from remote('%s:%d', 'system', 'asynchronous_metrics', '%s', '%s') "
            + "where metric in ('MemoryResident','MemoryShared','MemoryVirtual','NumberOfDatabases','NumberOfTables','Uptime');";

    public static final String clickhousMetricsInfoSqlFormat = "select "
            + "metric, "
            + "value "
            + "from remote('%s:%d', 'system', 'metrics', '%s', '%s') "
            + "where metric in ('Query','HTTPConnection','QueryThread','MemoryTracking');";

    public static final String clickhouseInfoSqlFormatNoRemote = "select "
            + "sum(bytes) as total_size, "
            + "sum(rows) as total_rows, "
            + "max(modification_time) as last_modified "
            + "from `system`.parts "
            + "where database = '%s' and table = '%s'; ";

    public static final String getClickhouseClusterInfoSql = "select "
            + "shard_num as shardNumber, "
            + "host_address as host, "
            + "port as port "
            + "from `clusters`; ";

    // ddl语句
    public static final String clickhouseDDLSqlFormat = "show create table %s;";

    // select语句
    public static final String clickhouseSelectSqlFormat = "select \n\t %s \n from %s.%s;";

    // 统计app调用次数
    public static final String clickhouseCountAppAccessNumSql = "select count(*) as call_num from dp.api_access_record where app_id = %s;";


    /**
     *  Oracle DDL
     *   jdbc:oracle:thin:@10.5.24.112:1521:XEw
     *   testpdb/testpdb
     */
    public static final String oracleSIDUrlFormat = "jdbc:oracle:thin:@%s:%s:%s";

    public static final String oracleServerNameUrlFormat = "jdbc:oracle:thin:@//%s:%s/%s";

    // 返回所有的用户模式
    public static final String oracleAllSchemaSql = "select DISTINCT owner from dba_tables";

    // 注：oracle会自动转大写、oracle不需要加逗号;  否则会出现 SQL 命令未正确结束
    public static final String oracleAllTableSql = "select table_name from dba_tables where owner='%s'";

    public static final String oracleAllFieldSql = "select " +
            "a.column_name as columnName, a.data_type as columnType, b.comments as columnComment " +
            "from dba_tab_columns a LEFT JOIN dba_col_comments b ON a.OWNER = b.OWNER AND a.TABLE_NAME = b.TABLE_NAME " +
            "AND a.COLUMN_NAME = b.COLUMN_NAME " +
            "WHERE a.owner = '%s' AND a.table_name = '%s'";

    // 注：oracle jdbc查询需要带上双引号, 字段和表名
    public static final String oracleDataSqlFormat = "select %s from \"%s\".\"%s\" where rownum <= %s";

    public static final String oracleInfoSqlFormat = "select " +
            "o.LAST_DDL_TIME as last_modified, t.num_rows as total_rows, s.bytes as total_size\n" +
            "from dba_objects o left join dba_segments s on s.segment_name=o.object_name\n" +
            "left join dba_tables t on t.table_name=o.object_name\n" +
            "where o.OWNER = '%s' and o.object_name='%s'";

    /**
     * Sql server DDL
     * jdbc:sqlserver://10.5.24.19:1433/dp
     * username: SA
     * password: Zjlab1234
     * 连接数据库
     * jdbc:sqlserver://ip:hort;DatabaseName=dataname
     * 查询某库的所有表
     * select Name from 表名..SysObjects Where XType='U'; xtype='U':表示所有用户表，xtype='S':表示所有系统表。
     */
    public static final String sqlServerUrlFormat = "jdbc:sqlserver://%s:%s;DatabaseName=%s";

    public static final String sqlServerUrlWithoutDbNameFormat = "jdbc:sqlserver://%s:%s";

    public static final String sqlServerAllDatabaseSql = "select name from SysDatabases;";

    // 注意：两个点 表名..SysObjects
    public static final String sqlServerAllTableSql = "select Name as tablename from %s..SysObjects Where XType='U';";

    // 注：SysColumns和sys.columns表记录数据相同，但字段不同，需注意区分
    public static final String sqlServerAllFieldSql = "select sc.name as columnName, st.name as columnType, se.value as columnComment\n" +
            "from sys.objects so \n" +
            "INNER JOIN sys.columns sc on so.object_id=sc.object_id\n" +
            "INNER JOIN sys.types st on st.system_type_id=sc.system_type_id\n" +
            "LEFT JOIN sys.extended_properties se on se.major_id=so.object_id and se.minor_id=sc.column_id\n" +
            "where so.name='%s';";

    public static final String sqlServerDataSqlFormat = "select top %s %s from %s;";

    // 注：总大小需*8转化成kb计算
    // todo check
    public static final String sqlServerInfoSqlFormat = "select o.refdate as last_modified, p.rows as total_rows, (a.used_pages*8*1024) as total_size \n" +
            "from SysObjects o \n" +
            "INNER join SysIndexes as b ON o.id=b.id \n" +
            "INNER JOIN sys.partitions p on o.id=p.object_id \n" +
            "INNER JOIN sys.allocation_units a on p.partition_id = a.container_id \n" +
            "where o.XType='u' and p.index_id=1 and b.indid in(0,1) and o.name = '%s';";

}
