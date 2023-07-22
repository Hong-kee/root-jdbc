package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction - Transaction Manager.
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

    //    private final DataSource dataSource;
    // OCP를 지키기 위해 외부에서 주입 받을 것임.
    private final PlatformTransactionManager platformTransactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) {
        // 트랜잭션 시작.
        TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            // 비즈니스 로직 시작
            businessLogic(fromId, toId, money);

            // 성공 시, 커밋
            platformTransactionManager.commit(status);

        } catch (Exception e) {
            // 실패 시, 롤백
            platformTransactionManager.rollback(status);
            throw new IllegalStateException(e);

        } finally {
            // TransactionManager가 release를 알아서 해준다. 커밋 or 롤백이 되면 알아서 정리해준다!
//            release(connection);
        }
    }

    private void businessLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
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
