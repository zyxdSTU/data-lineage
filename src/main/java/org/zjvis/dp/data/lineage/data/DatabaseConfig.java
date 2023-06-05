package org.zjvis.dp.data.lineage.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConfig {

    private String host;

    private Integer port;

    private String username;

    private String password;

    private String databaseName;

    private String schemaName;

    private Integer loginTimeout;
}
