
package org.delcom.ifs25048springbootcashflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DotEnvTest {

    @AfterEach
    void cleanup() {
        for (String k : new String[]{
                "DOTENV_A",
                "DOTENV_B",
                "DOTENV_C",
                "DOTENV_D",
                "DOTENV_E",
                "DOTENV_F"
        }) {
            System.clearProperty(k);
        }
    }

    @Test
    void loadsKeysCommentsAndQuotes(@TempDir Path dir) throws Exception {
        Path f = dir.resolve(".env");

        Files.writeString(f,
                "# komentar\n"
                + "\n"
                + "DOTENV_A=satu\n"
                + "DOTENV_B=\"dua spasi\"\n"
                + "DOTENV_C='tiga'\n"
                + "baris-tanpa-sama\n"
                + "=kosong\n"
                + "DOTENV_D=\"x\n"
                + "DOTENV_E=\n"
                + "DOTENV_F='x\n"
        );

        DotEnv.load(f);

        assertEquals("satu", System.getProperty("DOTENV_A"));
        assertEquals("dua spasi", System.getProperty("DOTENV_B"));
        assertEquals("tiga", System.getProperty("DOTENV_C"));
        assertEquals("\"x", System.getProperty("DOTENV_D"));
        assertEquals("", System.getProperty("DOTENV_E"));

        // Menguji tanda kutip satu yang tidak ditutup.
        assertEquals("'x", System.getProperty("DOTENV_F"));
    }

    @Test
    void doesNotOverrideExistingProperty(@TempDir Path dir)
            throws Exception {

        System.setProperty("DOTENV_A", "dari-sistem");

        Path f = dir.resolve(".env");
        Files.writeString(f, "DOTENV_A=dari-file\n");

        DotEnv.load(f);

        assertEquals(
                "dari-sistem",
                System.getProperty("DOTENV_A")
        );
    }

    @Test
    void missingFileIsIgnored(@TempDir Path dir) {
        DotEnv.load(dir.resolve("tidak-ada.env"));

        assertNull(System.getProperty("DOTENV_A"));
    }

    @Test
    void doesNotOverrideExistingEnvironmentVariable(@TempDir Path dir)
            throws Exception {

        String key = System.getenv()
                .keySet()
                .stream()
                .findFirst()
                .orElse(null);

        Assumptions.assumeTrue(
                key != null,
                "Tidak ada environment variable yang tersedia"
        );

        String previousProperty = System.getProperty(key);

        Path f = dir.resolve(".env");
        Files.writeString(f, key + "=nilai-dari-file\n");

        DotEnv.load(f);

        assertEquals(
                previousProperty,
                System.getProperty(key)
        );
    }

    @Test
    void wrapsInvalidUtf8AsIllegalStateException(@TempDir Path dir)
            throws Exception {

        Path f = dir.resolve(".env");

        Files.write(
                f,
                new byte[]{
                        (byte) 0xC3,
                        (byte) 0x28
                }
        );

        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> DotEnv.load(f)
        );

        assertEquals(
                "Gagal membaca " + f,
                error.getMessage()
        );

        assertInstanceOf(
                java.io.IOException.class,
                error.getCause()
        );
    }

    @Test
    void defaultLoadReadsDotEnvInWorkingDirectoryIfPresent() {
        DotEnv.load();
    }
}
