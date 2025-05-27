package pl.agh.backend.sleep_stage;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.backend.sleep_stage.model.SleepStage;

import java.util.List;

public interface SleepStageRepository extends JpaRepository<SleepStage, Integer> {
    List<SleepStage> findAllByTimestampBetween(int from, int to);
}
