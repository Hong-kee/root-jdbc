package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        connection = getConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());

            preparedStatement.executeUpdate();

            return member;
        } catch (SQLException e) {
            log.info("DB Error", e);
            throw e;
        } finally {
            // preparedStatement.close(); // If, 이 곳에서 Exception 이 터지면? connection.close(); 은 실행이 안 된다.
            // connection.close();
            close(connection, preparedStatement, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);

            resultSet = preparedStatement.executeQuery();// ResultSet을 반환 해준다.

            if (resultSet.next()) {
                Member member = new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setMoney(resultSet.getInt("money"));

                return member;
            }

            throw new NoSuchElementException("Member Not Found. MemberId = " + memberId);

        } catch (SQLException e) {
            log.error("DB Error");
            throw e;

        } finally {
            close(connection, preparedStatement, resultSet);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);

            int resultSize = preparedStatement.executeUpdate(); // rows의 크기이다. 여기선 하나만 업데이트 하기 때문에 1이 리턴.

            log.info("result size is {}", resultSize);

        } catch (SQLException e) {
            log.error("DB Error");
            throw e;

        } finally {
            close(connection, preparedStatement, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);

            int resultSize = preparedStatement.executeUpdate(); // rows의 크기이다. 여기선 하나만 업데이트 하기 때문에 1이 리턴.

            log.info("result size is {}", resultSize);

        } catch (SQLException e) {
            log.error("DB Error");
            throw e;

        } finally {
            close(connection, preparedStatement, null);
        }
    }

    private static void close(Connection connection, Statement statement, ResultSet resultSet) {
        // Statement 는 쿼리를 스태틱하게 박아서 사용, PreparedStatement 는 쿼리를 바인딩(동적)해서 사용 (기능이 더 많음) (PreparedStatement 는 Statement 를 상속 받음)
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.info("Error", e);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.info("Error", e);
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.info("Error", e);
            }
        }

    }

    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

}
