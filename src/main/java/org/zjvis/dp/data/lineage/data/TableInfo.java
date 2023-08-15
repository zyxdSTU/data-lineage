package org.zjvis.dp.data.lineage.data;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouyu
 * @create 2023-02-21 11:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableInfo {

    String tableName;
    String databaseName;

    /**
     * 库名默认为default
     */
    public static final String DEFAULT_DATABASE_NAME = "default";

    public boolean isBlank() {
        return StringUtils.isEmpty(tableName);
    }

    public static boolean isNull(TableInfo tableInfo) {
        if(Objects.isNull(tableInfo)) {
            return true;
        }

        if(StringUtils.isEmpty(tableInfo.getTableName())) {
            return true;
        }

        return false;
    }



    @Override
    public boolean equals(Object otherObject) {
        if(this == otherObject) {
            return true;
        }

        if(!(otherObject instanceof  TableInfo)) {
            return false;
        }

        TableInfo otherTableInfo = (TableInfo) otherObject;

        //数据库名是否一致
        boolean isOtherDatabaseNull = StringUtils.isBlank(otherTableInfo.getDatabaseName()) ||
                StringUtils.equals(otherTableInfo.getDatabaseName(), DEFAULT_DATABASE_NAME);

        boolean isDatabaseNull = StringUtils.isBlank(this.getDatabaseName()) ||
                StringUtils.equals(this.getDatabaseName(), DEFAULT_DATABASE_NAME);

        boolean isAllDatabaseNull = isOtherDatabaseNull && isDatabaseNull;

        boolean isAllDatabaseNotNull = StringUtils.isNotBlank(otherTableInfo.getDatabaseName())
                && !StringUtils.equals(otherTableInfo.getDatabaseName(), DEFAULT_DATABASE_NAME)
                && StringUtils.isNotBlank(this.getDatabaseName())
                && !StringUtils.equals(this.getDatabaseName(),DEFAULT_DATABASE_NAME);

        if(!(isAllDatabaseNull || isAllDatabaseNotNull)) {
            return false;
        }

        if(isAllDatabaseNotNull) {
            if(!StringUtils.equals(this.getDatabaseName(), otherTableInfo.getDatabaseName())) {
                return false;
            }
        }

        //表名是否一致
        boolean isAllTableNull = StringUtils.isBlank(otherTableInfo.getTableName())
                && StringUtils.isBlank(this.getTableName());

        boolean isAllTableNotNull = StringUtils.isNotBlank(otherTableInfo.getTableName())
                && StringUtils.isNotBlank(this.getTableName());

        if(!(isAllTableNull || isAllTableNotNull)) {
            return false;
        }

        if(isAllTableNotNull) {
            if(!StringUtils.equals(this.getTableName(), otherTableInfo.getTableName())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        //表名不可能为空


        if(StringUtils.isNotBlank(getDatabaseName()) && StringUtils.isNotBlank(getTableName())) {
            return Objects.hash(getDatabaseName(), getTableName());
        } else {
            return Objects.hash(getTableName());
        }
    }
}