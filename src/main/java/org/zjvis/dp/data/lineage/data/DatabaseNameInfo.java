package org.zjvis.dp.data.lineage.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouyu
 * @create 2023-07-10 10:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DatabaseNameInfo {
    private String name;
    private String Database;

    public String getSpecificDatabaseName() {
        if(StringUtils.isNotEmpty(name)) {
            return name;
        }

        if(StringUtils.isNotEmpty(Database)) {
            return Database;
        }

        return null;
    }
}
