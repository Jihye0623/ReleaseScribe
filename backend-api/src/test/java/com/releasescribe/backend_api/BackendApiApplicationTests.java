package com.releasescribe.backend_api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("CI 환경에서 DB 미구성으로 contextLoads 비활성화 (추후 Testcontainers 도입 시 활성화)")
@SpringBootTest
class BackendApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
