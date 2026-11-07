package PaisaPulseBackend;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    private final GeminiService geminiService;

    public TransactionController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    public record ProcessRequest(
            @NotBlank
            @Size(max = 10000)
            @JsonProperty("raw_sms")
            String rawSms,

            @NotBlank
            @Size(max = 128)
            @JsonProperty("device_id")
            String deviceId,

            @Size(max = 32)
            String source
    ) {}

    @PostMapping("/process")
    public ResponseEntity<TransactionResponse> process(
            @Valid @RequestBody ProcessRequest request
    ) {
        TransactionResponse response =
                geminiService.categorize(request.rawSms());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("PaisaPulse backend is running");
    }
}