package org.zjvis.dp.data.lineage.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class DBConfig {

    private String host;

    private Integer port;


    private String username;


    private String password;


    private String databaseName;

    private String oracleConnectionType;

}
