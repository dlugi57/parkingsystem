package com.parkit.parkingsystem;

import com.parkit.parkingsystem.util.RoundUtil;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RoundUtilTest {
    @Test
    @DisplayName("Given value and places after comma, when make the round calculation, then result will be double with two numbers after comma")
    public void roundTest() {
        // GIVEN
        double value = 2.12345;
        int places = 2;
        // WHEN & THEN
        assertThat(RoundUtil.round(value, places)).isEqualTo(2.12);
    }

    @Test
    @DisplayName("Given value and negative places after comma, when make the round calculation, then exception will be thrown")
    public void roundExceptionTest() {
        // GIVEN
        double value = 2.12345;
        int places = -1;
        // WHEN & THEN
        assertThatThrownBy(() -> RoundUtil.round(value, places)).isInstanceOf(IllegalArgumentException.class);
    }

}
