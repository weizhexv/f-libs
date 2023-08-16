package org.opensearch.jdbc;

import org.opensearch.jdbc.config.ConnectionConfig;
import org.opensearch.jdbc.logging.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

public class DriverExt extends Driver {
    static String ENV;
    static {
        try {
            java.sql.DriverManager.deregisterDriver(new Driver());
            Enumeration<java.sql.Driver> driverEnumeration = java.sql.DriverManager.getDrivers();
            while (driverEnumeration.hasMoreElements()){
                java.sql.Driver driver =  driverEnumeration.nextElement();
                if(driver instanceof  Driver){
                    DriverManager.deregisterDriver(driver);
                }
            }
            java.sql.DriverManager.registerDriver(new DriverExt());
        } catch (SQLException e) {
            throw new RuntimeException("Can't register driver ext!");
        }
    }

    public static void setEnv(String value){
        DriverExt.ENV = value + ".";
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        ConnectionConfig connectionConfig = ConnectionConfig.builder().setUrl(url).setProperties(info).build();
        Logger log = initLog(connectionConfig);
        log.debug(() -> this.logMessage("connect (%s, %s)", new Object[]{url, info == null ? "null" : info.toString()}));
        log.debug(() -> this.logMessage("Opening connection using config: %s", new Object[]{connectionConfig}));
        return new ConnectionImplExt(connectionConfig, log);
    }
}
