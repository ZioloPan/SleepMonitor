package pl.agh.backend.sleep_stage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.agh.backend.acceleration.AccelerationRepository;
import pl.agh.backend.acceleration.model.Acceleration;
import pl.agh.backend.heart_rate.HeartRateRepository;
import pl.agh.backend.heart_rate.model.HeartRate;
import pl.agh.backend.sleep_stage.model.SleepStage;
import pl.agh.backend.sleep_stage.model.command.CreateSleepStageCommand;
import pl.agh.backend.sleep_stage.model.dto.NightSummaryDto;
import pl.agh.backend.sleep_stage.model.dto.PredictionDto;
import pl.agh.backend.sleep_stage.model.dto.SleepStageDto;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<SleepStageDto> getByTimestampRange(int night_id) {
        List<SleepStageDto> stages = sleepStageRepository.findAllByNightId(night_id).stream()
                .map(SleepStageDto::fromEntity)
                .toList();

        List<SleepStageDto> every30thElement = new ArrayList<>();
        for (int i = 0; i < stages.size(); i += 30) {
            every30thElement.add(stages.get(i));
        }
        return every30thElement;
    }

    public SleepStageDto create(CreateSleepStageCommand command) {
        SleepStage sleepStage = command.toEntity();
        return SleepStageDto.fromEntity(sleepStageRepository.save(sleepStage));
    }

    public void predictAndSaveStages(int night_id) throws IOException {
        List<HeartRate> heartRates = heartRateRepository.findAllByNightId(night_id);
        List<Acceleration> accelerations = accelerationRepository.findAllByNightId(night_id);

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
        ResponseEntity<String> response = restTemplate.postForEntity("http://127.0.0.1:5000/predict", request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        JsonNode predictionsNode = root.get("predictions");
        List<PredictionDto> predictions = mapper.readValue(
                predictionsNode.traverse(),
                new TypeReference<List<PredictionDto>>() {}
        );

        List<SleepStage> predictedStages = predictions.stream()
                .map(p -> SleepStage.builder()
                        .timestamp(p.getSecond_of_sleep())
                        .stage(p.getStage())
                        .nightId(p.getNight_id())
                        .build())
                .toList();

        sleepStageRepository.saveAll(predictedStages);
    }

    public List<NightSummaryDto> getNightSummaries() {
        return sleepStageRepository.findAllByOrderByNightIdAscTimestampAsc().stream()
                .collect(Collectors.groupingBy(SleepStage::getNightId, HashMap::new, Collectors.toList()))
                .entrySet().stream()
                .map(entry -> {
                    int nightId = entry.getKey();
                    int firstTimestamp = entry.getValue().get(0).getTimestamp();
                    LocalDateTime dateTime = Instant.ofEpochSecond(firstTimestamp)
                            .atZone(ZoneId.of("Europe/Warsaw"))
                            .toLocalDateTime();
                    return new NightSummaryDto(nightId, dateTime);
                })
                .toList();
    }
}