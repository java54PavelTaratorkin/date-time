package telran.time.application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

import telran.util.Arrays;

record MonthYearWeekDay(int month, int year, DayOfWeek firstDayOfWeek) {
	
}
public class PrintCalendar {

	private static final int TITLE_OFFSET = 5;
	private static final int COLUMN_WIDTH = 4;
	private static DayOfWeek[] weekDays = DayOfWeek.values();
	public static void main(String[] args)  {

		try {
			MonthYearWeekDay monthYearWeekDay = getMonthYearWeekDay(args);
			if (monthYearWeekDay.firstDayOfWeek() != DayOfWeek.MONDAY){
				getWeekDays(monthYearWeekDay);
			}
			printCalendar(monthYearWeekDay);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static void getWeekDays(MonthYearWeekDay monthYearWeekDay) {
		weekDays[0] = monthYearWeekDay.firstDayOfWeek();
		int dayValue = monthYearWeekDay.firstDayOfWeek().getValue();
		for (int index = 1; index < weekDays.length; index++) {
			if (dayValue == 7) {
				dayValue = 1;
			}
			weekDays[dayValue] = DayOfWeek.of(dayValue++);
		}
	}

	private static  MonthYearWeekDay getMonthYearWeekDay(String[] args) throws Exception{
		int monthNumber = getMonth(args);
		int year = getYear(args);
		DayOfWeek firstDay = getFirstDay(args);
		return new MonthYearWeekDay(monthNumber, year, firstDay);
	}

	private static DayOfWeek getFirstDay(String[] args) throws Exception {
		DayOfWeek firstDay = args.length < 3 ? getDefaultFirstDay() :  getFirstDay(args[2]);
		return firstDay;
	}

	private static DayOfWeek getFirstDay(String weekDayStr) throws Exception {			
		if (weekDayStr.length() <= 1) {
			throw new Exception("Day of the week must be minimum two letters.");
		}
		return findFirstDay(weekDayStr);
	}

	private static DayOfWeek findFirstDay(String weekDayStr) throws Exception {
	    DayOfWeek[] days = DayOfWeek.values();
	    DayOfWeek firstDay = null;
	    int i = 0;
	    while (i < days.length && firstDay == null) {
	        if (days[i].name().startsWith(weekDayStr.toUpperCase())) {
	            firstDay = days[i];
	        }
	        i++;
	    }
	    if (firstDay == null) {
	        throw new Exception("Incorrect day of the week was entered. "
	                + "Must contain full or short name (minimum 2 letters) of the day of the week"
	                + "(e.g. Sunday, Monday, TUESDAY, su, Tue, etc.).");
	    }
	    return firstDay;		
	}

	private static DayOfWeek getDefaultFirstDay() {
		
		return DayOfWeek.MONDAY;
	}

	private static int getYear(String[] args) throws Exception {
		int year = args.length < 2 ? getCurrentYear() : getYear(args[1]);
		return year;
	}

	private static int getYear(String yearStr) throws Exception {
		try {
			int res = Integer.parseInt(yearStr);
			return res;
		} catch (NumberFormatException e) {
			throw new Exception("year must be an integer number");
		}
		
	}

	private static int getCurrentYear() {
		
		return LocalDate.now().getYear();
	}

	private static int getMonth(String[] args) throws Exception{
		int month = args.length == 0 ? getCurrentMonth() : getMonthNumber(args[0]);
		return month;
	}

	private static int getMonthNumber(String monthStr)throws Exception {
		try {
			int result = Integer.parseInt(monthStr);
			if (result < 1) {
				throw new Exception("Month cannot be less than 1");
			}
			if(result > 12) {
				throw new Exception("Month cannot be greater than 12");
			}
			return result;
		} catch (NumberFormatException e) {
			throw new Exception("Month must be a number");
		}
	}

	private static int getCurrentMonth() {
		
		return LocalDate.now().get(ChronoField.MONTH_OF_YEAR);
	}

	private static void printCalendar(MonthYearWeekDay monthYearWeekDay) {
		printTitle(monthYearWeekDay);
		printWeekDays();
		printDays(monthYearWeekDay);		
	}

	private static void printDays(MonthYearWeekDay monthYearWeekDay) {
		int nDays = getDaysInMonth(monthYearWeekDay);
		int currentWeekDay = getFirstDayOfMonth(monthYearWeekDay);		
		int currentWeekDayPos = getWeekDayPosArraysIndexOf(currentWeekDay);
		int firstOffset = getFirstOffset(currentWeekDayPos);
		
		System.out.printf("%s", " ".repeat(firstOffset));
		for(int day = 1; day <= nDays; day++) {
			System.out.printf("%" + COLUMN_WIDTH +"d", day);
			currentWeekDayPos++;
			if(currentWeekDayPos == weekDays.length) {
				currentWeekDayPos = 0;
				System.out.println();
			}			
		}		
	}

	//this is regular method without usage external method Arrays.indexOf()
	private static int getWeekDayPos(int currentWeekDay) {
		int weekDayIndex = weekDays[0].getValue() - currentWeekDay;	
		return weekDayIndex <= 0 ? Math.abs(weekDayIndex) : weekDays.length - weekDayIndex;		
	}
	
	// this is same method as above getWeekDayPos(), but uses external Arrays.indexOf()
	private static int getWeekDayPosArraysIndexOf(int currentWeekDay) {

		return Arrays.indexOf(weekDays, DayOfWeek.of(currentWeekDay));
	}
	
	private static int getFirstOffset(int currentWeekDayPos) {
		return COLUMN_WIDTH * currentWeekDayPos;
	}

	private static int getFirstDayOfMonth(MonthYearWeekDay monthYearWeekDay) {
		LocalDate ld = LocalDate.of(monthYearWeekDay.year(), monthYearWeekDay.month(),
				1);
		return ld.get(ChronoField.DAY_OF_WEEK);
	}

	private static int getDaysInMonth(MonthYearWeekDay monthYearWeekDay) {
		YearMonth ym = YearMonth.of(monthYearWeekDay.year(), monthYearWeekDay.month());
		return ym.lengthOfMonth();
	}

	private static void printWeekDays() {
		System.out.printf("%s", " ".repeat(1));
		for(DayOfWeek weekday: weekDays) {
			System.out.printf("%" + COLUMN_WIDTH +"s",weekday.getDisplayName(TextStyle.SHORT,
					Locale.forLanguageTag("en")));
			
		}
		System.out.println();
		
	}

	private static void printTitle(MonthYearWeekDay monthYearWeekDay) {
		String monthName = Month.of(monthYearWeekDay.month())
				.getDisplayName(TextStyle.FULL, Locale.getDefault());
		System.out.printf("%s%s %d\n"," ".repeat(TITLE_OFFSET), monthName, monthYearWeekDay.year());	
	}

}
