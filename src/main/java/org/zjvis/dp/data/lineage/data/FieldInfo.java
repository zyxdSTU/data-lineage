package org.zjvis.dp.data.lineage.data;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouyu
 * @create 2023-02-21 11:01
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldInfo {

    /**
     * 与其关联的字段名
     */
    List<FieldInfo> relatedFieldInfoList;

    /**
     * 字段名称
     */
    String fieldName;


    /**
     * 表信息
     */
    TableInfo tableInfo;

    /**
     * 函数
     */
    String process;


    /**
     * 位于语句中的顺序
     */
    Integer sequenceNumber;

    /**
     * 是不是星号列
     */
    boolean isAsteriskColumn;

    public String getReallyNameWithNumber() {
        String result = "";
        if(Objects.nonNull(sequenceNumber)) {
            result += sequenceNumber;
        }
        if(!TableInfo.isNull(tableInfo)) {
            if(StringUtils.isNotEmpty(tableInfo.getDatabaseName())) {
                result += tableInfo.getDatabaseName() + ".";
            }
            result += tableInfo.getTableName() + ".";
        }
        result += fieldName;


        return result;
    }

    public String getReallyName() {
        String result = "";
        if(!TableInfo.isNull(tableInfo)) {
            if(StringUtils.isNotEmpty(tableInfo.getDatabaseName())) {
                result += tableInfo.getDatabaseName() + ".";
            }
            result += tableInfo.getTableName() + ".";
        }
        result += fieldName;
        return result;
    }


    public static FieldInfo copy(FieldInfo fieldInfo) {
        TableInfo tableInfo = null;
        if(Objects.nonNull(fieldInfo.getTableInfo())) {
            tableInfo = TableInfo.builder()
                    .tableName(fieldInfo.getTableInfo().getTableName())
                    .databaseName(fieldInfo.getTableInfo().getDatabaseName())
                    .build();
        }
        return FieldInfo.builder()
                .tableInfo(tableInfo)
                .fieldName(fieldInfo.getFieldName())
                .isAsteriskColumn(fieldInfo.isAsteriskColumn())
                .sequenceNumber(fieldInfo.getSequenceNumber())
                .build();
    }

    public static boolean isContainAsterisk(List<FieldInfo> input) {
        List<FieldInfo> asteriskColumnList = input.stream()
                .filter(FieldInfo::isAsteriskColumn)
                .collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(asteriskColumnList);
    }
}
