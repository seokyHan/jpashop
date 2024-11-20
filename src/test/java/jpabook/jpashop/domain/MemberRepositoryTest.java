package jpabook.jpashop.domain;

import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;


    @DisplayName("회원 저장 테스트")
    @Test
    @Transactional
    void memberTest() {
        // given
        Member member = new Member();
        member.setName("memberA");

        // when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.findOne(savedId);

        //then
        assertThat(savedId).isEqualTo(findMember.getId());

    }


}