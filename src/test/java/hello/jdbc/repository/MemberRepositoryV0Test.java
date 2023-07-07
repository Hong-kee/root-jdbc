package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 memberRepositoryV0 = new MemberRepositoryV0();

    @Test
    @DisplayName("Member CRUD 테스트")
    void crud() throws SQLException {
        // Save.
        Member member = new Member("member224", 10000);

        memberRepositoryV0.save(member);

        // Read.
        Member findMember = memberRepositoryV0.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        log.info("findMember = {}", member.equals(findMember));
        assertThat(findMember).isEqualTo(member);

        // Update.
        memberRepositoryV0.update(member.getMemberId(), 20000);

        // Read for update.
        Member updatedMember = memberRepositoryV0.findById(member.getMemberId());
        assertThat(member.getMoney()).isNotEqualTo(updatedMember.getMoney());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        // Delete.
        memberRepositoryV0.delete(member.getMemberId());

        // Read for delete.
        assertThatThrownBy(() -> memberRepositoryV0.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }

}