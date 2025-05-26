package pl.agh.backend.acceleration;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.backend.acceleration.model.Acceleration;

public interface AccelerationRepository extends JpaRepository<Acceleration, Integer> {
}
