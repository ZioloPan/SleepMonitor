package pl.agh.backend.sleep_stage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.agh.backend.sleep_stage.model.SleepStage;
import pl.agh.backend.sleep_stage.model.dto.NightSummaryDto;

import java.util.Collection;
import java.util.List;

public interface SleepStageRepository extends JpaRepository<SleepStage, Integer> {
    List<SleepStage> findAllByTimestampBetween(int from, int to);

    List<SleepStage> findAllByNightId(int nightId);

    List<SleepStage> findAllByOrderByNightIdAscTimestampAsc();
}
