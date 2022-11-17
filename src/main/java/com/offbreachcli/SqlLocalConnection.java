package com.offbreachcli;


import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author rafae
 */
public class SqlLocalConnection {
    private JdbcTemplate connection;
    
    public SqlLocalConnection() {
        BasicDataSource dataSource = new BasicDataSource();
        
         dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
         dataSource.setUrl("jdbc:mysql://localhost:3306/Off_Breach");
         dataSource.setUsername("off_breach");
         dataSource.setPassword("#Gfgrupo3");

         connection = new JdbcTemplate(dataSource);
    }
    
    public JdbcTemplate getDataSource() {
        return connection;
    }
}
