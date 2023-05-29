package com.commonlib.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Radithya Mirza Aribowo
 * @since 2022-04-19
 */
@Slf4j
@Service
public class SignatureGeneratorService {

    @Setter
    private Mac sha256HMAC;

    private static final String SECRET = "hello";

    public String createSignature(String param) throws NoSuchAlgorithmException, InvalidKeyException {
        sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256HMAC.init(secretKeySpec);
        return Hex.encodeHexString(sha256HMAC.doFinal(param.getBytes(StandardCharsets.UTF_8)));

    }

}
