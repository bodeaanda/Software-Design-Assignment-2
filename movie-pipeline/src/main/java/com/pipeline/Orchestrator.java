package main.java.com.pipeline;

import java.nio.channels.Pipe;

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
            transition(PipelineState.INGESTING);
            new IngestStage(inputFile).run();

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
