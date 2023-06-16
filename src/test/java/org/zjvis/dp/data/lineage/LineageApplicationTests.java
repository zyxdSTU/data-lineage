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
        String sql = "insert into student_school\n"
                + "select \n"
                + "    student.student_id,\n"
                + "    concat(student.student_name, school.school_name), \n"
                + "    school.* \n"
                + "from student\n"
                + "join school\n"
                + "on student.school_id = school.school_id;";
        DatabaseConfig databaseConfig = DatabaseConfig.builder()
                .host("10.5.24.98")
                .port(3306)
                .username("root")
                .password("Zhejianglab@123")
                .databaseName("test")
                .build();
        List<FieldLineageInfo> fieldLineageInfoList = dataLineageParser.processFieldLineageParse(SQLType.MYSQL.name(), sql, databaseConfig);
        if(CollectionUtils.isNotEmpty(fieldLineageInfoList)) {
            for(FieldLineageInfo fieldLineageInfo : fieldLineageInfoList) {
                System.out.println(fieldLineageInfo);
            }
        }
    }
}
