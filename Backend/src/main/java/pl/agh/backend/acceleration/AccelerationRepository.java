package pl.agh.backend.acceleration;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.backend.acceleration.model.Acceleration;

import java.util.List;

public interface AccelerationRepository extends JpaRepository<Acceleration, Integer> {
    List<Acceleration> findByTimestampBetween(int from, int to);
}
