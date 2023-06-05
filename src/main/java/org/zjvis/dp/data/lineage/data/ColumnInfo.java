package org.zjvis.dp.data.lineage.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnInfo {
    private String columnName;
    private String columnType;
    private String columnComment;
}
