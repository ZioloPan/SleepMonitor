package pl.agh.backend.acceleration.model;

import jakarta.persistence.Column;
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
public class Acceleration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int timestamp;

    @Column(name = "acceleration_x")
    private double accelerationX;

    @Column(name = "acceleration_y")
    private double accelerationY;

    @Column(name = "acceleration_z")
    private double accelerationZ;
}
