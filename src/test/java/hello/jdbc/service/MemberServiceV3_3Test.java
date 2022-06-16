package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 기본 동작 , 트랜잭션이 없어서 문제 발생
 */
@SpringBootTest
@Slf4j
class MemberServiceV3_3Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 memberRepository;
   @Autowired
    private MemberServiceV3_3 memberService;

   @TestConfiguration
   static class TestConfig{
       @Bean
       DataSource dataSource(){
           return new DriverManagerDataSource(URL,USERNAME,PASSWORD);
       }

       @Bean
       PlatformTransactionManager transactionManager(){
           return new DataSourceTransactionManager(dataSource());
       }

       @Bean
       MemberRepositoryV3 memberRepositoryV3(){
           return new MemberRepositoryV3(dataSource());
       }

       @Bean
       MemberServiceV3_3 memberServicev3_3(){
           return new MemberServiceV3_3(memberRepositoryV3());
       }

   }
/*
    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

        memberService = new MemberServiceV3_3( memberRepository);
    }
*/

    @Test
    void AopCheck(){
        log.info("member Service class={}",memberService.getClass());
        log.info("member repository class={}",memberRepository.getClass());
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member memberAFind = memberRepository.findById(memberA.getMemberId());
        Member memberBFind = memberRepository.findById(memberB.getMemberId());
        assertThat(memberAFind.getMoney()).isEqualTo(10000);
        assertThat(memberBFind.getMoney()).isEqualTo(10000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        //when

        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000)).isInstanceOf(IllegalStateException.class);
        //then
        Member memberAFind = memberRepository.findById(memberA.getMemberId());
        Member memberBFind = memberRepository.findById(memberB.getMemberId());
        assertThat(memberAFind.getMoney()).isEqualTo(10000);
        assertThat(memberBFind.getMoney()).isEqualTo(10000);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_EX);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_A);

    }
}
