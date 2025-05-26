package pl.agh.backend.heart_rate;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.agh.backend.heart_rate.model.HeartRate;
import pl.agh.backend.heart_rate.model.command.CreateHeartRateCommand;
import pl.agh.backend.heart_rate.model.dto.HeartRateDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HeartRateService {

    private final HeartRateRepository heartRateRepository;

    public List<HeartRateDto> getAll() {
        return heartRateRepository.findAll().stream()
                .map(HeartRateDto::fromEntity)
                .toList();
    }

    public HeartRateDto getById(int id) {
        return heartRateRepository.findById(id)
                .map(HeartRateDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("HeartRate with id = {0} not found", id)));
    }

    public HeartRateDto create(CreateHeartRateCommand command) {
        HeartRate heartRate = command.toEntity();
        return HeartRateDto.fromEntity(heartRateRepository.save(heartRate));
    }

    public void saveFromTxt(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            List<HeartRate> records = reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        String[] parts = line.split(",");
                        CreateHeartRateCommand command = new CreateHeartRateCommand();
                        command.setTimestamp(Integer.parseInt(parts[0].trim()));
                        command.setHeartRateValue(Double.parseDouble(parts[1].trim()));
                        return command.toEntity();
                    })
                    .toList();

            heartRateRepository.saveAll(records);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process uploaded file", e);
        }
    }
}
