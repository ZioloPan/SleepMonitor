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
    private int nightId;

    public static HeartRateDto fromEntity(HeartRate heartRate) {
        return HeartRateDto.builder()
                .id(heartRate.getId())
                .nightId(heartRate.getNightId())
                .timestamp(heartRate.getTimestamp())
                .heartRateValue(heartRate.getHeartRateValue())
                .build();
    }
}
