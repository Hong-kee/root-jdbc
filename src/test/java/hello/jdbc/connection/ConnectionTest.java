package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
class ConnectionTest {

    @Test
    void driverManager() throws SQLException {
        // getConnection 시, URL, USERNAME, PASSWORD 등 설정 정보를 계속해서 넘겨야 한다. 매우 불편
        Connection connection1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection connection2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        log.info("Connection = {}, class = {}", connection1, connection1.getClass());
        log.info("Connection = {}, class = {}", connection2, connection2.getClass());
    }

    @Test
    void dataSourceManager() throws SQLException {
        // DriverManagerDataSource - 항상 새로운 커넥션을 획득
        // 이 객체가 만들어 지는 시점에 URL, USERNAME, PASSWORD 를 세팅해 놓으면 사용할 때 설정 정보를 계속해서 넘겨주지 않고 getConnection() 만 호출하면 됨.
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(driverManagerDataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection connection1 = dataSource.getConnection();
        Connection connection2 = dataSource.getConnection();

        log.info("Connection = {}, class = {}", connection1, connection1.getClass());
        log.info("Connection = {}, class = {}", connection2, connection2.getClass());
    }

}
