package spring.gemfire.batch.account.batch;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.rsocket.PrometheusRSocketClient;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.listener.annotation.AfterTask;
import org.springframework.cloud.task.repository.TaskExecution;
@SpringBootApplication
public class AccountBatchApp {

	@Autowired
	PrometheusMeterRegistry prometheusMeterRegistry;

	@Autowired
	PrometheusRSocketClient client;

	public static void main(String[] args) {
		SpringApplication.run(AccountBatchApp.class, args);
	}


	@AfterTask
	public void afterTask(TaskExecution taskExecution) {

		System.out.println(prometheusMeterRegistry.scrape());
		client.pushAndClose();

		client = PrometheusRSocketClient
				.build(prometheusMeterRegistry, TcpClientTransport.create("35.223.217.203", 7001))
				.connect();
		client.pushAndClose();
	}

}
