package ro.axonsoft.eval.minibank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ro.axonsoft.eval.minibank.dto.request.TransferCreateRequest;
import ro.axonsoft.eval.minibank.dto.response.TransferResponse;
import ro.axonsoft.eval.minibank.service.TransfersService;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("api/transfers")
@RequiredArgsConstructor
public class TransfersController {
    private final TransfersService transfersService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse createTransfer(@RequestBody TransferCreateRequest request) {
        return transfersService.createTransfer(request);
    }

    @GetMapping("/{id}")
    public TransferResponse getTransfer(@PathVariable Long id) {
        return transfersService.getTransfer(id);
    }

    @GetMapping
    public Map<String, Object> getAllTransfers(
            @RequestParam(required = false) String iban,
            @RequestParam(required = false) Instant fromDate,
            @RequestParam(required = false) Instant toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return transfersService.getAllTransfers(iban, fromDate, toDate, page, size);
    }

}
