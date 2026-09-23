package mci.vietnam.splam.web.service;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import mci.vietnam.splam.core.domain.MyBigNumber;

@ExtendWith(MockitoExtension.class)
class AddNumberServiceTest {

    @Mock
    private MyBigNumber myBigNumber;

    @Test
    void delegatesValidOperandsAndReturnsTheSum() {
        AddNumberService service = new AddNumberService(myBigNumber);
        when(myBigNumber.sumWithoutHistory("000123", "000456")).thenReturn("579");

        assertEquals("579", service.add("000123", "000456"));

        verify(myBigNumber).sumWithoutHistory("000123", "000456");
    }

    @ParameterizedTest
    @MethodSource("invalidOperands")
    void rejectsInvalidOperandsBeforeDelegating(String firstOperand, String secondOperand,
                                                String expectedCode, String expectedField,
                                                String expectedMessage) {
        AddNumberService service = new AddNumberService(myBigNumber);

        AddNumberValidationException exception = assertThrows(AddNumberValidationException.class,
            () -> service.add(firstOperand, secondOperand));

        assertEquals(expectedCode, exception.getCode());
        assertEquals(expectedField, exception.getField());
        assertEquals(expectedMessage, exception.getMessage());
        verifyNoInteractions(myBigNumber);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> invalidOperands() {
        return Stream.of(
            arguments(null, "456", "VALIDATION_ERROR", "a", "Field must not be null."),
            arguments("123", null, "VALIDATION_ERROR", "b", "Field must not be null."),
            arguments("", "456", "VALIDATION_ERROR", "a", "Field must not be blank."),
            arguments("123", "   ", "VALIDATION_ERROR", "b", "Field must not be blank."),
            arguments("12x3", "456", "VALIDATION_ERROR", "a", "Field must contain ASCII digits only."),
            arguments("-1", "456", "VALIDATION_ERROR", "a", "Field must be a non-negative integer."),
            arguments("123 ", "456", "VALIDATION_ERROR", "a", "Field must contain ASCII digits only."),
            arguments("9".repeat(AddNumberService.MAX_OPERAND_LENGTH + 1), "1",
                "INPUT_TOO_LARGE", "a", "Input exceeds the maximum allowed length.")
        );
    }
}