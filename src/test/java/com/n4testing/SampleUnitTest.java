package com.n4testing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleUnitTest {

    @Test
    void testSampleLogic() {
        int expected = 2;
        int actual = 1 + 1;
        assertEquals(expected, actual, "Simple math should work!");
    }
}
