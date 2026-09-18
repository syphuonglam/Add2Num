package mci.vietnam.splam.core.domain;

import java.util.List;

public class BigNumberResult {
    private final String result;
    private final List<String> steps;

    public BigNumberResult(String result, List<String> steps) {
        this.result = result;
        this.steps = steps;
    }

    public String getResult() {
        return result;
    }

    public List<String> getSteps() {
        return steps;
    }
}
