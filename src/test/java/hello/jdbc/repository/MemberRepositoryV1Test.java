package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        // 기본 DriverManager - 항상 새로운 커넥션을 획득 (할 때 마다 TCP/IP로 커넥션을 맺기에 속도가 느리다.)
        //DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 커넥션 풀링.
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    @DisplayName("Member CRUD 테스트")
    void crud() throws SQLException, InterruptedException {
        // Save.
        Member member = new Member("member224", 10000);

        repository.save(member);

        // Read.
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        log.info("findMember = {}", member.equals(findMember));
        assertThat(findMember).isEqualTo(member);

        // Update.
        repository.update(member.getMemberId(), 20000);

        // Read for update.
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(member.getMoney()).isNotEqualTo(updatedMember.getMoney());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        // Delete.
        repository.delete(member.getMemberId());

        // Read for delete.
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

        Thread.sleep(1000);
    }

}