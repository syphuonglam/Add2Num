package mci.vietnam.splam.web.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import mci.vietnam.splam.core.domain.MyBigNumber;

@Service
public class AddNumberService {
    public static final int MAX_OPERAND_LENGTH = 100_000;

    private final MyBigNumber myBigNumber;

    public AddNumberService(MyBigNumber myBigNumber) {
        this.myBigNumber = Objects.requireNonNull(myBigNumber, "myBigNumber must not be null");
    }

    public String add(String firstOperand, String secondOperand) {
        validateOperand(firstOperand, "a");
        validateOperand(secondOperand, "b");
        return myBigNumber.sumWithoutHistory(firstOperand, secondOperand);
    }

    private void validateOperand(String operand, String field) {
        if (operand == null) {
            throw new AddNumberValidationException(
                "VALIDATION_ERROR", "Field must not be null.", field);
        }
        if (operand.isEmpty() || operand.trim().isEmpty()) {
            throw new AddNumberValidationException(
                "VALIDATION_ERROR", "Field must not be blank.", field);
        }
        if (operand.length() > MAX_OPERAND_LENGTH) {
            throw new AddNumberValidationException(
                "INPUT_TOO_LARGE", "Input exceeds the maximum allowed length.", field);
        }
        if (!operand.matches("[0-9]+")) {
            String message = operand.startsWith("-")
                ? "Field must be a non-negative integer."
                : "Field must contain ASCII digits only.";
            throw new AddNumberValidationException("VALIDATION_ERROR", message, field);
        }
    }
}