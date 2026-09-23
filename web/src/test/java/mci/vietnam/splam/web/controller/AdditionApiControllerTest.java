package mci.vietnam.splam.web.controller;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({AdditionController.class, AdditionRestController.class})
@org.springframework.context.annotation.Import({
    mci.vietnam.splam.web.config.CoreConfiguration.class,
    mci.vietnam.splam.web.service.AddNumberService.class
})
class AdditionApiControllerTest {

    private static final String API_PATH = "/api/v1/add";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addsNormalNumbersAndDoesNotReturnSteps() throws Exception {
        mockMvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"a\":\"123\",\"b\":\"456\"}"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.result").value("579"))
            .andExpect(jsonPath("$.steps").doesNotExist());
    }

    @Test
    void handlesDifferentLengthsAndCarryPropagation() throws Exception {
        mockMvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"a\":\"999999\",\"b\":\"1\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("1000000"));
    }

    @Test
    void normalizesLeadingZerosAndZeroValues() throws Exception {
        mockMvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"a\":\"000123\",\"b\":\"000456\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("579"));

        mockMvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"a\":\"0000\",\"b\":\"0\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value("0"));
    }

    @Test
    void handlesVeryLargeValuesAsStrings() throws Exception {
        String firstOperand = "9".repeat(2000);
        String secondOperand = "1";
        String expectedResult = "1" + "0".repeat(2000);

        mockMvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"a\":\"" + firstOperand + "\",\"b\":\"" + secondOperand + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(expectedResult));
    }

    @ParameterizedTest
    @MethodSource("invalidOperandRequests")
    void rejectsInvalidOperands(String requestBody, String field, String message) throws Exception {
        mockMvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.field").value(field))
            .andExpect(jsonPath("$.message", not(containsString("123"))));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> invalidOperandRequests() {
        return Stream.of(
            Arguments.of("{\"a\":null,\"b\":\"456\"}", "a", "Field must not be null."),
            Arguments.of("{\"a\":\"\",\"b\":\"456\"}", "a", "Field must not be blank."),
            Arguments.of("{\"a\":\"   \",\"b\":\"456\"}", "a", "Field must not be blank."),
            Arguments.of("{\"a\":\"123\",\"b\":null}", "b", "Field must not be null."),
            Arguments.of("{\"a\":\"123\",\"b\":\"\"}", "b", "Field must not be blank."),
            Arguments.of("{\"a\":\"123\",\"b\":\"   \"}", "b", "Field must not be blank."),
            Arguments.of("{\"b\":\"456\"}", "a", "Field is required."),
            Arguments.of("{\"a\":\"123\"}", "b", "Field is required."),
            Arguments.of("{\"a\":\"12x3\",\"b\":\"456\"}", "a", "Field must contain ASCII digits only."),
            Arguments.of("{\"a\":\"-1\",\"b\":\"456\"}", "a", "Field must be a non-negative integer."),
            Arguments.of("{\"a\":\"1.0\",\"b\":\"456\"}", "a", "Field must contain ASCII digits only."),
            Arguments.of("{\"a\":123,\"b\":\"456\"}", "a", "Field must be a JSON string."),
            Arguments.of("{\"a\":\"123 \",\"b\":\"456\"}", "a", "Field must contain ASCII digits only.")
        );
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mockMvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"a\":\"123\",\"b\":\"456\""))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("MALFORMED_JSON"))
            .andExpect(jsonPath("$.message").value("Request body contains malformed JSON."));
    }

    @Test
    void rejectsOversizedInputBeforeCalculation() throws Exception {
        String oversizedOperand = "9".repeat(100001);

        mockMvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"a\":\"" + oversizedOperand + "\",\"b\":\"1\"}"))
            .andExpect(result -> assertTrue(result.getResponse().getStatus() == 400
                || result.getResponse().getStatus() == 413))
            .andExpect(jsonPath("$.code").value("INPUT_TOO_LARGE"))
            .andExpect(jsonPath("$.field").value("a"));
    }

    @Test
    void rejectsRequestBodyThatExceedsTransportLimit() throws Exception {
        String maximumOperand = "9".repeat(100000);
        String requestBody = "{\"a\":\"" + maximumOperand
            + "\",\"b\":\"" + maximumOperand
            + "\",\"padding\":\"12345678901234567890\"}";

        mockMvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isPayloadTooLarge())
            .andExpect(jsonPath("$.code").value("INPUT_TOO_LARGE"))
            .andExpect(jsonPath("$.message")
                .value("Request body exceeds the maximum allowed size."));
    }

    @Test
    void rejectsUnsupportedContentType() throws Exception {
        mockMvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("a=123&b=456"))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(jsonPath("$.code").value("UNSUPPORTED_MEDIA_TYPE"));
    }

    @Test
    void rejectsUnsupportedHttpMethods() throws Exception {
        mockMvc.perform(get(API_PATH))
            .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(put(API_PATH))
            .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(patch(API_PATH))
            .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete(API_PATH))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void preservesExistingHtmlUiRoutes() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));

        mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("a", "1234")
                .param("b", "897"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("result", "2131"))
            .andExpect(model().attributeExists("steps"));
    }

    @Test
    void keepsConcurrentResultsIndependent() throws Exception {
        List<String> requestBodies = List.of(
            "{\"a\":\"999\",\"b\":\"1\"}",
            "{\"a\":\"12345678901234567890\",\"b\":\"10\"}",
            "{\"a\":\"000123\",\"b\":\"000456\"}"
        );
        List<String> expectedResults = List.of("1000", "12345678901234567900", "579");
        ExecutorService executor = Executors.newFixedThreadPool(requestBodies.size());

        try {
            List<Future<String>> responses = requestBodies.stream()
                .map(requestBody -> executor.submit(() -> mockMvc.perform(post(API_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString()))
                .toList();

            for (int index = 0; index < responses.size(); index++) {
                assertEquals("{\"result\":\"" + expectedResults.get(index) + "\"}",
                    responses.get(index).get());
            }
        } finally {
            executor.shutdownNow();
        }
    }
}