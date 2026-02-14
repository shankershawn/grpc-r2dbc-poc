package com.shankarsan.grpc_r2dbc_poc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class GrpcR2dbcPocApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext);
	}

	@Test
	void protobufHttpMessageConverterBeanIsAvailable() {
		assertNotNull(applicationContext.getBean(ProtobufHttpMessageConverter.class));
	}

	@Test
	void mainStartsAndShutsDown() {
		String original = System.getProperty("app.exit-immediately");
		System.setProperty("app.exit-immediately", "true");
		try {
			GrpcR2dbcPocApplication.main(new String[0]);
		} finally {
			assertNotNull(applicationContext);
			if (original == null) {
				System.clearProperty("app.exit-immediately");
			} else {
				System.setProperty("app.exit-immediately", original);
			}
		}
	}

}
