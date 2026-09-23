package mci.vietnam.splam.web.controller;

public class ApiErrorResponse {
    private final String code;
    private final String message;
    private final String field;
    private final String requestId;

    public ApiErrorResponse(String code, String message, String field, String requestId) {
        this.code = code;
        this.message = message;
        this.field = field;
        this.requestId = requestId;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getField() {
        return field;
    }

    public String getRequestId() {
        return requestId;
    }
}