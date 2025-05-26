package pl.agh.backend.heart_rate;

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
import pl.agh.backend.heart_rate.model.command.CreateHeartRateCommand;
import pl.agh.backend.heart_rate.model.dto.HeartRateDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/heart_rate")
public class HeartRateController {

    private final HeartRateService heartRateService;

    @GetMapping
    public List<HeartRateDto> getAll() {
        return heartRateService.getAll();
    }

    @GetMapping("/{id}")
    public HeartRateDto getById(@PathVariable int id) {
        return heartRateService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HeartRateDto create(@RequestBody @Valid CreateHeartRateCommand command) {
        return heartRateService.create(command);
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFromTxt(@RequestParam("file") MultipartFile file) {
        heartRateService.saveFromTxt(file);
    }
}
