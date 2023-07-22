package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction - Transaction Template.
 */
@Slf4j
public class MemberServiceV3_2 {

    private final TransactionTemplate transactionTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager platformTransactionManager, MemberRepositoryV3 memberRepository) {
        // TransactionTemplate 이라는 클래스는 인터페이스라 유연성이 떨어지기 때문에 PlatformTransactionManager 라는 인터페이스를 주입시켜 사용하면 유연성이 증가한다.
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException{
        // 1. executeWithoutResult 에서 Transaction 시작
        // 2. 비즈니스 로직 시작
        // 3. 성공 -> commit / 실패 -> rollback
        // 템플릿 - 콜백 패턴 학습 하기
        transactionTemplate.executeWithoutResult((status) -> {
            // 비즈니스 로직 시작
            try { // executeWithoutResult 메서드에서 SQLException 을 잡아주지 않기에 try-catch 로 잡아 주어야 한다.
                businessLogic(fromId, toId, money);

            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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
