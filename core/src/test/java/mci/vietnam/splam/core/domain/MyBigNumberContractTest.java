package mci.vietnam.splam.core.domain;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class MyBigNumberContractTest {

    @Test
    void returnsCanonicalResultForLeadingZerosAndZero() {
        MyBigNumber number = new MyBigNumber();

        assertEquals("579", number.sum("000123", "000456"));
        assertEquals("0", number.sum("0000", "0"));
    }

    @Test
    void returnsIndependentHistorySnapshotForEachCalculation() {
        MyBigNumber number = new MyBigNumber();

        BigNumberResult first = number.sumWithHistory("99", "1");
        BigNumberResult second = number.sumWithHistory("1", "2");

        assertEquals("100", first.getResult());
        assertEquals("3", second.getResult());
        assertTrue(first.getSteps().size() >= 2);
        assertEquals(1, second.getSteps().size());
        assertNotSame(first.getSteps(), second.getSteps());
        assertEquals(List.of("Bước 1: Lấy 1 cộng 2 được 3. Lưu 3 vào kết quả, được 3. Ghi nhớ 0."),
            second.getSteps());
    }
}