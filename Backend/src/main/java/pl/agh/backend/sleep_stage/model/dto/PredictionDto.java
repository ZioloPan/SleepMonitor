package pl.agh.backend.sleep_stage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PredictionDto {
    private int night_id;
    private int second_of_sleep;
    private String stage;
}
