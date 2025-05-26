package pl.agh.backend.heart_rate;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.backend.heart_rate.model.HeartRate;

public interface HeartRateRepository extends JpaRepository<HeartRate, Integer> {
}
