package org.zjvis.dp.data.lineage;

import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.zjvis.dp.data.lineage.data.DatabaseConfig;
import org.zjvis.dp.data.lineage.data.FieldLineageInfo;
import org.zjvis.dp.data.lineage.enums.SQLType;
import org.zjvis.dp.data.lineage.parser.AstParser;
import org.zjvis.dp.data.lineage.parser.AstParserFactory;
import org.zjvis.dp.data.lineage.parser.DataLineageParser;

@SpringBootTest
@RunWith(SpringRunner.class)
class LineageApplicationTests {
    @Resource
    private DataLineageParser dataLineageParser;

    @Test
    void contextLoads() {
    }

    @Test
    void testMysql() {
        String sql = "INSERT INTO money_laundering_predict\n"
                + "SELECT \n"
                + "\tuser_id,\n"
                + "\tcard_id,\n"
                + "\tCASE \n"
                + "\t\tWHEN is_multi_ip = 0 THEN 0\n"
                + "\t\tWHEN is_night_trade = 0 THEN 0\n"
                + "\t\tWHEN is_deposity_transfer = 0 THEN 0\n"
                + "\t\tWHEN counterparty_num < 10 THEN 0\n"
                + "\t\tWHEN trade_num < 20 THEN 0\n"
                + "\t\tWHEN age_type != 'young' THEN 0\n"
                + "\t\tWHEN job_type != 'student' THEN 0\n"
                + "\t\tELSE 1\n"
                + "\tEND\n"
                + "FROM money_laundering_feature;";
        DatabaseConfig databaseConfig = DatabaseConfig.builder()
                .host("10.5.24.18")
                .port(8124)
                .username("bigdata")
                .password("a123456")
                .databaseName("4_5_819")
                .build();
        List<FieldLineageInfo> fieldLineageInfoList = dataLineageParser.processFieldLineageParse(SQLType.CLICKHOUSE.name(), sql, databaseConfig);
        if(CollectionUtils.isNotEmpty(fieldLineageInfoList)) {
            for(FieldLineageInfo fieldLineageInfo : fieldLineageInfoList) {
                System.out.println(fieldLineageInfo);
            }
        }
    }
}
