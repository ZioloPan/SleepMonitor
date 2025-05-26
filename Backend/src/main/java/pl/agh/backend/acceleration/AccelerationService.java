package pl.agh.backend.acceleration;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.agh.backend.acceleration.model.Acceleration;
import pl.agh.backend.acceleration.model.command.CreateAccelerationCommand;
import pl.agh.backend.acceleration.model.dto.AccelerationDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccelerationService {

    private final AccelerationRepository accelerationRepository;

    public List<AccelerationDto> getAll() {
        return accelerationRepository.findAll().stream()
                .map(AccelerationDto::fromEntity)
                .toList();
    }

    public AccelerationDto getById(int id) {
        return accelerationRepository.findById(id)
                .map(AccelerationDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Acceleration with id ={0} not found", id)));
    }

    public List<AccelerationDto> getByTimestampRange(int from, int to) {
        return accelerationRepository.findByTimestampBetween(from, to)
                .stream()
                .map(AccelerationDto::fromEntity)
                .toList();
    }

    public AccelerationDto create(CreateAccelerationCommand command) {
        Acceleration acceleration = command.toEntity();
        return AccelerationDto.fromEntity(accelerationRepository.save(acceleration));
    }

    public void saveFromTxt(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            List<Acceleration> records = reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        String[] parts = line.split(",");
                        CreateAccelerationCommand command = new CreateAccelerationCommand();
                        command.setTimestamp(Integer.parseInt(parts[0].trim()));
                        command.setAccelerationX(Double.parseDouble(parts[1].trim()));
                        command.setAccelerationY(Double.parseDouble(parts[2].trim()));
                        command.setAccelerationZ(Double.parseDouble(parts[3].trim()));
                        return command.toEntity();
                    })
                    .toList();

            accelerationRepository.saveAll(records);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process uploaded file", e);
        }
    }
}
