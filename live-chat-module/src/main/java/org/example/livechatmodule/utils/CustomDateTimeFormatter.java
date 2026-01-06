package org.example.livechatmodule.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateTimeFormatter {

    public static final DateTimeFormatter COMMENT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM HH:mm");

    public static String formatCommentDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(COMMENT_DATE_FORMATTER) : "";
    }
}
