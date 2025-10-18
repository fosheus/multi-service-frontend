package fr.albanj.multifront;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import fr.albanj.corelib.core.CorelibConfiguration;
import fr.albanj.corelib.servicecore.ServiceCoreConfiguration;

@SpringBootApplication
@Import({ CorelibConfiguration.class, ServiceCoreConfiguration.class })
public class MultifrontApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultifrontApplication.class, args);
	}

}
