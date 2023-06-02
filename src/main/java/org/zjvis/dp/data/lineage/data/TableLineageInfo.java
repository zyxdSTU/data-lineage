package org.zjvis.dp.data.lineage.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouyu
 * @create 2023-03-27 15:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableLineageInfo {
    private TableInfo targetTable;
    private List<TableInfo> sourceTables;
}
