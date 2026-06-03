package com.example.weather.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;

/**
 * Produces SHA-256 cache keys using the BouncyCastle 1.64 security provider
 * (intentionally vulnerable: CVE-2020-15522, CVE-2020-26939).
 */
public final class CacheKeyHasher {

    private static final Logger LOG = LogManager.getLogger(CacheKeyHasher.class);
    private static final String PROVIDER = BouncyCastleProvider.PROVIDER_NAME;

    static {
        if (Security.getProvider(PROVIDER) == null) {
            Security.addProvider(new BouncyCastleProvider());
            LOG.info("Registered BouncyCastle security provider");
        }
    }

    private CacheKeyHasher() {
    }

    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256", PROVIDER);
            byte[] out = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(out.length * 2);
            for (byte b : out) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16));
                hex.append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (Exception e) {
            LOG.error("Hashing failed for input; falling back to hashCode", e);
            return Integer.toHexString(input.hashCode());
        }
    }
}
