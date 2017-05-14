package de.jverhoelen.cryptoalerts.datautils.converter;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringArrayConverterTest {

    private StringArrayConverter converter = new StringArrayConverter();

    @Test
    public void convertToDatabaseColumn() throws Exception {
        String result = converter.convertToDatabaseColumn(new String[]{"BTC", "ETC", "DASH"});
        assertThat(result, is("BTC,ETC,DASH"));
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        String[] arr = converter.convertToEntityAttribute("BTC,ETC,DASH");
        assertThat(arr[0], is("BTC"));
        assertThat(arr[1], is("ETC"));
        assertThat(arr[2], is("DASH"));
    }
}