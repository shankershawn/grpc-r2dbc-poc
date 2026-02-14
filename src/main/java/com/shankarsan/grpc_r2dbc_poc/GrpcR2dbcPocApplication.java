package com.shankarsan.grpc_r2dbc_poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@SpringBootApplication
public class GrpcR2dbcPocApplication {

	@Bean
	public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
		return new ProtobufHttpMessageConverter();
	}

	static ConfigurableApplicationContext run(String... args) {
		return SpringApplication.run(GrpcR2dbcPocApplication.class, args);
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = run(args);
		if (Boolean.getBoolean("app.exit-immediately")) {
			context.close();
		}
	}

}
