package com.releasescribe.backend_api.crypto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.crypto")
public record AppCryptoProperties(String key) {}
// Spring이 app.crypto.key 값을 읽어서 이 객체에 자동으로 담아줌