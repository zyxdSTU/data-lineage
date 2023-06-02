package org.zjvis.dp.data.lineage.data;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnDTO {
    private String columnName;
    private String columnType;
    private String columnComment;


    public static List<String> getColumnNameList(List<ColumnDTO> columnDTOList) {
        return columnDTOList.stream()
                .map(ColumnDTO::getColumnName)
                .collect(Collectors.toList());
    }
}
