package org.zjvis.dp.data.lineage.parser.database;

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
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zjvis.dp.data.lineage.constant.DatabaseConstant;
import org.zjvis.dp.data.lineage.data.ColumnDTO;
import org.zjvis.dp.data.lineage.data.DBConfig;
import org.zjvis.dp.data.lineage.exception.CommonException;

/**
 * @author zhouyu
 * @create 2023-03-07 16:55
 */
@Service
public class LocalClickhouseService {


    @Value("${clickhouse.user}")
    private String userName;

    @Value("${clickhouse.password}")
    private String password;

    @Value("${clickhouse.host}")
    private String host;

    @Value("${clickhouse.port}")
    private Integer port;

    /**
     * 后端目前固定的关系目标源配置
     */
    private DBConfig dbLocalConfig;

    public static final String DATE_TIME = "dt";

    public static final Integer LOGIN_TIMEOUT = 5;

    private final static Logger logger = LoggerFactory.getLogger("LocalClickhouseService");

    /**
     * 初始化关系目标源
     */
    @PostConstruct
    public void initDBLocalConfig() {
        dbLocalConfig = new DBConfig();
        dbLocalConfig.setHost(host);
        dbLocalConfig.setPort(port);
        dbLocalConfig.setUsername(userName);
        dbLocalConfig.setPassword(password);
    }


    public List<ColumnDTO> getAllFields(String databaseName, String tableName) {
        if(StringUtils.isAnyEmpty(databaseName, tableName)) {
            return Lists.newArrayList();
        }
        Connection connection = getClickhouseConnection(DatabaseConstant.clickhouseUrlFormat, dbLocalConfig, databaseName,
                LOGIN_TIMEOUT);

        //复用mysql查询字段语句
        JSONArray jsonArray = executeSQL(connection, String.format(DatabaseConstant.mysqlAllFieldsSqlFormat, databaseName, tableName));

        List<ColumnDTO> columnDTOList = JSON.parseArray(jsonArray.toJSONString(), ColumnDTO.class);

        //过滤掉保留字段
        return columnDTOList.stream()
                .filter(columnDTO -> !columnDTO.getColumnName().equals(DATE_TIME))
                .collect(Collectors.toList());

    }


    /**
     * 查询clickhouse表是否存在
     * @param databaseName
     * @param tableName
     * @return
     */
    public boolean tableIsExist(String databaseName, String tableName) {
        if(StringUtils.isAnyEmpty(databaseName, tableName)) {
            return Boolean.FALSE;
        }
        Connection connection = getClickhouseConnection(DatabaseConstant.clickhouseUrlWithoutDbNameFormat, dbLocalConfig, null,
                LOGIN_TIMEOUT);

        JSONArray jsonArray = executeSQL(connection, String.format(DatabaseConstant.isTableExistSqlFormat, databaseName, tableName));

        if(CollectionUtils.isEmpty(jsonArray)) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }

    }

    public Connection getClickhouseConnection(String urlFormat, DBConfig dbConfig, String databaseName, Integer loginTimeout) {
        try {
            String url;
            if (StringUtils.isNotBlank(databaseName)) {
                url = String.format(urlFormat, dbConfig.getHost(), dbConfig.getPort(),
                        databaseName);
            } else {
                url = String.format(urlFormat, dbConfig.getHost(),
                        dbConfig.getPort());
            }

            Properties properties = new Properties();
            properties.setProperty("user", dbConfig.getUsername());
            properties.setProperty("password", dbConfig.getPassword());
            //对于postgres setLoginTimeout可能失效, 所以参数属性里面塞connectTimeout值, 单位秒
            if(urlFormat.contains("postgresql")) {
                properties.setProperty("connectTimeout", String.valueOf(loginTimeout));
            } else {
                DriverManager.setLoginTimeout(loginTimeout);
            }
            return DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            throw new CommonException(e.getMessage());
        }
    }

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
            throw new CommonException(e.getMessage());
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }

}
