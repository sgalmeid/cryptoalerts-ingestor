package de.jverhoelen.cryptoalerts.datautils.converter;

import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StringArrayConverter implements AttributeConverter<String[], String> {

    @Override
    public String convertToDatabaseColumn(String[] strings) {
        if (strings == null) {
            return "";
        }

        return String.join(",", strings);
    }

    @Override
    public String[] convertToEntityAttribute(String s) {
        if (StringUtils.isEmpty(s)) {
            return new String[0];
        }

        return s.split(",");
    }
}
