package controller.util;

/**
 *
 * @author Aimad.JAOUHAR
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtil {

    private static long CONST_DURATION_OF_HOUR = 1000 * 60 * 60;

    public static float calculTempsProductionParLigne(int quantite, int qtteProduiteParHeure) {
        if (qtteProduiteParHeure > 0) {
            return quantite / qtteProduiteParHeure;
        }
        return -1f;
    }

    public static long dateDiffEnHeure(Date grand, Date petit) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
        gregorianCalendar.setTime(petit);
        gregorianCalendar2.setTime(grand);
        long diffPe = Math.abs((gregorianCalendar2.getTime()).getTime() - (gregorianCalendar.getTime()).getTime());
        long numberOfHourPr = (long) diffPe / CONST_DURATION_OF_HOUR;
        return numberOfHourPr - 1;
    }

    public static int[] getPeriodeMinMax(int nbrePeriode) {
//        return new int[]{indexDate*24, 24*(indexDate+1)};
        return new int[]{0, nbrePeriode};
    }

    public static int[] getPeriodeMinMax(int indexDate, int nbrePeriode) {
        return new int[]{indexDate * 23, 23 * (indexDate + 1)};
    }

    public static List<Date> getDatesBetween(Date start, Date end) {
        List<Date> dates = new ArrayList<>();
        Date date = new Date(start.getTime());
        while (date.before(end)) {
            date.setTime(date.getTime() + (24 * 3600000));
            dates.add(new Date(date.getTime()));
        }
        return dates;
    }

    public static int getNumberOfPeriod(Date start, Date end) {
        return getDatesBetween(start, end).size() * 24;
    }

    public static java.sql.Date getSqlDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }

    public static String strDateDefaultPattern(Date date) {
        return formateDate(date, "dd/MM/yyyy");
    }

    public static String formateDate(Date date, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static Date addHourOfDayCalendarToDate(int heures, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, heures);
        return cal.getTime();
    }

    public static int hoursDifference(Date date1, Date date2) {
        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        return (int) (date1.getTime() - date2.getTime()) / MILLI_TO_HOUR;
    }

//    public static Date swtDateTimeToDate(DateTime dateTime) {
//        return new Date(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), dateTime.getHours(), dateTime.getMinutes(), dateTime.getSeconds());
//    }
//    public static void swtDateToDateTime(Date date, DateTime dateTime) {
//        dateTime.setDay(date.getDay());
//        dateTime.setYear(date.getYear());
//        dateTime.setMonth(date.getMonth());
//        dateTime.setHours(date.getHours());
//        dateTime.setMinutes(date.getMinutes());
//        dateTime.setSeconds(date.getSeconds());
//    }
}
