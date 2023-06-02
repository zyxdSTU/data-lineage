package org.zjvis.dp.data.lineage.parser;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author zhouyu
 * @create 2023-05-31 15:31
 */
@Component
public class AstParserFactory {


    Map<String, AstParser> map = Maps.newHashMap();

    @Resource
    private List<AstParser> astParserList;


    @PostConstruct
    public void init() {
        for(AstParser astParser : astParserList) {
            map.put(astParser.getSQLType(), astParser);
        }
    }

    public AstParser createAstParser(String sqlType) {
        return map.get(sqlType);
    }
}
