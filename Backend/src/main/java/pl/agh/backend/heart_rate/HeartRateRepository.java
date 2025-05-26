package pl.agh.backend.heart_rate;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.backend.heart_rate.model.HeartRate;

import java.util.List;

public interface HeartRateRepository extends JpaRepository<HeartRate, Integer> {
    List<HeartRate> findAllByTimestampBetween(int from, int to);
}
