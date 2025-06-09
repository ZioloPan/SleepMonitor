package pl.agh.backend.sleep_stage.model.dto;

import java.time.LocalDateTime;

public record NightSummaryDto(int nightId, LocalDateTime date) {}
