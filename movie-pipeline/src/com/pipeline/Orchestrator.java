package com.pipeline;

import com.pipeline.stages.IngestStage;
import com.pipeline.stages.AnalysisStage;
import com.pipeline.stages.VisualsStage;
import com.pipeline.stages.AudioTextStage;
import com.pipeline.stages.ComplianceStage;
import com.pipeline.stages.PackagingStage;

public class Orchestrator {

    private final String inputFile;
    private final String outputDir;
    private PipelineState state;

    public Orchestrator(String inputFile, String outputDir) {
        this.inputFile = inputFile;
        this.outputDir = outputDir;
        this.state     = PipelineState.IDLE;
    }

    private void transition(PipelineState newState) {
        System.out.println();
        System.out.println("==================================================");
        System.out.printf("  STATE: %-12s ->  %s%n", state, newState);
        System.out.println("==================================================");
        this.state = newState;
    } 

    public void run() {
        try {

            // Ingest Stage
            transition(PipelineState.INGESTING);
            new IngestStage(inputFile).run();

            // Analysis Stage
            transition(PipelineState.ANALYZING);
            new AnalysisStage(inputFile, outputDir).run();

            // Visuals + Audio/Text Stages (parallel)
            transition(PipelineState.PROCESSING);

            String[] errors = new String[2];

            Thread visualsThread = new Thread(() -> {
                try {
                    new VisualsStage(inputFile, outputDir).run();
                } catch (Exception e) {
                    errors[0] = "Visuals error: " + e.getMessage();
                }
            });

            Thread audioThread = new Thread(() -> {
                try {
                    new AudioTextStage(inputFile, outputDir).run();
                } catch (Exception e) {
                    errors[1] = "Audio/Text error: " + e.getMessage();
                }
            });

            System.out.println("\n  [Orchestrator] Starting Phase 3 + Phase 4 in parallel...");
            visualsThread.start();
            audioThread.start();
            visualsThread.join();
            audioThread.join();

            for (String error : errors) {
                if (error != null) throw new RuntimeException(error);
            }

            // Compliance Stage
            transition(PipelineState.COMPLIANCE);
            new ComplianceStage(outputDir).run();

            // Packaging Stage
            transition(PipelineState.PACKAGING);
            new PackagingStage(outputDir).run();

            transition(PipelineState.DONE);
            System.out.println("\n Pipeline completed successfully!");

        } catch (Exception e) {
            transition(PipelineState.FAILED);
            System.err.println("\n Pipeline failed: " + e.getMessage());
        }
    }

    public String getInputFile() { 
        return inputFile; 
    }
    public String getOutputDir() { 
        return outputDir;
    }
    public PipelineState getState() { 
        return state;
    }
}
