package pl.agh.backend.heart_rate.model.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.agh.backend.heart_rate.model.HeartRate;

@Data
public class CreateHeartRateCommand {

    @NotNull(message = "NULL_VALUE")
    private Integer timestamp;

    @NotNull(message = "NULL_VALUE")
    private Double heartRateValue;

    public HeartRate toEntity() {
        return HeartRate.builder()
                .timestamp(timestamp)
                .heartRateValue(heartRateValue)
                .build();
    }
}
