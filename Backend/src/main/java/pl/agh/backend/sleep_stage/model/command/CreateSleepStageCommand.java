package pl.agh.backend.sleep_stage.model.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.agh.backend.sleep_stage.model.SleepStage;

@Data
public class CreateSleepStageCommand {

    @NotNull(message = "NULL_VALUE")
    private Integer timestamp;

    private String stage;

    public SleepStage toEntity() {
        return SleepStage.builder()
                .timestamp(timestamp)
                .stage(stage)
                .build();
    }
}
