package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource(String driver, String url, String password, String name) {
        this.driver = driver;
        this.url = url;
        this.password = password;
        this.name = name;
        try {
            Class.forName(driver) ;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static CustomDataSource getInstance() {

        if (instance == null) {
            synchronized (CustomDataSource.class) {
                if (instance == null) {

                    try {
                        Properties props = new Properties();
                        props.load(CustomDataSource.class.getClassLoader().getResourceAsStream("app.properties"));

                        String driver   = props.getProperty("postgres.driver");
                        String name     = props.getProperty("postgres.name");
                        String password = props.getProperty("postgres.password");
                        String url      = props.getProperty("postgres.url");

                        instance = new CustomDataSource(driver,url,password,name);

                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new CustomConnector().getConnection(url, name, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new CustomConnector().getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
       return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("'getParentLogger' is not implemented");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("'unwrap' is not implemented");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("'isWrapperFor' is not implemented");
    }
}
