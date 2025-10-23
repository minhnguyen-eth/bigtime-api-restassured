package helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateHelper {

    public static String getFirstDayOfCurrentMonth() {
        return LocalDate.now()
                .withDayOfMonth(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String getLastDayOfCurrentMonth() {
        LocalDate now = LocalDate.now();
        return now.withDayOfMonth(now.lengthOfMonth())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
