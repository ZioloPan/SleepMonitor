package pl.agh.backend.heart_rate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class HeartRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int timestamp;
    private double heartRateValue;
    private int nightId;
}
