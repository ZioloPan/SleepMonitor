package pl.agh.backend.acceleration.model.dto;

import lombok.Builder;
import lombok.Getter;
import pl.agh.backend.acceleration.model.Acceleration;

@Getter
@Builder
public class AccelerationDto {
    private int id;
    private int timestamp;
    private double accelerationX;
    private double accelerationY;
    private double accelerationZ;

    public static AccelerationDto fromEntity(Acceleration acceleration) {
        return AccelerationDto.builder()
                .id(acceleration.getId())
                .timestamp(acceleration.getTimestamp())
                .accelerationX(acceleration.getAccelerationX())
                .accelerationY(acceleration.getAccelerationY())
                .accelerationZ(acceleration.getAccelerationZ())
                .build();
    }
}
