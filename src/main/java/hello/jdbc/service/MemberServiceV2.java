package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction - Parameter 연동, 풀을 고려한 종료.
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;

    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            connection.setAutoCommit(false); // Transaction Start.

            // tx 시작 (비즈니스 로직 시작)
            businessLogic(connection, fromId, toId, money);

            connection.commit();

        } catch (Exception e) {
            // 실패시, 롤백
            connection.rollback();
            throw new IllegalStateException(e);

        } finally {
            release(connection);
        }
    }

    private void businessLogic(Connection connection, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(connection, fromId);
        Member toMember = memberRepository.findById(connection, toId);

        memberRepository.update(connection, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(connection, toId, toMember.getMoney() + money);
    }

    private static void release(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true); // 커넥션 풀 고려해서 오토커밋 상태로 세팅하고 돌려준다. (디폴트가 true 라서)
                connection.close();
            } catch (Exception e) {
                log.info("Error", e);
            }
        }
    }

    private static void validation(Member toMember) {
        if ("ex".equals(toMember.getMemberId())) {
            throw new IllegalStateException("이체 중 예외 발생.");
        }
    }

}
