package org.delcom.ifs25048springbootcashflow.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Hash dan verifikasi password memakai SHA-256 (hex lowercase). */
public final class PasswordHasher {

    private PasswordHasher() {
    }

    /** Menghasilkan hash SHA-256 dari password. */
    public static String hash(String raw) {
        return hash(raw, "SHA-256");
    }

    /** Package-private agar jalur exception dapat diuji. */
    static String hash(String raw, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return HexFormat.of().formatHex(
                    md.digest(raw.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 tidak tersedia", e);
        }
    }

    /** Memverifikasi password dengan hash yang tersimpan. */
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
