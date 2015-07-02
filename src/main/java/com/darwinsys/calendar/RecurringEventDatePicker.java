package com.darwinsys.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Pick the date that a recurring event will fall on, e.g.,
 * gtabug.ca currently meets on the Third Wednesday of each month:
 * <pre>
 * RecurringEventDatePicker mp = new RecurringEventDatePicker(3, DayOfWeek.WEDNESDAY);
 * LocalDate nextMeeting = mp.getNextMeeting(0); // next
 * </pre>
 * In a JSP you might use:
 * <pre>
 * Next three meetings: <%
 * RecurringEventDatePicker dp = new RecurringEventDatePicker(3, DayOfWeek.WEDNESDAY);
 * DateTimeFormatter dfm = 
 * 			DateTimeFormatter.ofPattern("MMMM d, yyyy");
 * out.println("*" + dp.getEventDate(0).format(dfm));
 * out.println("*" + dp.getEventDate(1).format(dfm));
 * out.println("*" + dp.getEventDate(2).format(dfm)); %>
 * </pre>
 * @author Original code by Derek Marcotte.
 * @author Improvements and JUnit tests by Ian Darwin
 * @author Rewritten for Java 8 date/time API by Ian Darwin
 */
public class RecurringEventDatePicker {
	
	private DayOfWeek dayOfWeek;
	
	private int weekOfMonth = 3;
	
	private LocalTime hourOfDay;
	
	// non-private only for testing!
	LocalDate now = LocalDate.now();

	/**
	 * Construct a RecurringEventDatePicker for e.g, the 
	 * third Tuesday of every month:
	 * new RecurringEventDatePicker(3, DayOfWeek.TUESDAY);
	 * @param weekOfMonth The recurrence week of the month
	 * @param dayOfWeek The recurrence day of the week
	 */
	public RecurringEventDatePicker(int weekOfMonth, DayOfWeek dayOfWeek, LocalTime hourOfDay) {
		super();
		if (weekOfMonth < 1 || weekOfMonth > 5) {
			throw new IllegalArgumentException("weekOfMonth must be in 1..5");
		}
		this.weekOfMonth = weekOfMonth;
		this.dayOfWeek = dayOfWeek;
		this.hourOfDay = hourOfDay;
	}
	
	public RecurringEventDatePicker(int weekOfMonth, DayOfWeek dayOfWeek) {
		this(weekOfMonth, dayOfWeek, LocalTime.of(12, 00));
	}

	/**
	 * Get a LEGACY java.util.Calendar representing
	 * the event date for the 'n'th next meeting
	 * @param meetingsAway the number of meetings into the future that
	 * you're interested in.
	 * @return The given date
	 */
	@Deprecated
	public Calendar getEventDate(int meetingsAway) {
		
		LocalDate when = getEventLocalDate(meetingsAway);
		LocalDateTime zhen = LocalDateTime.of(when, hourOfDay);
		ZonedDateTime zdt = ZonedDateTime.of(zhen, ZoneId.systemDefault());
		
		return GregorianCalendar.from(zdt);
	}
	
	/**
	 * Get a java.time.LocalDate representing
	 * the event date for the 'n'th next meeting
	 * for this RecurringEventDatePicker
	 * @param meetingsAway the number of meetings into the future that
	 * you're interested in.
	 * @return The given date
	 */
	public LocalDate getEventLocalDate(int meetingsAway) {

		LocalDate thisMeeting = meetingForMonth(now);
		// has the meeting already happened this month
		if (thisMeeting.isBefore(now)) {
			// start from next month
			meetingsAway++;
		}
		if (meetingsAway > 0) {
			thisMeeting = meetingForMonth(
				thisMeeting.plusMonths(meetingsAway));
		}
		
		return thisMeeting;
	}
	
	public LocalDateTime getEventLocalDateTime(int meetingsAway) {
		return LocalDateTime.of(getEventLocalDate(meetingsAway), hourOfDay);
	}
	
	/**
	 * Get the meeting in the month of the given LocalDate
	 * for this RecurringDatePicker
	 * @param dateWithMonth A date in the same month as the meeting
	 * @return The date of the meeting
	 */
	public LocalDate meetingForMonth(LocalDate dateWithMonth) {
		return
			dateWithMonth.with(TemporalAdjusters.firstInMonth(dayOfWeek))
				.plusWeeks(Math.max(0, weekOfMonth - 1));
	}
}
