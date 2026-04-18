package com.pipeline.stages;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class IngestStage {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".mp4", ".mov", ".mkv", ".webm");
    private static final long MIN_SIZE_BYTES = 1024; 

    private final String inputFile;

    public IngestStage(String inputFile) {
        this.inputFile = inputFile;
    }

    public void run() throws Exception {
        System.out.println("\nINGEST STAGE(^◕.◕^)");

        checkFileExists();
        String checksum = computeChecksum();
        validateFormat(checksum);

        System.out.println(" Ingest complete — file is valid.\n");
    }

    private void checkFileExists() throws IOException {
        Path path = Path.of(inputFile);
        if (!Files.exists(path)) {
            throw new IOException("Input file not found: " + inputFile);
        }
        System.out.println("  [Integrity Check] File found: " + inputFile);
    }

    private String computeChecksum() throws IOException, NoSuchAlgorithmException {
        System.out.print("  [Integrity Check] Computing SHA-256 checksum... ");

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        StringBuilder hex = new StringBuilder();
        for (byte b : digest.digest()) {
            hex.append(String.format("%02x", b));
        }

        String checksum = hex.toString();
        System.out.println("  [Integrity Check] Checksum: " + checksum.substring(0, 16) + "...");
        return checksum;
    }

    private void validateFormat(String checksum) throws IOException {
        System.out.print("  [Format Validator] Checking file format... ");

        String ext = "";
        int dot = inputFile.lastIndexOf('.');
        if (dot >= 0) {
            ext = inputFile.substring(dot).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException(
                "Unsupported format: '" + ext + "'. Allowed: " + ALLOWED_EXTENSIONS
            );
        }

        long sizeBytes = Files.size(Path.of(inputFile));
        if (sizeBytes < MIN_SIZE_BYTES) {
            throw new IllegalArgumentException(
                "File too small (" + sizeBytes + " bytes). Possibly corrupt."
            );
        }

        double sizeMB = sizeBytes / (1024.0 * 1024.0);
        System.out.printf("  [Format Validator] Extension: %s | Size: %.2f MB%n", ext, sizeMB);
    }
}
