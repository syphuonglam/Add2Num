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
        StringBuilder sb = new StringBuilder();
        int step = 1;

        while (i >= 0 || j >= 0 || carry != 0) {
            int da = i >= 0 ? a.charAt(i) - '0' : 0;
            int db = j >= 0 ? b.charAt(j) - '0' : 0;
            int total = da + db + carry;
            int digit = total % 10;
            int newCarry = total / 10;

            sb.append((char) ('0' + digit));

            String currentResult = sb.reverse().toString();
            String msg;
            if (carry == 0) {
                msg = String.format("Bước %d: Lấy %d cộng %d được %d. Lưu %d vào kết quả, được %s. Ghi nhớ %d.",
                        step, da, db, da + db, digit, currentResult, newCarry);
            } else {
                msg = String.format("Bước %d: Lấy %d cộng %d cộng nhớ %d được %d. Lưu %d vào kết quả, được %s. Ghi nhớ %d.",
                        step, da, db, carry, total, digit, currentResult, newCarry);
            }
            lastSteps.add(msg);
            LOGGER.info(msg);

            carry = newCarry;
            i--;
            j--;
            step++;
            sb.reverse();
        }

        String result = sb.reverse().toString();
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
