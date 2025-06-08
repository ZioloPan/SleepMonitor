package pl.agh.backend.sleep_stage.model.dto;

import lombok.Builder;
import lombok.Getter;
import pl.agh.backend.sleep_stage.model.SleepStage;

@Getter
@Builder
public class SleepStageDto {
    private int id;
    private int timestamp;
    private String stage;

    public static SleepStageDto fromEntity(SleepStage sleepStage) {
        return SleepStageDto.builder()
                .id(sleepStage.getId())
                .timestamp(sleepStage.getTimestamp())
                .stage(sleepStage.getStage())
                .build();
    }
}
