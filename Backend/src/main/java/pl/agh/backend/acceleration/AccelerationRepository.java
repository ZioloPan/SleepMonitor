package pl.agh.backend.acceleration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.agh.backend.acceleration.model.Acceleration;

import java.util.List;
import java.util.Optional;

public interface AccelerationRepository extends JpaRepository<Acceleration, Integer> {
    List<Acceleration> findByTimestampBetween(int from, int to);
    @Query("SELECT MAX(a.nightId) FROM Acceleration a")
    Optional<Integer> findMaxNightId();

}
