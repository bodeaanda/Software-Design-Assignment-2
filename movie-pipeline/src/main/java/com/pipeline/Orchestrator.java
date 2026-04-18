package main.java.com.pipeline;

import java.nio.channels.Pipe;

import main.java.com.pipeline.stages.IngestStage;
import main.java.com.pipeline.stages.AnalysisStage;

public class Orchestrator {
    private final String inputFile;
    private final String outputDir;
    private PipelineState state;

    public Orchestrator(String inputFile, String outputDir) {
        this.inputFile = inputFile;
        this.outputDir = outputDir;
        this.state = PipelineState.IDLE;
    }

    private void transition(PipelineState newState) {
        System.out.println("Transitioning from " + state + " to " + newState);
        this.state = newState;
    }

    public void run() {
        try {
            //Ingest Stage
            transition(PipelineState.INGESTING);
            new IngestStage(inputFile).run();

            //Analysis Stage
            transition(PipelineState.ANALYZING);
            new AnalysisStage(inputFile, outputDir).run();

            transition(PipelineState.DONE);
            System.out.println("Pipeline completed successfully.");
        } catch (Exception e) {
            transition(PipelineState.FAILED);
            System.err.println("Pipeline failed: " + e.getMessage());
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
