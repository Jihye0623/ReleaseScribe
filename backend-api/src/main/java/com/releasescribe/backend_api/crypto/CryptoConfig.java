package com.releasescribe.backend_api.crypto;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppCryptoProperties.class)
public class CryptoConfig {}
// @ConfigurationProperties 를 Spring에 등록
// 이를 통해 AppCryptoProperties가 Bean으로 만들어짐