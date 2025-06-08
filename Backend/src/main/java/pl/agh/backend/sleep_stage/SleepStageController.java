package pl.agh.backend.sleep_stage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.agh.backend.sleep_stage.model.command.CreateSleepStageCommand;
import pl.agh.backend.sleep_stage.model.dto.SleepStageDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sleep_stage")
public class SleepStageController {

    private final SleepStageService sleepStageService;

    @GetMapping
    public List<SleepStageDto> getAll() {
        return sleepStageService.getAll();
    }

    @GetMapping("/{id}")
    public SleepStageDto getById(@PathVariable int id) {
        return sleepStageService.getById(id);
    }

    @GetMapping(params = {"from", "to"})
    public List<SleepStageDto> getByTimestampRange(@RequestParam int from, @RequestParam int to) {
        return sleepStageService.getByTimestampRange(from, to);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SleepStageDto create(@RequestBody @Valid CreateSleepStageCommand command) {
        return sleepStageService.create(command);
    }

    @PostMapping("/predict")
    @ResponseStatus(HttpStatus.CREATED)
    public void predictSleepStages(@RequestParam int from, @RequestParam int to) {
        sleepStageService.predictAndSaveStages(from, to);
    }
}
