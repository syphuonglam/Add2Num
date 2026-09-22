package mci.vietnam.splam.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class MyBigNumber {
    private static final Logger LOGGER = Logger.getLogger(MyBigNumber.class.getName());
    private final List<String> lastSteps = new ArrayList<>();

    // Required signature
    public String sum(String stn1, String stn2) {
        return sumWithHistory(stn1, stn2).getResult();
    }

    // Domain-friendly API returning history
    public BigNumberResult sumWithHistory(String stn1, String stn2) {
        lastSteps.clear();

        String a = normalize(stn1);
        String b = normalize(stn2);

        int i = a.length() - 1;
        int j = b.length() - 1;
        int carry = 0;
        char[] resultDigits = new char[Math.max(a.length(), b.length()) + 1];
        int resultStart = resultDigits.length;
        int step = 1;
        int da;
        int db;
        int total;
        int digit;
        int newCarry;
        int resultLength;
        String currentResult;
        String msg;
        StringBuilder messageBuilder = new StringBuilder(128);

        while (i >= 0 || j >= 0 || carry != 0) {
            da = i >= 0 ? a.charAt(i) - '0' : 0;
            db = j >= 0 ? b.charAt(j) - '0' : 0;
            total = da + db + carry;
            digit = total % 10;
            newCarry = total / 10;

            resultDigits[--resultStart] = (char) ('0' + digit);
            resultLength = resultDigits.length - resultStart;

            currentResult = new String(resultDigits, resultStart, resultLength);
                messageBuilder.setLength(0);
            if (carry == 0) {
                messageBuilder.append("Bước ").append(step).append(": Lấy ").append(da).append(" cộng ")
                    .append(db).append(" được ").append(da + db).append(". Lưu ").append(digit)
                    .append(" vào kết quả, được ").append(currentResult).append(". Ghi nhớ ").append(newCarry)
                    .append('.');
            } else {
                messageBuilder.append("Bước ").append(step).append(": Lấy ").append(da).append(" cộng ")
                    .append(db).append(" cộng nhớ ").append(carry).append(" được ").append(total)
                    .append(". Lưu ").append(digit).append(" vào kết quả, được ").append(currentResult)
                    .append(". Ghi nhớ ").append(newCarry).append('.');
            }
                msg = messageBuilder.toString();
            lastSteps.add(msg);
            LOGGER.info(msg);

            carry = newCarry;
            i--;
            j--;
            step++;
        }

        String result = new String(resultDigits, resultStart, resultDigits.length - resultStart);
        return new BigNumberResult(result, Collections.unmodifiableList(new ArrayList<>(lastSteps)));
    }

    private String normalize(String value) {
        if (value == null || value.isEmpty()) {
            return "0";
        }
        String normalized = value.replaceAll("^0+", "");
        return normalized.isEmpty() ? "0" : normalized;
    }

    public List<String> getLastSteps() {
        return Collections.unmodifiableList(lastSteps);
    }
}
