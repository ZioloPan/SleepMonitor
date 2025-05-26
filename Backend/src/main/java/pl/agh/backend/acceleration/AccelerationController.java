package pl.agh.backend.acceleration;

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
import pl.agh.backend.acceleration.model.command.CreateAccelerationCommand;
import pl.agh.backend.acceleration.model.dto.AccelerationDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/acceleration")
public class AccelerationController {

    private final AccelerationService accelerationService;

    @GetMapping
    public List<AccelerationDto> getAll() {
        return accelerationService.getAll();
    }

    @GetMapping("/{id}")
    public AccelerationDto getById(@PathVariable int id) {
        return accelerationService.getById(id);
    }

    @GetMapping(params = {"from", "to"})
    public List<AccelerationDto> getByTimestampRange(@RequestParam int from, @RequestParam int to) {
        return accelerationService.getByTimestampRange(from, to);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccelerationDto create(@RequestBody @Valid CreateAccelerationCommand command) {
        return accelerationService.create(command);
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFromTxt(@RequestParam("file") MultipartFile file) {
        accelerationService.saveFromTxt(file);
    }
}
