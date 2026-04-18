package main.java.com.pipeline;

import com.pipeline.stages.IngestStage;
import com.pipeline.stages.AnalysisStage;
import com.pipeline.stages.VisualsStage;

/**
 * Orchestrator manages the pipeline state machine.
 * It transitions between states and calls each stage in order.
 *
 * Phase 3 (Visuals) and Phase 4 (Audio/Text) run in PARALLEL.
 */
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

            // ── Phase 1: Ingest ──────────────────────────────
            transition(PipelineState.INGESTING);
            new IngestStage(inputFile).run();

            // ── Phase 2: Analysis ────────────────────────────
            transition(PipelineState.ANALYZING);
            new AnalysisStage(inputFile, outputDir).run();

            // ── Phase 3+4: Visuals + Audio/Text in parallel ──
            transition(PipelineState.PROCESSING);

            // Collect errors from threads
            String[] errors = new String[2];

            Thread visualsThread = new Thread(() -> {
                try {
                    new VisualsStage(inputFile, outputDir).run();
                } catch (Exception e) {
                    errors[0] = "Visuals error: " + e.getMessage();
                }
            });

            // Audio/Text thread placeholder — will be added in next commit
            Thread audioThread = new Thread(() -> {
                System.out.println("\n── Phase 4: AUDIO/TEXT (coming in next commit) ──");
            });

            System.out.println("\n  [Orchestrator] Starting Phase 3 + Phase 4 in parallel...");
            visualsThread.start();
            audioThread.start();
            visualsThread.join();
            audioThread.join();

            // Check for errors from threads
            for (String error : errors) {
                if (error != null) throw new RuntimeException(error);
            }

            // More stages coming in future commits...

            transition(PipelineState.DONE);
            System.out.println("\n✅  Pipeline completed successfully!");

        } catch (Exception e) {
            transition(PipelineState.FAILED);
            System.err.println("\n❌  Pipeline failed: " + e.getMessage());
        }
    }

    public String getInputFile()    { return inputFile; }
    public String getOutputDir()    { return outputDir; }
    public PipelineState getState() { return state; }
}