package pl.agh.backend.sleep_stage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.agh.backend.sleep_stage.model.command.CreateSleepStageCommand;
import pl.agh.backend.sleep_stage.model.dto.NightSummaryDto;
import pl.agh.backend.sleep_stage.model.dto.SleepStageDto;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sleep_stage")
public class SleepStageController {

    private final SleepStageService sleepStageService;
    private final SleepStageRepository sleepStageRepository;

    @GetMapping
    public List<SleepStageDto> getAll() {
        return sleepStageService.getAll();
    }

    @GetMapping("/{id}")
    public SleepStageDto getById(@PathVariable int id) {
        return sleepStageService.getById(id);
    }

    @GetMapping("/night/{id}")
    public List<SleepStageDto> getByTimestampRange(@PathVariable int id) {
        return sleepStageService.getByTimestampRange(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SleepStageDto create(@RequestBody @Valid CreateSleepStageCommand command) {
        return sleepStageService.create(command);
    }

    @PostMapping("/predict/{night_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void predictSleepStages(@PathVariable int night_id) throws IOException {
        sleepStageService.predictAndSaveStages(night_id);
    }

    @GetMapping("/nights")
    public List<NightSummaryDto> getNightSummaries() {
        return sleepStageService.getNightSummaries();
    }
}
