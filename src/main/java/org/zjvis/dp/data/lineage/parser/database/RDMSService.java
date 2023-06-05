package org.zjvis.dp.data.lineage.parser.database;

import static org.zjvis.dp.data.lineage.constant.DatabaseConstant.DEFAULT_LOGIN_TIME_OUT;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;
import org.zjvis.dp.data.lineage.data.ColumnInfo;
import org.zjvis.dp.data.lineage.data.DatabaseConfig;
import org.zjvis.dp.data.lineage.exception.DataLineageException;

/**
 * @author zhouyu
 * @create 2023-06-05 16:02
 */
public abstract class RDMSService implements DatabaseService {

    public static final String DATE_TIME = "dt";

    @Override
    public List<ColumnInfo> getAllFields(DatabaseConfig databaseConfig, String databaseName, String tableName) {
        if (StringUtils.isAnyEmpty(databaseName, tableName)) {
            return Lists.newArrayList();
        }

        Connection connection = getConnection(databaseConfig);

        //复用mysql查询字段语句
        JSONArray jsonArray = executeSQL(connection, String.format(getAllFieldSqlFormat(), databaseName, tableName));

        List<ColumnInfo> columnInfoList = JSON.parseArray(jsonArray.toJSONString(), ColumnInfo.class);

        //过滤掉保留字段
        return columnInfoList.stream()
                .filter(columnInfo -> !columnInfo.getColumnName().equals(DATE_TIME))
                .collect(Collectors.toList());
    }


    /**
     * 查询clickhouse表是否存在
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    public boolean tableIsExist(DatabaseConfig databaseConfig, String databaseName, String tableName) {
        if (StringUtils.isAnyEmpty(databaseName, tableName)) {
            return Boolean.FALSE;
        }
        Connection connection = getConnection(databaseConfig);

        JSONArray jsonArray = executeSQL(connection, String.format(getIsTableExistSqlFormat(), databaseName, tableName));

        if (CollectionUtils.isEmpty(jsonArray)) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }

    }

    @Override
    public Connection getConnection(DatabaseConfig databaseConfig) {
        try {
            String sqlUrl = constructCompleteUrl(databaseConfig);

            Integer loginTimeout;
            if (Objects.isNull(databaseConfig.getLoginTimeout()) || databaseConfig.getLoginTimeout() == 0) {
                loginTimeout = DEFAULT_LOGIN_TIME_OUT;
            } else {
                loginTimeout = databaseConfig.getLoginTimeout();
            }

            Properties properties = new Properties();
            properties.setProperty("user", databaseConfig.getUsername());
            properties.setProperty("password", databaseConfig.getPassword());
            //对于postgres setLoginTimeout可能失效, 所以参数属性里面塞connectTimeout值, 单位秒
            if (sqlUrl.contains("postgresql")) {
                properties.setProperty("connectTimeout", String.valueOf(loginTimeout));
            } else {
                DriverManager.setLoginTimeout(loginTimeout);
            }
            return DriverManager.getConnection(sqlUrl, properties);
        } catch (SQLException e) {
            throw new DataLineageException(e.getMessage());
        }
    }

    @Override
    public JSONArray executeSQL(Connection connection, String sql) {
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            JSONArray resultJsonArray = new JSONArray();
            while (rs.next()) {
                JSONObject resultJsonElement = new JSONObject();
                for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                    String name = metaData.getColumnLabel(i);
                    resultJsonElement.put(name, rs.getObject(name));
                }
                resultJsonArray.add(resultJsonElement);
            }
            return resultJsonArray;
        } catch (SQLException e) {
            //获取详细异常
            throw new DataLineageException(e.getMessage());
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }


    @Override
    public String constructCompleteUrl(DatabaseConfig databaseConfig) {
        String result;
        if (StringUtils.isNotBlank(databaseConfig.getDatabaseName())) {
            result = String.format(
                    getUrlFormat(),
                    databaseConfig.getHost(),
                    databaseConfig.getPort(),
                    databaseConfig.getDatabaseName()
            );
        } else {
            result = String.format(
                    getUrlWithoutDatabase(),
                    databaseConfig.getHost(),
                    databaseConfig.getPort()
            );
        }
        return result;
    }
}
