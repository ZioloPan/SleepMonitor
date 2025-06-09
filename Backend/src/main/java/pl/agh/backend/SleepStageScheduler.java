package pl.agh.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.agh.backend.heart_rate.HeartRateRepository;
import pl.agh.backend.sleep_stage.SleepStageService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SleepStageScheduler {

    private final SleepStageService sleepStageService;
    private final HeartRateRepository heartRateRepository;

    @Scheduled(cron = "0 0 12 * * *")
    public void runPredictionOnceDaily() {
        Integer maxNightId = heartRateRepository.findMaxNightId().orElse(null);

        if (maxNightId != null) {
            try {
                sleepStageService.predictAndSaveStages(maxNightId);
            } catch (IOException e) {
                // Możesz zalogować błąd lub wysłać alert
                System.err.println("Błąd podczas predykcji dla night_id = " + maxNightId);
                e.printStackTrace();
            }
        }
    }
}