package com.offbreachcli;


import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlServerConnection {

    private JdbcTemplate connection;

    public SqlServerConnection() {
        BasicDataSource dataSource = new BasicDataSource();
        
         dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
         dataSource.setUrl("jdbc:sqlserver://servidor-off-breach.database.windows.net;database=off_breach;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net");
         dataSource.setUsername("off_breach");
         dataSource.setPassword("#Gfgrupo3");

         connection = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getDataSource() {
        return connection;
    }
}
