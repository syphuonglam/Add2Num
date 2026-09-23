package mci.vietnam.splam.web.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import mci.vietnam.splam.web.service.AddNumberService;
import mci.vietnam.splam.web.service.AddNumberValidationException;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdditionRestController {
    private final AddNumberService addNumberService;

    public AdditionRestController(AddNumberService addNumberService) {
        this.addNumberService = addNumberService;
    }

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AddNumberResponse add(@RequestBody AddNumberRequest request) {
        return new AddNumberResponse(addNumberService.add(
            operandAsString(request.getA(), request.isAPresent(), "a"),
            operandAsString(request.getB(), request.isBPresent(), "b")));
    }

    private String operandAsString(JsonNode operand, boolean present, String field) {
        if (!present) {
            throw new AddNumberValidationException(
                "VALIDATION_ERROR", "Field is required.", field);
        }
        if (operand == null || operand.isNull()) {
            throw new AddNumberValidationException(
                "VALIDATION_ERROR", "Field must not be null.", field);
        }
        if (!operand.isTextual()) {
            throw new AddNumberValidationException(
                "VALIDATION_ERROR", "Field must be a JSON string.", field);
        }
        return operand.textValue();
    }
}