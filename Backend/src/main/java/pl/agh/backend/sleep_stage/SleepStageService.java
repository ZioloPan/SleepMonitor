package pl.agh.backend.sleep_stage;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.agh.backend.sleep_stage.model.SleepStage;
import pl.agh.backend.sleep_stage.model.command.CreateSleepStageCommand;
import pl.agh.backend.sleep_stage.model.dto.SleepStageDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SleepStageService {

    private final SleepStageRepository sleepStageRepository;

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

    public void saveFromTxt(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            List<SleepStage> records = reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        String[] parts = line.split(",");
                        CreateSleepStageCommand command = new CreateSleepStageCommand();
                        command.setTimestamp(Integer.parseInt(parts[0].trim()));
                        command.setStage(Integer.parseInt(parts[1].trim()));
                        return command.toEntity();
                    })
                    .toList();

            sleepStageRepository.saveAll(records);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process uploaded file", e);
        }
    }

}
