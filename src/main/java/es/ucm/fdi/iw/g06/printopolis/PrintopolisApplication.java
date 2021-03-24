package es.ucm.fdi.iw.g06.printopolis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class PrintopolisApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrintopolisApplication.class, args);
	}

}
