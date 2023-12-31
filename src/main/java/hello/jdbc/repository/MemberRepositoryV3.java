package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * Transaction - Transaction Manager.
 * DataSourceUtils.getConnection()
 * DataSourceUtils.releaseConnection()
 */
@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    public Member findById(Connection connection, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
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
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
            //JdbcUtils.closeConnection(connection); 해당 커넥션을 닫아버리면 앞선 트랜잭션의 커넥션이 닫혀버린다. 따라서 트랜잭션 유지가 되지 않는다.
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

    public void update(Connection connection, String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);

            int resultSize = preparedStatement.executeUpdate(); // rows의 크기이다. 여기선 하나만 업데이트 하기 때문에 1이 리턴.

            log.info("result size is {}", resultSize);

        } catch (SQLException e) {
            log.error("DB Error");
            throw e;

        } finally {
            JdbcUtils.closeStatement(preparedStatement);
            //JdbcUtils.closeConnection(connection); 해당 커넥션을 닫아버리면 앞선 트랜잭션의 커넥션이 닫혀버린다. 따라서 트랜잭션 유지가 되지 않는다.
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

    private void close(Connection connection, Statement statement, ResultSet resultSet) {
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    private Connection getConnection() throws SQLException {
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        Connection connection = DataSourceUtils.getConnection(dataSource);

        log.info("Get Connection = {}, class = {}", connection, connection.getClass());

        return connection;
    }

}
