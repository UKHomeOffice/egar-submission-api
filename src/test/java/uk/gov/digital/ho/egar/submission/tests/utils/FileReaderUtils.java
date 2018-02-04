package uk.gov.digital.ho.egar.submission.tests.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FileReaderUtils {

    public static String readFileAsString(final String filename) throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        return read(stream);
    }

    public static String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining(""));
        }
    }
}
