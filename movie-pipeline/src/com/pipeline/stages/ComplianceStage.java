package com.pipeline.stages;

import java.io.*;
import java.nio.file.*;

// Safety Scnner -> detecteaza continut care trebuie cenzurat in anumite contexte
// Regional Branding -> adauga logo-ul corect in funtie de tara

public class ComplianceStage {

    private final String outputDir;

    public ComplianceStage(String outputDir) {
        this.outputDir = outputDir;
    }

    public void run() throws Exception {
        System.out.println("\n--COMPLIANCE STAGE--");

        runSafetyScanner();
        applyRegionalBranding();

        System.out.println(" Compliance complete.\n");
    }

    private void runSafetyScanner() throws IOException, InterruptedException {
        System.out.println("  [Safety Scanner] Scanning content for regional restrictions... ");
        Thread.sleep(500);

        String[][] flaggedRegions = {
            { "00:12:34", "00:12:41", "DE", "violence" },
            { "00:45:10", "00:45:18", "AU", "language" },
            { "01:02:05", "01:02:09", "SG", "nudity"   },
        };

        System.out.println("  [Safety Scanner] Flagged segments:");
        for (String[] flag : flaggedRegions) {
            System.out.printf("    %s -> %s  [region: %s, reason: %s]%n",
                flag[0], flag[1], flag[2], flag[3]);
        }

        String metaDir = outputDir + "/metadata";
        Files.createDirectories(Path.of(metaDir));
        String reportPath = metaDir + "/compliance_report.json";

        try (FileWriter fw = new FileWriter(reportPath)) {
            fw.write("{\n");
            fw.write("  \"safety_scan\": {\n");
            fw.write("    \"status\": \"completed\",\n");
            fw.write("    \"flagged_segments\": [\n");
            for (int i = 0; i < flaggedRegions.length; i++) {
                String[] flag = flaggedRegions[i];
                fw.write("      { \"start\": \"" + flag[0] + "\", \"end\": \"" + flag[1] +
                         "\", \"region\": \"" + flag[2] + "\", \"reason\": \"" + flag[3] + "\" }");
                if (i < flaggedRegions.length - 1) fw.write(",");
                fw.write("\n");
            }
            fw.write("    ]\n");
            fw.write("  }\n");
            fw.write("}\n");
        }

        System.out.println("  [Safety Scanner] compliance_report.json saved -> " + reportPath);
    }

    private void applyRegionalBranding() throws InterruptedException {
        System.out.println("  [Regional Branding] Applying studio logos... ");
        Thread.sleep(400);

        String[][] brandingRules = {
            { "US", "Netflix Original" },
            { "RO", "Netflix Original" },
            { "DE", "Netflix Original (FSK 16)" },
            { "AU", "Netflix Original (MA15+)" },
        };

        System.out.println("  [Regional Branding] Branding applied:");
        for (String[] rule : brandingRules) {
            System.out.printf("    Region %-4s -> %s%n", rule[0], rule[1]);
        }
    }
}