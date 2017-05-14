package de.jverhoelen.cryptoalerts.datautils.converter;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MapConverterTest {

    private MapConverter converter = new MapConverter();

    @Test
    public void convertToDatabaseColumn() throws Exception {
        String json = converter.convertToDatabaseColumn(
                ImmutableMap.<String, Double>builder()
                        .put("something", 666.0)
                        .put("thing", 333.0)
                        .build()
        );

        assertTrue(json.contains("\"something\":666.0"));
        assertTrue(json.contains("\"thing\":333.0"));
        assertTrue(json.contains("{"));
        assertTrue(json.contains("}"));

        String nullMap = converter.convertToDatabaseColumn(null);
        assertThat(nullMap, is("null"));
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        Map<String, Double> emptyMap = converter.convertToEntityAttribute("{}");
        assertThat(emptyMap.size(), is(0));

        Map<String, Double> nullMap = converter.convertToEntityAttribute("null");
        assertTrue(nullMap == null);
    }
}