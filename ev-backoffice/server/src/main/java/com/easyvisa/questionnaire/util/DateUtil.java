package com.easyvisa.questionnaire.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DateUtil {
    public static String EASY_VISA_DATE_FORMAT = "yyyy/MM/dd"; // This is the internal format
    public static String PDF_FORM_DATE_FORMAT = "MM/dd/yyyy";
    public static String USCIS_EDITION_DATE_FORMAT = "MM-dd-yyyy";
    public static String DATE_TIME_FORMAT = "MM/dd/yyyy h:mm:ss a";

    private static String DRUPAL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static String DOC_NOTE_DATE_FORMAT = "M/d/yyyy";
    public static String DOC_NOTE_TIME_FORMAT = "h:mm:ss aa";

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(EASY_VISA_DATE_FORMAT);
    private static DateTimeFormatter PDF_FORM_DATE_FORMATTER = DateTimeFormatter.ofPattern(PDF_FORM_DATE_FORMAT);
    private static DateTimeFormatter USCIS_DATE_FORMATTER = DateTimeFormatter.ofPattern(USCIS_EDITION_DATE_FORMAT);
    private static DateTimeFormatter USA_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    private static DateFormat USA_DATE_FORMAT = new SimpleDateFormat(PDF_FORM_DATE_FORMAT);
    private static DateFormat USCIS_DATE_FORMAT = new SimpleDateFormat(USCIS_EDITION_DATE_FORMAT);
    private static DateFormat DRUPAL_DATE_FORMAT = new SimpleDateFormat(DRUPAL_FORMAT, Locale.ENGLISH);

    private static Map<String, ChronoUnit> PERIOD_TYPES = new LinkedHashMap<>();

    static {
        PERIOD_TYPES.put("years", ChronoUnit.YEARS);
        PERIOD_TYPES.put("months", ChronoUnit.MONTHS);
        PERIOD_TYPES.put("weeks", ChronoUnit.WEEKS);
        PERIOD_TYPES.put("days", ChronoUnit.DAYS);
    }

    public static LocalDate localDate(String date) {
        LocalDate localDate = LocalDate.parse(normalizeEasyVisaDateFormat(date), dateTimeFormatter);
        return localDate;
    }

    public static Boolean isWithInNumberOfYears(LocalDate moveInDate, LocalDate currentDate, Integer noOfYears) {
        LocalDate noOfYearsFromMoveInDate = moveInDate.plusYears(noOfYears);
        return currentDate.isBefore(noOfYearsFromMoveInDate);
    }

    public static Boolean isExceedTheNumberOfYears(LocalDate moveInDate, LocalDate currentDate, Integer noOfYears) {
        Boolean hasWithInNumberOfYears = DateUtil.isWithInNumberOfYears(moveInDate, currentDate, noOfYears);
        return !hasWithInNumberOfYears;
    }

    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        long daysCount = ChronoUnit.DAYS.between(startDate, endDate);
        return daysCount;
    }

    public static LocalDate pdfLocalDate(String date) {
        return LocalDate.parse(date, PDF_FORM_DATE_FORMATTER);
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static String fromDate(Date date) {
        //default time zone
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        LocalDate localDate = instant.atZone(defaultZoneId).toLocalDate();
        return DateUtil.fromDate(localDate);
    }


    public static Date toDate(String dateStr) {
        //default time zone
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate localDate = DateUtil.localDate(dateStr);
        //local date + atStartOfDay() + default time zone + toInstant() = Date
        Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
        return date;
    }



    public static String fromDate(LocalDate localDate) {
        String dateStr = localDate.format(dateTimeFormatter);
        return dateStr;
    }

    public static String fromDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(USA_DATE_TIME_FORMAT);
    }

    public static String normalizeEasyVisaDateFormat(String date) {
        String inputDateValue = date;
        if (date.contains("-")) {
            inputDateValue = date.replaceAll("-", "/");
        }
        String normalizeDateValue = inputDateValue;
        String[] dateFormats = EASY_VISA_DATE_FORMAT.split("/");
        String[] inputDateValues = inputDateValue.split("/");
        if (inputDateValues.length == 3) {
            String normalizedYearValue = DateUtil.roundOff(inputDateValues[0], dateFormats[0].length());
            String normalizedMonthValue = DateUtil.roundOff(inputDateValues[1], dateFormats[1].length());
            String normalizedDayValue = DateUtil.roundOff(inputDateValues[2], dateFormats[2].length());
            normalizeDateValue = normalizedYearValue + "/" + normalizedMonthValue + "/" + normalizedDayValue;
        }
        return normalizeDateValue;
    }

    private static String roundOff(String inputValue, int numberOfChars) {
        String roundedVal = inputValue;
        while (roundedVal.length() < numberOfChars) {
            roundedVal = "0" + roundedVal;
        }
        return roundedVal;
    }


    public static String pdfFormDate(LocalDate localDate) {
        return localDate.format(PDF_FORM_DATE_FORMATTER);
    }

    public static String pdfFormDate(Date date) {
        return USA_DATE_FORMAT.format(date);
    }

    public static String uscisEditionDate(Date date) {
        if(date==null) {
            return "";
        }
        return USCIS_DATE_FORMAT.format(date);
    }

    public static String uscisEditionDate(String dateValue) {
        if(dateValue==null) {
            return "";
        }
        LocalDate localDate = DateUtil.pdfLocalDate(dateValue);
        return localDate.format(USCIS_DATE_FORMATTER);
    }

    public static Date drupalDate(String date) {
        try {
            return DRUPAL_DATE_FORMAT.parse(date);
        } catch (ParseException | NullPointerException e) {
            return null;
        }
    }

    public static String getPeriod(LocalDate start) {
        for (Map.Entry<String, ChronoUnit> entry : PERIOD_TYPES.entrySet()) {
            long numValue = entry.getValue().between(start, DateUtil.today());
            if (numValue > 0) {
                String desc = entry.getKey();
                if (numValue == 1) {
                    desc = desc.substring(0, desc.length() - 1);
                }
                return numValue + " " + desc;

            }
        }
        //should never be here
        return "1 day";
    }

    public static Date getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        clearTime(cal);
        return cal.getTime();
    }

    public static Date getCurrentQuarter() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, LocalDate.now().getMonth().firstMonthOfQuarter().getValue() - 1);
        clearTime(cal);
        return cal.getTime();
    }

    public static Date getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        clearTime(cal);
        return cal.getTime();
    }

    public static String questionnaireUpgradeCronExpression() {
        String UPGRADE_DATE_FORMAT = "ss mm HH dd MM ? yyyy";
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 15); // So job should run in next 15 minutes
        DateFormat upgradeDateTimeFormatter = new SimpleDateFormat(UPGRADE_DATE_FORMAT);
        String cronExpression = upgradeDateTimeFormatter.format(now.getTime());
        return cronExpression;
    }

    private static void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

}
