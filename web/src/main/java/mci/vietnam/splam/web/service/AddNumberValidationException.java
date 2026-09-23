package mci.vietnam.splam.web.service;

public class AddNumberValidationException extends IllegalArgumentException {
    private final String code;
    private final String field;

    public AddNumberValidationException(String code, String message, String field) {
        super(message);
        this.code = code;
        this.field = field;
    }

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }
}