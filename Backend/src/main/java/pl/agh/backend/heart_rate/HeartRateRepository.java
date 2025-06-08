package pl.agh.backend.heart_rate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.agh.backend.heart_rate.model.HeartRate;

import java.util.List;
import java.util.Optional;

public interface HeartRateRepository extends JpaRepository<HeartRate, Integer> {
    List<HeartRate> findAllByTimestampBetween(int from, int to);
    @Query("SELECT MAX(h.nightId) FROM HeartRate h")
    Optional<Integer> findMaxNightId();
}
