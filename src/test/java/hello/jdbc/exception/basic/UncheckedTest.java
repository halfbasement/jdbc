package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {


    @Test
    void unchecked_catch(){
        Service service = new Service();
        service.callCatch();
    }

    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    static class Service{
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리하면 된다
         */
        public void callCatch(){

            try {
                repository.call();
            } catch (MyUncheckedException e){
                log.info("예오ㅓㅣ처리");
            }
        }
        /**
         * throws안써도됨
         */
        public void callThrow(){
            repository.call();
        }
    }

    /**
     * throws 생략 가능
     */
    static class Repository{
        public void call(){
            throw new MyUncheckedException("ex");
        }
    }


}
