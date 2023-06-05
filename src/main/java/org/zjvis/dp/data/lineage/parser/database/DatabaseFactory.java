package org.zjvis.dp.data.lineage.parser.database;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author zhouyu
 * @create 2023-06-05 17:18
 */
@Component
public class DatabaseFactory {

    Map<String, DatabaseService> map = Maps.newHashMap();

    @Resource
    private List<DatabaseService> databaseServiceList;


    @PostConstruct
    public void init() {
        for (DatabaseService databaseService : databaseServiceList) {
            map.put(databaseService.getDatabaseType(), databaseService);
        }
    }

    public DatabaseService createDatabaseService(String sqlType) {
        return map.get(sqlType);
    }
}
