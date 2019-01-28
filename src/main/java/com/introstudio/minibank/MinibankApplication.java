package com.introstudio.minibank;

import com.introstudio.minibank.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class MinibankApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinibankApplication.class, args);
	}

}

