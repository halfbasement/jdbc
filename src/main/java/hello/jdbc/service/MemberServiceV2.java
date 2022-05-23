package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * 트랜잭션 - 파라미터 연동 , 풀을 고려한 종료
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId , int money) throws SQLException {

        Connection con = dataSource.getConnection();

        try{
            con.setAutoCommit(false); //트랜잭션 시작
            //시작
            Member fromMember = memberRepository.findById(con,fromId);
            Member toMember = memberRepository.findById(con,toId);

            //출
            memberRepository.update(con,fromId, fromMember.getMoney()-money);
            validation(toMember);
            //입
            memberRepository.update(con,toId,toMember.getMoney() + money);
            con.commit(); //성공시 커밋
       }catch (Exception e){
        con.rollback();
        throw new IllegalStateException(e);
        }finally {
            if(con != null){
                try{
                    con.setAutoCommit(true);
                    con.close();
                }catch (Exception e){
                    log.info("ERROR ",e);
                }
            }
        }




    }

    private void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }


}
