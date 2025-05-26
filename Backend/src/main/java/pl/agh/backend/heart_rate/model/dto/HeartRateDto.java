package pl.agh.backend.heart_rate.model.dto;

import lombok.Builder;
import lombok.Getter;
import pl.agh.backend.heart_rate.model.HeartRate;

@Getter
@Builder
public class HeartRateDto {
    private int id;
    private int timestamp;
    private double heartRateValue;

    public static HeartRateDto fromEntity(HeartRate heartRate) {
        return HeartRateDto.builder()
                .id(heartRate.getId())
                .timestamp(heartRate.getTimestamp())
                .heartRateValue(heartRate.getHeartRateValue())
                .build();
    }
}
