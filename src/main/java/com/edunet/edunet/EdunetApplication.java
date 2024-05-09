package com.edunet.edunet;

import com.edunet.edunet.security.RSAKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RSAKeyProperties.class)
@SpringBootApplication
public class EdunetApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdunetApplication.class, args);
	}

}
