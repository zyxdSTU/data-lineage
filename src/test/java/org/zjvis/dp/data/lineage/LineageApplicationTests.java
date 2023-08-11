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
    void testClickhouse() {
        String sql = "insert into student_school\n"
                + "select \n"
                + "    student.student_id,\n"
                + "    student.student_name,\n"
                + "    school_alias.school_id,\n"
                + "    school_alias.school_name,\n"
                + "    school_alias.school_tel\n"
                + "from student\n"
                + "global join (\n"
                + "    select\n"
                + "        school_id,\n"
                + "        school_name,\n"
                + "        school_tel\n"
                + "    from \n"
                + "    school\n"
                + ") school_alias\n"
                + "on student.school_id = school_alias.school_id;";
        DatabaseConfig databaseConfig = DatabaseConfig.builder()
                .host("10.5.24.18")
                .port(8124)
                .username("bigdata")
                .password("a123456")
                .databaseName("4_5_334")
                .build();
        List<FieldLineageInfo> fieldLineageInfoList = dataLineageParser.processFieldLineageParse(SQLType.CLICKHOUSE.name(), sql, databaseConfig);
        if(CollectionUtils.isNotEmpty(fieldLineageInfoList)) {
            for(FieldLineageInfo fieldLineageInfo : fieldLineageInfoList) {
                System.out.println(fieldLineageInfo);
            }
        }
    }

    @Test
    void testMysql() {
        String sql = "INSERT INTO student(student_id)\n"
                + "SELECT\n"
                + "\tcase \n"
                + "\t\tWHEN student_id > 10 THEN 0\n"
                + "\t\tWHEN student_id <= 10 THEN 1\n"
                + "\tELSE 2\n"
                + "\tEND\n"
                + "FROM student_test;";
        DatabaseConfig databaseConfig = DatabaseConfig.builder()
                .host("10.5.24.98")
                .port(3306)
                .username("root")
                .password("Zhejianglab@123")
                .databaseName("test")
                .build();
        List<FieldLineageInfo> fieldLineageInfoList = dataLineageParser.processFieldLineageParse(SQLType.MYSQL.name(), sql, null);
        if(CollectionUtils.isNotEmpty(fieldLineageInfoList)) {
            for(FieldLineageInfo fieldLineageInfo : fieldLineageInfoList) {
                System.out.println(fieldLineageInfo);
            }
        }
    }
}
