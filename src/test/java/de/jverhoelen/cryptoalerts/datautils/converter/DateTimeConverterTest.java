package de.jverhoelen.cryptoalerts.datautils.converter;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DateTimeConverterTest {

    DateTimeConverter converter = new DateTimeConverter();

    @Test
    public void convertToDatabaseColumn() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        Timestamp timestamp = converter.convertToDatabaseColumn(now);
        assertFalse(timestamp == null);
        assertThat(timestamp.getNanos(), is(now.getNano()));

        Timestamp timestampNull = converter.convertToDatabaseColumn(null);
        assertTrue(timestampNull == null);
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        long oneMinAgo = new Date().getTime() - 60000;

        LocalDateTime time = converter.convertToEntityAttribute(new Timestamp(oneMinAgo));
        ZonedDateTime zonedTime = time.atZone(ZoneId.systemDefault());

        assertThat(zonedTime.toInstant().toEpochMilli(), is(oneMinAgo));

        LocalDateTime localDateTimeNull = converter.convertToEntityAttribute(null);
        assertTrue(localDateTimeNull == null);
    }
}