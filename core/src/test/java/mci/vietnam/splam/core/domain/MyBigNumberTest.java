package mci.vietnam.splam.core.domain;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MyBigNumberTest {

    @Test
    public void simpleAddition() {
        MyBigNumber bn = new MyBigNumber();
        String res = bn.sum("1234", "897");
        assertEquals("2131", res);
        assertFalse(bn.getLastSteps().isEmpty());
    }

    @Test
    public void addWithCarry() {
        MyBigNumber bn = new MyBigNumber();
        assertEquals("1000", bn.sum("999", "1"));
        assertEquals("9999", bn.sum("4999", "5000"));
    }

    @Test
    public void addWithDifferentLength() {
        MyBigNumber bn = new MyBigNumber();
        assertEquals("120000", bn.sum("120000", "0"));
        assertEquals("1000000001", bn.sum("999999999", "2"));
    }

    @Test
    public void addZeroValues() {
        MyBigNumber bn = new MyBigNumber();
        assertEquals("0", bn.sum("0", "0"));
        assertEquals("123", bn.sum("123", "0"));
        assertEquals("123", bn.sum("0", "123"));
    }

    @Test
    public void addLargeNumbers() {
        MyBigNumber bn = new MyBigNumber();
        String a = "98765432101234567890";
        String b = "12345678909876543210";
        assertEquals("111111111011111111100", bn.sum(a, b));
    }

    @Test
    public void addLeadingZeroNumbers() {
        MyBigNumber bn = new MyBigNumber();
        assertEquals("579", bn.sum("000123", "000456"));
        assertEquals("0", bn.sum("0000", "0000"));
    }

    @Test
    public void addMaxCarrySequence() {
        MyBigNumber bn = new MyBigNumber();
        assertEquals("1000000", bn.sum("999999", "1"));
        assertEquals("1000000", bn.sum("1", "999999"));
    }

    @Test
    public void addSmallAndHugeNumbers() {
        MyBigNumber bn = new MyBigNumber();
        String huge = "999999999999999999999999999999999999999999999999999999999999999999";
        BigInteger expected = BigInteger.ONE.add(new BigInteger(huge));
        assertEquals(expected.toString(), bn.sum("1", huge));
    }

    @Test
    public void addVeryLongNumbersMatchesBigInteger() {
        Random rnd = new Random(42);
        int digits = 2000;
        String a = randomDigits(rnd, digits);
        String b = randomDigits(rnd, digits);

        MyBigNumber bn = new MyBigNumber();
        String res = bn.sum(a, b);

        BigInteger expected = new BigInteger(a).add(new BigInteger(b));
        assertEquals(expected.toString(), res);
        assertFalse(bn.getLastSteps().isEmpty());
    }

    private String randomDigits(Random rnd, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int d = rnd.nextInt(10);
            if (i == 0 && d == 0) {
                d = 1 + rnd.nextInt(9);
            }
            sb.append((char) ('0' + d));
        }
        return sb.toString();
    }
}
