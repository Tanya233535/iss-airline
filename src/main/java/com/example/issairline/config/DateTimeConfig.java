package com.example.issairline.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class DateTimeConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {

        registry.addFormatter(new Formatter<LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

            @Override
            public LocalDateTime parse(String text, Locale locale) throws ParseException {
                return (text == null || text.isEmpty()) ? null : LocalDateTime.parse(text, formatter);
            }

            @Override
            public String print(LocalDateTime object, Locale locale) {
                return (object == null) ? "" : object.format(formatter);
            }
        });

        registry.addFormatter(new Formatter<LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public LocalDate parse(String text, Locale locale) throws ParseException {
                return (text == null || text.isEmpty()) ? null : LocalDate.parse(text, formatter);
            }

            @Override
            public String print(LocalDate object, Locale locale) {
                return (object == null) ? "" : object.format(formatter);
            }
        });
    }
}
