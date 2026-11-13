package org.delcom.ifs25048springbootcashflow.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Hash dan verifikasi password menggunakan SHA-256.
 */
public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            return HexFormat.of().formatHex(
                    md.digest(raw.getBytes(StandardCharsets.UTF_8))
            );

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 tidak tersedia", e
            );
        }
    }

    public static boolean verify(String raw, String hash) {
        if (raw == null || hash == null) {
            return false;
        }

        return MessageDigest.isEqual(
                hash(raw).getBytes(StandardCharsets.UTF_8),
                hash.getBytes(StandardCharsets.UTF_8)
        );
    }
}
