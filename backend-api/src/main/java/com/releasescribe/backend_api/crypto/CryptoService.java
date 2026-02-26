package com.releasescribe.backend_api.crypto;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";

    // 권장값들
    private static final int KEY_LEN_BYTES = 32;      // 256-bit
    private static final int IV_LEN_BYTES  = 12;      // GCM 권장 96-bit
    private static final int TAG_LEN_BITS  = 128;     // 16 bytes

    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();

    public CryptoService(AppCryptoProperties props) {
        if (props == null || props.key() == null || props.key().isBlank()) {
            throw new IllegalStateException("app.crypto.key is missing (APP_CRYPTO_KEY env var)");
        }

        // Base64 디코딩을 통해 실제 bytes로 만듦
        byte[] raw = Base64.getDecoder().decode(props.key().trim());
        // 정확히 32 바이트 키가 맞는지 길이 체크
        if (raw.length != KEY_LEN_BYTES) {
            throw new IllegalStateException("APP_CRYPTO_KEY must be Base64 of 32 bytes, but was " + raw.length + " bytes");
        }

        this.key = new SecretKeySpec(raw, AES);
    }

    /**
     * 평문 -> Base64(iv + ciphertext)
     */
    public String encryptToBase64(String plaintext) {
        if (plaintext == null) throw new IllegalArgumentException("plaintext is null");

        // 랜덤 값 생성
        byte[] iv = new byte[IV_LEN_BYTES];
        random.nextBytes(iv);

        try {
            // AES-GCM으로 암호화
            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BITS, iv));

            byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // iv + ciphertext 를 붙여서 Base64로 인코딩해 DB에 저장
            ByteBuffer buf = ByteBuffer.allocate(iv.length + ct.length);
            buf.put(iv);
            buf.put(ct);

            return Base64.getEncoder().encodeToString(buf.array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    /**
     * Base64(iv + ciphertext) -> 평문
     * (GET /projects에서는 절대 토큰 복호화해서 내려주지 말고, 내부에서만 필요할 때 사용)
     */
    public String decryptFromBase64(String encBase64) {
        if (encBase64 == null || encBase64.isBlank()) throw new IllegalArgumentException("encBase64 is blank");

        byte[] all = Base64.getDecoder().decode(encBase64.trim());
        if (all.length <= IV_LEN_BYTES) {
            throw new IllegalArgumentException("Invalid ciphertext");
        }

        byte[] iv = new byte[IV_LEN_BYTES];
        byte[] ct = new byte[all.length - IV_LEN_BYTES];

        System.arraycopy(all, 0, iv, 0, IV_LEN_BYTES);
        System.arraycopy(all, IV_LEN_BYTES, ct, 0, ct.length);

        try {
            // AES-GCM으로 복호화
            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BITS, iv));
            byte[] pt = cipher.doFinal(ct);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            // 키가 바뀌었거나, DB에서 암호문이 변조됐거나, 형식이 깨졌을 때
            throw new IllegalArgumentException("Decryption failed (bad key or tampered data)", e);
        }
    }

    /**
     * 응답용 토큰 힌트 (예: ghp_****abcd)
     */
    public static String tokenHint(String token) {
        if (token == null || token.isBlank()) return "";
        String t = token.trim();
        int keep = Math.min(4, t.length());
        return t.substring(0, Math.min(4, t.length())) + "****" + t.substring(t.length() - keep);
    }
}