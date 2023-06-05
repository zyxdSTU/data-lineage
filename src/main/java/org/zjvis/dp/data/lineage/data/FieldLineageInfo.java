package org.zjvis.dp.data.lineage.data;

import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author zhouyu
 * @create 2023-02-22 14:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldLineageInfo {
    private FieldInfo targetField;
    private List<FieldInfo> sourceFields;

    @Override
    public String toString() {
        if(CollectionUtils.isEmpty(sourceFields)) {
            return super.toString();
        }

        if(Objects.isNull(targetField)) {
            return super.toString();
        }

        StringBuilder result = new StringBuilder();

        String targetField = getTargetField().getReallyName();

        for(FieldInfo fieldInfo : sourceFields) {
            String sourceField = fieldInfo.getReallyName();
            result.append("T: ").append(targetField).append(" S: ").append(sourceField).append("\n");
        }

        return result.toString();
    }
}
