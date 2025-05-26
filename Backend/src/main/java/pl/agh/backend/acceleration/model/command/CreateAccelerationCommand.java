package pl.agh.backend.acceleration.model.command;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.agh.backend.acceleration.model.Acceleration;

@Data
public class CreateAccelerationCommand {
    @NotNull(message = "NULL_VALUE")
    private Integer timestamp;

    @NotNull(message = "NULL_VALUE")
    private Double accelerationX;

    @NotNull(message = "NULL_VALUE")
    private Double accelerationY;

    @NotNull(message = "NULL_VALUE")
    private Double accelerationZ;

    public Acceleration toEntity() {
        return Acceleration.builder()
                .timestamp(timestamp)
                .accelerationX(accelerationX)
                .accelerationY(accelerationY)
                .accelerationZ(accelerationZ)
                .build();
    }
}
