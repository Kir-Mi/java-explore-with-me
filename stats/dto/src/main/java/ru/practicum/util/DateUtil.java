package ru.practicum.util;

import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtil {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public LocalDateTime toDate(final String date) {
        return LocalDateTime.parse(date, FORMATTER);
    }

    public String toFormattedString(LocalDateTime date) {
        return date.format(FORMATTER);
    }

    public String encodeToString(LocalDateTime date) {
        String formatted = toFormattedString(date);
        return URLEncoder.encode(formatted, StandardCharsets.UTF_8);
    }

    public LocalDateTime decodeFromString(String encodedDate) {
        String decoded = URLDecoder.decode(encodedDate, StandardCharsets.UTF_8);
        return LocalDateTime.parse(decoded, FORMATTER);
    }
}