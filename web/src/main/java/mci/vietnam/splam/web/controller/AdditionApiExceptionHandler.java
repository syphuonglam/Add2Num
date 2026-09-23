package mci.vietnam.splam.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import mci.vietnam.splam.web.config.RequestBodyLimitFilter;
import mci.vietnam.splam.web.service.AddNumberValidationException;

@RestControllerAdvice
public class AdditionApiExceptionHandler {

    @ExceptionHandler(AddNumberValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(AddNumberValidationException exception) {
        HttpStatus status = "INPUT_TOO_LARGE".equals(exception.getCode())
            ? HttpStatus.PAYLOAD_TOO_LARGE
            : HttpStatus.BAD_REQUEST;
        return response(status, exception.getCode(), exception.getMessage(), exception.getField());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJson(HttpServletRequest request) {
        if (Boolean.TRUE.equals(request.getAttribute(RequestBodyLimitFilter.BODY_TOO_LARGE_ATTRIBUTE))) {
            return response(HttpStatus.PAYLOAD_TOO_LARGE, "INPUT_TOO_LARGE",
                "Request body exceeds the maximum allowed size.", null);
        }
        return response(HttpStatus.BAD_REQUEST, "MALFORMED_JSON",
            "Request body contains malformed JSON.", null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedMediaType() {
        return response(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE",
            "Content-Type must be application/json.", null);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotAcceptable() {
        return response(HttpStatus.NOT_ACCEPTABLE, "NOT_ACCEPTABLE",
            "Requested response format is not available.", null);
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String code,
                                                      String message, String field) {
        return ResponseEntity.status(status)
            .body(new ApiErrorResponse(code, message, field, null));
    }
}