package com.pipeline.stages;

import java.io.*;
import java.nio.file.*;

public class AudioTextStage {

    private final String inputFile;
    private final String outputDir;

    public AudioTextStage(String inputFile, String outputDir) {
        this.inputFile = inputFile;
        this.outputDir = outputDir;
    }

    public void run() throws Exception {
        System.out.println("\nAUDIO / TEXT STAGE(^◕.◕^)─────────────────────────────");

        String transcript   = speechToText();
        String translation  = translate(transcript);
        generateDub(translation);

        System.out.println(" Audio/Text complete.\n");
    }

    private String speechToText() throws IOException, InterruptedException {
        System.out.print("  [Speech-to-Text] Transcribing audio... ");
        Thread.sleep(500);

        String transcript =
            "00:00:00 -> 00:00:02\n" +
            "My responsibilities...\n\n" +
            "00:00:03 -> 00:00:04\n" +
            "What are you talking about?\n\n" +
            "00:00:05 -> 00:00:08\n" +
            "Have you ever had a single moment of thought about my responsibilities?\n\n" +
            "00:00:09 -> 00:00:11\n" +
            "Have you ever thought for a single solitary moment?\n";

        String textDir = outputDir + "/text";
        Files.createDirectories(Path.of(textDir));
        String outPath = textDir + "/source_transcript.txt";
        try (FileWriter fw = new FileWriter(outPath)) {
            fw.write(transcript);
        }

        System.out.println("  [Speech-to-Text] source_transcript.txt saved → " + outPath);
        return transcript;
    }

    private String translate(String transcript) throws IOException, InterruptedException {
        System.out.print("  [Translator] Translating to Romanian (ro)... ");
        Thread.sleep(400);

        String translation =
            "00:00:00 -> 00:00:02\n" +
            "Responabilitățile mele...\n\n" +
            "00:00:03 -> 00:00:04\n" +
            "Despre ce vorbești?\n\n" +
            "00:00:05 -> 00:00:08\n" +
            "Ai avut vreodată un singur moment să te gândești la responsabilitățile mele?\n\n" +
            "00:00:09 -> 00:00:11\n" +
            "Te-ai gândit vreodată la responsabilitățile mele?\n";

        String outPath = outputDir + "/text/ro_translation.txt";
        try (FileWriter fw = new FileWriter(outPath)) {
            fw.write(translation);
        }

        System.out.println("  [Translator] ro_translation.txt saved → " + outPath);
        return translation;
    }

    private void generateDub(String translation) throws IOException, InterruptedException {
        System.out.print("  [AI Dubber] Generating synthetic Romanian dub... ");
        Thread.sleep(600);

        String audioDir = outputDir + "/audio";
        Files.createDirectories(Path.of(audioDir));
        String outPath = audioDir + "/ro_dub_synthetic.aac";
        try (FileWriter fw = new FileWriter(outPath)) {
            fw.write("STUB_AAC_DUB — ro\n");
        }

        System.out.println("  [AI Dubber] ro_dub_synthetic.aac saved → " + outPath);
    }
}
