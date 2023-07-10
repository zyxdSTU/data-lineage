package org.zjvis.dp.data.lineage.data;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouyu
 * @create 2023-07-10 15:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TableChangeInfo {

    private String tableChangeType;
    private String tableName;
    private String databaseName;

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }

        if (!(otherObject instanceof TableChangeInfo)) {
            return false;
        }

        TableChangeInfo otherTableChangeInfo = (TableChangeInfo) otherObject;

        if (!this.getTableChangeType().equals(otherTableChangeInfo.getTableChangeType())) {
            return false;
        }

        if (!this.getTableName().equals(otherTableChangeInfo.getTableName())) {
            return false;
        }

        if (!this.getDatabaseName().equals(otherTableChangeInfo.getDatabaseName())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTableChangeType(), getTableName(), getDatabaseName());
    }
}
