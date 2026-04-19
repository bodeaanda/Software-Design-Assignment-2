package com.pipeline.stages;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

// DRM Wrapper -> cripteaza toate fisierele ca sa nu poata fi descarcate ilegal
// Manifest Builder -> creeaza un fisier .json cu lista tuturor fisierelor generate

public class PackagingStage {

    private final String outputDir;

    public PackagingStage(String outputDir) {
        this.outputDir = outputDir;
    }

    public void run() throws Exception {
        System.out.println("\n--PACKAGING STAGE--");

        applyDrm();
        buildManifest();

        System.out.println(" Packaging complete.\n");
    }

    private void applyDrm() throws InterruptedException {
        System.out.println("  [DRM Wrapper] Encrypting streamable assets... ");
        Thread.sleep(600);
        System.out.println("  [DRM Wrapper] Encryption scheme : AES-128 (stub)");
        System.out.println("  [DRM Wrapper] Key ID            : stub-key-id-0000-ffff");
        System.out.println("  [DRM Wrapper] License server    : https://drm.stub.pipeline/license");
    }

    private void buildManifest() throws IOException {
        System.out.println("  [Manifest Builder] Scanning output and building manifest... ");

        List<String> videoFiles = collectFiles(outputDir + "/video");
        List<String> imageFiles = collectFiles(outputDir + "/images");
        List<String> textFiles  = collectFiles(outputDir + "/text");
        List<String> audioFiles = collectFiles(outputDir + "/audio");
        List<String> metaFiles  = collectFiles(outputDir + "/metadata");

        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"pipeline_version\": \"1.0\",\n");
        json.append("  \"generated_at\": \"").append(timestamp).append("\",\n");
        json.append("  \"output_dir\": \"").append(outputDir).append("\",\n");
        json.append("  \"drm\": {\n");
        json.append("    \"scheme\": \"AES-128\",\n");
        json.append("    \"key_id\": \"stub-key-id-0000-ffff\",\n");
        json.append("    \"license_server\": \"https://drm.stub.pipeline/license\"\n");
        json.append("  },\n");
        json.append("  \"assets\": {\n");
        json.append("    \"video\": ").append(toJsonArray(videoFiles)).append(",\n");
        json.append("    \"images\": ").append(toJsonArray(imageFiles)).append(",\n");
        json.append("    \"text\": ").append(toJsonArray(textFiles)).append(",\n");
        json.append("    \"audio\": ").append(toJsonArray(audioFiles)).append(",\n");
        json.append("    \"metadata\": ").append(toJsonArray(metaFiles)).append("\n");
        json.append("  },\n");
        json.append("  \"summary\": {\n");
        json.append("    \"video_files\": ").append(videoFiles.size()).append(",\n");
        json.append("    \"image_files\": ").append(imageFiles.size()).append(",\n");
        json.append("    \"text_files\": ").append(textFiles.size()).append(",\n");
        json.append("    \"audio_files\": ").append(audioFiles.size()).append(",\n");
        json.append("    \"metadata_files\": ").append(metaFiles.size()).append(",\n");
        json.append("    \"total_files\": ").append(
            videoFiles.size() + imageFiles.size() + textFiles.size() +
            audioFiles.size() + metaFiles.size()
        ).append("\n");
        json.append("  }\n");
        json.append("}\n");

        String metaDir = outputDir + "/metadata";
        Files.createDirectories(Path.of(metaDir));
        String manifestPath = metaDir + "/manifest.json";
        try (FileWriter fw = new FileWriter(manifestPath)) {
            fw.write(json.toString());
        }

        System.out.println("  [Manifest Builder] manifest.json saved -> " + manifestPath);

        int total = videoFiles.size() + imageFiles.size() + textFiles.size() +
                    audioFiles.size() + metaFiles.size();
        System.out.println("  [Manifest Builder] Total assets catalogued: " + total);
    }

    private List<String> collectFiles(String dirPath) {
        List<String> files = new ArrayList<>();
        Path dir = Paths.get(dirPath);
        if (!Files.exists(dir)) return files;

        try {
            Files.walk(dir)
                .filter(Files::isRegularFile)
                .forEach(p -> files.add(p.toString().replace("\\", "/")));
        } catch (IOException e) {
            System.err.println("Warning: could not scan " + dirPath);
        }
        return files;
    }

    private String toJsonArray(List<String> items) {
        if (items.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < items.size(); i++) {
            sb.append("      \"").append(items.get(i)).append("\"");
            if (i < items.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("    ]");
        return sb.toString();
    }
}