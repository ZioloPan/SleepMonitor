package pl.agh.backend.sleep_stage;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import pl.agh.backend.acceleration.AccelerationRepository;
import pl.agh.backend.acceleration.model.Acceleration;
import pl.agh.backend.heart_rate.HeartRateRepository;
import pl.agh.backend.heart_rate.model.HeartRate;
import pl.agh.backend.sleep_stage.model.SleepStage;
import pl.agh.backend.sleep_stage.model.command.CreateSleepStageCommand;
import pl.agh.backend.sleep_stage.model.dto.SleepStageDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SleepStageService {

    private final SleepStageRepository sleepStageRepository;
    private final HeartRateRepository heartRateRepository;
    private final AccelerationRepository accelerationRepository;

    public List<SleepStageDto> getAll() {
        return sleepStageRepository.findAll().stream()
                .map(SleepStageDto::fromEntity)
                .toList();
    }

    public SleepStageDto getById(int id) {
        return sleepStageRepository.findById(id)
                .map(SleepStageDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("HeartRate with id = {0} not found", id)));
    }

    public List<SleepStageDto> getByTimestampRange(int from, int to) {
        return sleepStageRepository.findAllByTimestampBetween(from, to).stream()
                .map(SleepStageDto::fromEntity)
                .toList();
    }

    public SleepStageDto create(CreateSleepStageCommand command) {
        SleepStage sleepStage = command.toEntity();
        return SleepStageDto.fromEntity(sleepStageRepository.save(sleepStage));
    }

    public void predictAndSaveStages(int from, int to) {
        List<HeartRate> heartRates = heartRateRepository.findAllByTimestampBetween(from, to);
        List<Acceleration> accelerations = accelerationRepository.findByTimestampBetween(from, to);

        List<Map<String, Object>> heartRateJson = heartRates.stream()
                .map(hr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", hr.getId());
                    map.put("timestamp", hr.getTimestamp());
                    map.put("heartRateValue", hr.getHeartRateValue());
                    return map;
                })
                .toList();

        List<Map<String, Object>> accelerationJson = accelerations.stream()
                .map(a -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", a.getId());
                    map.put("timestamp", a.getTimestamp());
                    map.put("accelerationX", a.getAccelerationX());
                    map.put("accelerationY", a.getAccelerationY());
                    map.put("accelerationZ", a.getAccelerationZ());
                    return map;
                })
                .toList();

        Map<String, Object> request = new HashMap<>();
        request.put("heart_rate", heartRateJson);
        request.put("acceleration", accelerationJson);
        request.put("night_id", heartRates.get(0).getNightId());


        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity("http://127.0.0.1:5000/predict", request, Map.class);
        Map<String, Object> responseBody = response.getBody();


        int nightId = (Integer) responseBody.get("night_id");

        Map<String, String> predictions = (Map<String, String>) responseBody.get("predictions");

        List<SleepStage> predictedStages = predictions.entrySet().stream()
                .map(e -> SleepStage.builder()
                        .timestamp(Integer.parseInt(e.getKey()))
                        .stage(e.getValue())
                        .nightId(nightId)
                        .build())
                .toList();

        sleepStageRepository.saveAll(predictedStages);
    }
}