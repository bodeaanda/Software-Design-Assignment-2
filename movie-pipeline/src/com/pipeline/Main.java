package com.pipeline;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java Main <inputFile> <outputDir>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputDir = args[1];

        System.out.println("Starting pipeline with input: " + inputFile + " and output: " + outputDir);
        System.out.println("Initializing pipeline...");
        
        Orchestrator orchestrator = new Orchestrator(inputFile, outputDir);
        orchestrator.run();
    }
}
