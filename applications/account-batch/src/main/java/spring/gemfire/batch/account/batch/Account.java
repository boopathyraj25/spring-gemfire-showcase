package spring.gemfire.batch.account.batch;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Account {
    private String id;

    private String name;
}
