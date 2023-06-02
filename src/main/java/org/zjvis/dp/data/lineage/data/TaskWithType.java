package org.zjvis.dp.data.lineage.data;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
 * @author zhouyu
 * @create 2023-05-28 13:43
 */
@Data
@AllArgsConstructor
@Builder
public class TaskWithType{
    private long taskId;
    private int taskType;

    @Override
    public boolean equals(Object otherObject) {
        if(this == otherObject) {
            return true;
        }

        if(!(otherObject instanceof TaskWithType)) {
            return false;
        }

        TaskWithType otherTaskWithType = (TaskWithType) otherObject;

        if(otherTaskWithType.getTaskId() == taskId && otherTaskWithType.getTaskType() == taskType) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTaskId(), getTaskType());
    }
}

