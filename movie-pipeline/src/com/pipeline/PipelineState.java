package com.pipeline;

public enum PipelineState {
    IDLE,
    INGESTING,
    ANALYZING,
    PROCESSING,
    COMPLIANCE,
    PACKAGING,
    DONE,
    FAILED
}
