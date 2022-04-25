package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection(){
        try {
            //구현체는 h2 드라이버 ( jdbc를 구현하고 있음 )
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={} , class={}", connection,connection.getClass());
            return connection;
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
