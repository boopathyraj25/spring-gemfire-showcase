package spring.gemfire.showcase.account.sink.functions;

import com.rabbitmq.stream.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import spring.gemfire.showcase.account.domain.account.Account;
import spring.gemfire.showcase.account.sink.repostories.AccountRepository;

import java.util.StringTokenizer;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Component
@Slf4j
public class AccountConsumer implements Consumer<String> {
    private final AccountRepository repository;

    public void accept(String rabbitMessage) {
        log.info("The queue message is " + rabbitMessage);
        StringTokenizer st = new StringTokenizer(rabbitMessage,",");
        Account account = new Account();
        if (st.hasMoreTokens()) {
            account.setId(st.nextToken());
        }
        if (st.hasMoreTokens()){
            account.setName(st.nextToken());
        }
        repository.save(account);
    }
}
