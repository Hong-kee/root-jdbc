package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection() {
        try {
            // Connection 은 interface 이고, getConnection 은 구현 하기 위한 객체이다.
            // 테스트를 확인해보면 class=class org.h2.jdbc.JdbcConnection 로 찍혀 있는데, 해당 external library 를 끌고 와서 구현함을 확인할 수 있다.
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("Get Connection={}, class={}",connection, connection.getClass());

            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
