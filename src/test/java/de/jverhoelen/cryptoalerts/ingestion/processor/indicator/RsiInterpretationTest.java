package de.jverhoelen.cryptoalerts.ingestion.processor.indicator;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RsiInterpretationTest {

    @Test
    public void interpretRsi() throws Exception {
        // extreme cases
        RsiInterpretation overSeventy = RsiInterpretation.interpretRsi(new BigDecimal(71.0));
        assertThat(overSeventy, is(RsiInterpretation.OVER_BOUGHT));

        RsiInterpretation underThirty = RsiInterpretation.interpretRsi(new BigDecimal(29.0));
        assertThat(underThirty, is(RsiInterpretation.OVER_SOLD));

        // null/empty and normal case
        RsiInterpretation noInterpretation = RsiInterpretation.interpretRsi(null);
        RsiInterpretation noInterpretationOnZero = RsiInterpretation.interpretRsi(new BigDecimal(0.0));
        RsiInterpretation noInterpretationInBetween = RsiInterpretation.interpretRsi(new BigDecimal(50.0));

        assertThat(noInterpretation, is(RsiInterpretation.NONE));
        assertThat(noInterpretationOnZero, is(RsiInterpretation.NONE));
        assertThat(noInterpretationInBetween, is(RsiInterpretation.NONE));
    }
}