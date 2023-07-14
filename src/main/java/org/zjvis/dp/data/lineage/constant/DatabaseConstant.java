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

    public static final String mysqlIsTableExistSqlFormat = "select "
            + "table_name, "
            + "table_type, "
            + "engine "
            + "FROM information_schema.tables "
            + "WHERE TABLE_SCHEMA = '%s' and TABLE_NAME = '%s';";



    /**
     * Clickhouse DDL
     */
    public static final String clickhouseUrlFormat = "jdbc:clickhouse://%s:%s/%s?";

    public static final String clickhouseUrlWithoutDbNameFormat = "jdbc:clickhouse://%s:%s";

    public static final String clickhouseAllDatabaseSql = "show databases";

    public static final String clickhouseAllTableSql = "show tables";

    public static final String clickhouseIsTableExistSqlFormat = "select "
            + "database, "
            + "name "
            + "from system.tables "
            + "where database = '%s' "
            + "and name = '%s' ";


    /**
     *  PostgreSQL DDL
     */
    public static final String postgresUrlFormat = "jdbc:postgresql://%s:%s/%s";

    public static final String postgresUrlFormatWithSchemaName = "jdbc:postgresql://%s:%s/%s?currentSchema=%s";

    /**
     * 有 2 个数据库可用于初始连接 - “template1”和“postgres”。
     */
    public static final String postgresUrlWithoutDbNameFormat = "jdbc:postgresql://%s:%s/postgres";

    public static final String postgresAllDatabaseSql = "select datname from pg_database;";

    public static final String postgresAllSchemaSql = "select nspname from pg_namespace;";

    public static final String postgresAllTableSql = "select tablename from pg_tables where schemaname='%s';";

    /**
     * 添加namesp.nspname = '%s',输出字段是当前schema.table
     */
    public static final String postgreAllFieldsSqlFormat = "SELECT "
            + "attr.attname AS columnName, "
            + "format_type(attr.atttypid, attr.atttypmod) AS columnType, "
            + "col_description(attr.attrelid, attr.attnum) as columnComment "
            + "FROM pg_attribute attr "
            + "INNER JOIN pg_class cla ON attr.attrelid = cla.oid "
            + "INNER JOIN pg_namespace namesp ON cla.relnamespace = namesp.oid "
            + "where cla.relname = '%s' AND namesp.nspname = '%s' AND attr.attnum > 0;";



    /**
     *  Oracle DDL
     *   jdbc:oracle:thin:@10.5.24.112:1521:XEw
     *   testpdb/testpdb
     */
    public static final String oracleSIDUrlFormat = "jdbc:oracle:thin:@%s:%s:%s";

    public static final String oracleServerNameUrlFormat = "jdbc:oracle:thin:@//%s:%s/%s";

    /**
     * 返回所有的用户模式
     */
    public static final String oracleAllSchemaSql = "select DISTINCT owner from dba_tables";

    /**
     * 注意：oracle会自动转大写、oracle不需要加逗号;  否则会出现 SQL 命令未正确结束
     */
    public static final String oracleAllTableSql = "select table_name from dba_tables where owner='%s'";

    public static final String oracleAllFieldSql = "select "
            + "a.column_name as columnName, "
            + "a.data_type as columnType, "
            + "b.comments as columnComment "
            + "from dba_tab_columns a "
            + "LEFT JOIN dba_col_comments b "
            + "ON a.OWNER = b.OWNER AND a.TABLE_NAME = b.TABLE_NAME "
            + "AND a.COLUMN_NAME = b.COLUMN_NAME "
            + "WHERE a.owner = '%s' AND a.table_name = '%s'";


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

    /**
     * 注意：两个点 表名..SysObjects
     */
    public static final String sqlServerAllTableSql = "select Name as tablename from %s..SysObjects Where XType='U';";

    /**
     * 注：SysColumns和sys.columns表记录数据相同，但字段不同，需注意区分
     */
    public static final String sqlServerAllFieldSql = "select "
            + "sc.name as columnName, "
            + "st.name as columnType, "
            + "se.value as columnComment "
            + "from sys.objects so "
            + "INNER JOIN sys.columns sc on so.object_id=sc.object_id "
            + "INNER JOIN sys.types st on st.system_type_id=sc.system_type_id "
            + "LEFT JOIN sys.extended_properties se on se.major_id=so.object_id and se.minor_id=sc.column_id "
            + "where so.name='%s';";

    public static final Integer DEFAULT_LOGIN_TIME_OUT = 5;
}
