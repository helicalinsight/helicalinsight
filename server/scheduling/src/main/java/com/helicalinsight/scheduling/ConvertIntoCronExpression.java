package com.helicalinsight.scheduling;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Prashansa Kumari
 * @author Somen
 * @version 1.0
 * class ConvertIntoCronExpression implementing {@link ICronExpresssionOperation} interface.
 * Cron helps the programmer to execute a job periodically on the server.
 * Cron Expression is a utility that schedules a task.
 * It allows user to schedule a task periodically at specified time, date, month, weak, and year. 
 * it is used to automate the process.
 * This class provides methods to convert various scheduling parameters into a Cron Expression.
 */

public class ConvertIntoCronExpression implements ICronExpresssionOperation {
    private static final Logger logger = LoggerFactory.getLogger(ConvertIntoCronExpression.class);

    /**
     * convertDateIntoCronExpression(JsonObject jsonObject)
     * Converts a scheduling parameters into a Cron Expression.
     * @param jsonObject              contains schedule related parameters
     * @return generated cronExpression     
     */
    @Override
    public String convertDateIntoCronExpression(JsonObject jsonObject) {
        String cronExpression = "";
        String endDate = "";
        String daysOfWeek = "";
        String scheduledTime = "";
        String endsRadio = "";
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        if (jsonObject != null) {
            String startDate = jsonObject.get("StartDate").getAsString();
            String frequency = jsonObject.get("Frequency").getAsString();
            int repeatsEvery = jsonObject.get("RepeatsEvery").getAsInt();
            logger.debug("Contain Schedule Time:  " + jsonObject.has("ScheduledTime"));
            if (jsonObject.has("ScheduledTime")) {
                scheduledTime = jsonObject.get("ScheduledTime").getAsString();
                if (StringUtils.isNotEmpty(scheduledTime)) {
                    String[] tokens = scheduledTime.split(":");
                    hours = Integer.parseInt(tokens[0]);
                    minutes = Integer.parseInt(tokens[1]);
                    seconds = Integer.parseInt(tokens[2]);
                }
            }
            if (jsonObject.has("endsRadio")) {
                endsRadio = jsonObject.get("endsRadio").getAsString();
                if (endsRadio.equalsIgnoreCase("on")) {
                    endDate = jsonObject.get("EndDate").getAsString();
                } else if (endsRadio.equalsIgnoreCase("never")) {
                    endDate = "never";
                } else if (endsRadio.equalsIgnoreCase("after")) {
                    endDate = jsonObject.get("EndAfterExecutions").getAsString();
                }
            }
            if (StringUtils.isNotEmpty(frequency)) {
                if (frequency.equalsIgnoreCase("daily")) {
                    cronExpression = cronExpressionForDailyFrequency(repeatsEvery, startDate, endDate, hours, minutes,
                            seconds, endsRadio);
                } else if (frequency.equalsIgnoreCase("Monthly")) {
                    logger.debug("MONTHLY BLOCK");
                    String repeatBy = jsonObject.get("RepeatBy").getAsString();
                    cronExpression = cronExpressionForMonthlyFrequency(repeatsEvery, startDate, endDate,
                            repeatBy.trim(), hours, minutes, seconds, endsRadio);
                } else if (frequency.equalsIgnoreCase("yearly")) {
                    logger.debug("YEARLY BLOCK..");
                    cronExpression = cronExpressionForYearlyFrequency(repeatsEvery, startDate, endDate, hours,
                            minutes, seconds, endsRadio);
                } else if (frequency.equalsIgnoreCase("Weekly")) {
                    logger.debug("WEEKLY BLOCK..");
                     JsonElement jsonElement = jsonObject.get("DaysofWeek");
                     daysOfWeek = (jsonElement.isJsonNull()) ? null : jsonElement.getAsString();
                    cronExpression = cronExpressionForWeeklyFrequency(repeatsEvery, startDate, endDate, daysOfWeek,
                            hours, minutes, seconds, endsRadio);
                } else {
                    logger.debug("Frequency must have one of the following value Daily,Monthly,Weekly,Yearly");
                }
            }
            logger.debug("cronExpression before validate: " + cronExpression);
            if (CronExpression.isValidExpression(cronExpression)) {
                logger.debug("Validate cron expression:");
                logger.info("generatedCronExpression :" + cronExpression);
                return cronExpression;
            } else {
                logger.error("Cron Expression is invalid");
            }
        }
        logger.info("generatedCronExpression :" + cronExpression);
        return cronExpression;
    }
    /**
     * cronExpressionForDailyFrequency(int RepeatsEvery, String StartDate, String EndDate, int hours,
                                                  int minutes, int seconds, String endsRadio)
     *  Generates a Cron Expression for daily frequency scheduling.                                             
     * @param RepeatsEvery			no of intervals to repeat the task	
     * @param StartDate				start date for scheduling
     * @param EndDate				end date for scheduling
     * @param hours					hour of the day to execute the task
     * @param minutes				minute of the hour to execute the task
     * @param seconds				second of the minute
     * @param endsRadio				it povides end condition such as never, on(end date) and after(certain intervals)
     * @return generated Cron Expression for daily frequency.
     */
    public String cronExpressionForDailyFrequency(int RepeatsEvery, String StartDate, String EndDate, int hours,
                                                  int minutes, int seconds, String endsRadio) {
        logger.debug("Daily Frequency");
        String cronExpression = "";
        Calendar startDate = new GregorianCalendar();
        Calendar endDate = new GregorianCalendar();
        int startYear = 0;
        int endYear = 0;
        int startDayOfTheMonth = 0;
        int startMonth = 0;
        int endMonth = 0;
        int endDayOfTheMonth = 0;

        if (RepeatsEvery > 0 && StartDate != null && EndDate != null && StartDate != "" &&
                EndDate != "") {
            logger.debug("ExpressionForDailyFrequency being called");
            startDate = convertStringIntoDate(StartDate);
            startYear = startDate.get(Calendar.YEAR);
            startMonth = startDate.get(Calendar.MONTH) + 1;

            //startDayOfTheMonth = startDate.get(Calendar.DAY_OF_MONTH);
            logger.debug("startYear:  " + startYear);

            if (!EndDate.equalsIgnoreCase("never") && endsRadio.equalsIgnoreCase("on")) {
                logger.debug("END DATE");
                endDate = convertStringIntoDate(EndDate);
                endYear = endDate.get(Calendar.YEAR);
                endMonth = endDate.get(Calendar.MONTH) + 1;
                logger.debug("endYear:  " + endYear);
                String sequenceMonth = (startMonth == endMonth) ? startMonth + "" : startMonth + "-" + endMonth;
                String sequenceYear = (startYear == endYear) ? startYear + "" : startYear + "-" + endYear;
                cronExpression = seconds + " " + minutes + " " + hours + " " + "*/" + RepeatsEvery + " " + sequenceMonth + " ? " + sequenceYear;
            } else {
                logger.debug("inside else part..");
                cronExpression = seconds + " " + minutes + " " + hours + " " + "*/" +
                        RepeatsEvery + " " + startMonth + "/1" + " ? " + startYear + "/1";
            }

        } else {
            logger.error("StartDate, EndDate any of them is null or blank or RepeatsEvery os Zero or less than zero");
        }
        logger.debug("cronExpression: " + cronExpression);
        return cronExpression;
    }

    /**
     * cronExpressionForMonthlyFrequency(int RepeatsEvery, String StartDate, String EndDate,
                                String repeatBy, int hours, int minutes, int seconds, String endsRadio) 
     * 
     * @param RepeatsEvery					no of intervals to repeat the task if like 2 or 3 times in month 
     * @param StartDate						start date for scheduling
     * @param EndDate						start date for scheduling
     * @param hours							hour of the day to execute the task
     * @param minutes						minute of the hour to execute the task
     * @param seconds						second of the minute
     * @param endsRadio						it povides end condition such as never, on(end date) and after(certain intervals)
     * @return generated Cron Expression for monthly frequency.
     */
    public String cronExpressionForMonthlyFrequency(int RepeatsEvery, String StartDate, String EndDate,
                                                    String repeatBy, int hours, int minutes, int seconds, String endsRadio) {
        logger.debug("contains" + repeatBy.contains("]"));
        String cronExpression = "";
        Calendar startDate = new GregorianCalendar();
        Calendar endDate = new GregorianCalendar();
        int startYear = 0;
        int endYear = 0;
        int startDayOfTheMonth = 0;
        int dayOfWeek = 0;
        int WeekOfMonth = 0;
        int endMonth = 0;
        int startMonth = 0;
        logger.debug("repeatBy" + repeatBy);
        if (RepeatsEvery > 0 && StartDate != null && EndDate != null && repeatBy != null &&
                StartDate != "" && !StartDate.trim().isEmpty() && EndDate != "" && !EndDate.trim().isEmpty() &&
                repeatBy != "" &&
                !repeatBy.trim().isEmpty() && !repeatBy.contains("]")) {
            logger.debug("Expression ForMonthlyFrequency");
            startDate = convertStringIntoDate(StartDate);
            startYear = startDate.get(Calendar.YEAR);
            startMonth = startDate.get(Calendar.MONTH) + 1;
            startDayOfTheMonth = startDate.get(Calendar.DAY_OF_MONTH);
            dayOfWeek = startDate.get(Calendar.DAY_OF_WEEK);
            WeekOfMonth = startDate.get(Calendar.DAY_OF_WEEK_IN_MONTH);
            logger.debug("startDayOfTheMonth:" + startDayOfTheMonth);

            if (repeatBy.equalsIgnoreCase("dayOfTheMonth")) {
                if (!EndDate.equalsIgnoreCase("never") && endsRadio.equalsIgnoreCase("On")) {
                    logger.debug("Day of Month startYear" + startYear + "endYear:" +
                            endYear);
                    endDate = convertStringIntoDate(EndDate);
                    endYear = endDate.get(Calendar.YEAR);
                    endMonth = endDate.get(Calendar.MONTH) + 1;
                    logger.debug("END DATE");
                    //String sequenceDay = (startDayOfTheMonth == endDayOfTheMonth) ? startDayOfTheMonth + "" : startDayOfTheMonth + "-" + endDayOfTheMonth;
                    String sequenceMonth = (startMonth == endMonth) ? startMonth + "" : startMonth + "-" + endMonth;
                    String sequenceYear = (startYear == endYear) ? startYear + "" : startYear + "-" + endYear;
                    cronExpression = seconds + " " + minutes + " " + hours + " " + startDayOfTheMonth +
                            " " + sequenceMonth + "/" + RepeatsEvery + " ? " + sequenceYear;
                } else {
                    cronExpression = seconds + " " + minutes + " " + hours + " " + startDayOfTheMonth +
                            " " + "*" + "/" + RepeatsEvery + " ? " + startYear + "/1";
                }

            } else if ((repeatBy.equalsIgnoreCase("dayOfTheWeek"))) {
                if (!EndDate.equalsIgnoreCase("never") && endsRadio.equalsIgnoreCase("On")) {
                    endDate = convertStringIntoDate(EndDate);
                    endYear = endDate.get(Calendar.YEAR);
                    endMonth = endDate.get(Calendar.MONTH) + 1;
                    String sequenceMonth = (startMonth == endMonth) ? startMonth + "" : startMonth + "-" + endMonth;
                    String sequenceYear = (startYear == endYear) ? startYear + "" : startYear + "-" + endYear;
                    cronExpression = seconds + " " + minutes + " " + hours + " ? " + sequenceMonth + "/" +
                            RepeatsEvery + " " + dayOfWeek + "#" + WeekOfMonth + " " + sequenceYear;
                } else {
                    cronExpression = seconds + " " + minutes + " " + hours + " ? " +
                            "*" + "/" + RepeatsEvery + " " + dayOfWeek + "#" + WeekOfMonth + " " + startYear + "/1";
                }
            }
        } else {
            logger.error("StartDate, EndDate any of them is null or blank " + "or RepeatsEvery " +
                    "os Zero or less than zero");
        }
        logger.debug("cronExpression: " + cronExpression);
        return cronExpression;
    }

    /**
     * cronExpressionForYearlyFrequency(int repeatsEvery, String startDate, String endDate, int hours,
                                                   int minutes, int seconds, String endsRadio)
     * @param RepeatsEvery					no of intervals to repeat the task if like 2 or 3 times in year 
     * @param StartDate						start date for scheduling
     * @param EndDate						start date for scheduling
     * @param hours							hour of the day to execute the task
     * @param minutes						minute of the hour to execute the task
     * @param seconds						second of the minute
     * @param endsRadio						it povides end condition such as never, on(end date) and after(certain intervals)
     * @return generated Cron Expression for yearly interval.
     */
    public String cronExpressionForYearlyFrequency(int repeatsEvery, String startDate, String endDate, int hours,
                                                   int minutes, int seconds, String endsRadio) {
        logger.debug("Yearly block");
        String cronExpression = "";
        Calendar startDateCalander = new GregorianCalendar();
        Calendar endDateCalander = new GregorianCalendar();
        int startYear = 0;
        int endYear = 0;
        int startDatee = 0;
        int startMonth = 0;

        if (repeatsEvery > 0 && startDate != null && endDate != null && startDate != "" &&
                endDate != "") {
            startDateCalander = convertStringIntoDate(startDate);
            startYear = startDateCalander.get(Calendar.YEAR);
            startMonth = startDateCalander.get(Calendar.MONTH) + 1;
            startDatee = startDateCalander.get(Calendar.DAY_OF_MONTH);
            if (!endDate.equalsIgnoreCase("never") && endsRadio.equalsIgnoreCase("On")) {
                endDateCalander = convertStringIntoDate(endDate);
                endYear = endDateCalander.get(Calendar.YEAR);
                String sequenceYear = (startYear == endYear) ? startYear + "" : startYear + "-" + endYear;
                cronExpression = seconds + " " + minutes + " " + hours + " " + startDatee + "" +
                        " " + startMonth + " ? " + " " + sequenceYear;
            } else {
                cronExpression = seconds + " " + minutes + " " + hours + " " + startDatee + " " +
                        startMonth + " ? " + " " + "*" + "/" + repeatsEvery;
            }

        } else {
            logger.error("StartDate, EndDate any of them is null or blank " + "or RepeatsEvery " +
                    "os Zero or less than zero");
        }
        return cronExpression;
    }

    /**
     * cronExpressionForWeeklyFrequency(int repeatsEvery, String startDate, String endDate,
                            String daysOfWeek, int hours, int minutes, int seconds, String endsRadio) 
     
    * @param RepeatsEvery					no of intervals to repeat the task if like 2 or 3 times in week 
     * @param StartDate						start date for scheduling
     * @param EndDate						start date for scheduling
     * @param hours							hour of the day to execute the task
     * @param minutes						minute of the hour to execute the task
     * @param seconds						second of the minute
     * @param endsRadio						it povides end condition such as never, on(end date) and after(certain intervals)
     * @return generated Cron Expression for weekly interval.
     */
    public String cronExpressionForWeeklyFrequency(int repeatsEvery, String startDate, String endDate,
                                                   String daysOfWeek, int hours, int minutes, int seconds, String endsRadio) {
        String cronExpression = "";
        String dayOfWeek = "";
        if (repeatsEvery > 0 && startDate != null && endDate != null && daysOfWeek != null &&
                !(startDate.isEmpty() && endDate.isEmpty() && daysOfWeek.isEmpty())) {
            logger.debug("WEEKLY BLOCK");
            logger.debug("daysOfWeek:  " + daysOfWeek);
            Calendar startDateCalendar = new GregorianCalendar();
            Calendar endDateCalendar = new GregorianCalendar();
            int startYear = 0;
            int endYear = 0;
            int startMonth = 0;
            int endMonth = 0;
            startDateCalendar = convertStringIntoDate(startDate);
            startYear = startDateCalendar.get(Calendar.YEAR);
            startMonth = startDateCalendar.get(Calendar.MONTH) + 1;

            //JSONArray arrayList = new JSONArray().fromObject(daysOfWeek);
            JsonArray arrayList = new Gson().fromJson(daysOfWeek, JsonArray.class);
            
            logger.debug("arrayList" + arrayList.size());
            String weekName = "";
            for (int weekCount = 0; weekCount < arrayList.size(); weekCount++) {
                String weekDay = "";


                weekDay = arrayList.get(weekCount).getAsString();


                logger.debug("weekDay: " + weekDay);
                String weekDayUpper = weekDay.toUpperCase();
                weekName = weekDayUpper.substring(0, 3);

                logger.debug("weekName:  " + weekName);
                dayOfWeek = dayOfWeek + "," + weekName;
                logger.debug("DayOfWeek:  " + dayOfWeek);
            }
            logger.debug("DayOfWeek: " + dayOfWeek);
            if (dayOfWeek.length() > 0) {
                dayOfWeek = dayOfWeek.substring(1, dayOfWeek.length());
            } else {
                dayOfWeek = "1";
            }
            if (!endDate.equalsIgnoreCase("never") && endsRadio.equalsIgnoreCase("On")) {
                endDateCalendar = convertStringIntoDate(endDate);
                endYear = endDateCalendar.get(Calendar.YEAR);
                endMonth = endDateCalendar.get(Calendar.MONTH) + 1;
                String sequenceMonth = (startMonth == endMonth) ? startMonth + "" : startMonth + "-" + endMonth;
                String sequenceYear = (startYear == endYear) ? startYear + "" : startYear + "-" + endYear;
                cronExpression = seconds + " " + minutes + " " + hours + " " + "? " + sequenceMonth + " " + dayOfWeek + " " + sequenceYear;
                //0 40 13 ?  */1 ?  2020/1
               /* cronExpression = seconds + " " + minutes + " " + hours + " " + "? " + "*//*1" + " " + DayOfWeek + "/" +
                        repeatsEvery + " " + startYear + "-" + endYear;*/

            } else {
                cronExpression = seconds + " " + minutes + " " + hours + " " + "? " + startMonth + "/1" + " " + dayOfWeek + "/" +
                        repeatsEvery + " " + startYear + "/1";

            }
            if (dayOfWeek.equals("1")) {
                cronExpression = seconds + " " + minutes + " " + hours + " " + "*" + "/" +
                        (Integer.valueOf(repeatsEvery) * 7) + " " + startMonth + "/1" + " " + "?" + " " + startYear + "/1";
            }
        } else {
            logger.error("StartDate, EndDate any of them is null or blank " + "or RepeatsEvery or Zero or less than " +
                    "zero");
        }

        logger.debug("DayOfWeek: " + dayOfWeek);
        return cronExpression;
    }
    /**
     * convertStringIntoDate(String date)
     * @param date           date in string format
     * @return date into calendar format  year-month-date
     */
    public Calendar convertStringIntoDate(String date) {
        Calendar calanderDate = new GregorianCalendar();
        DateFormat formatter = null;
        Date convertdate = null;
        try {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            convertdate = (Date) formatter.parse(date);
        } catch (ParseException ex) {
            logger.error("Exception stack trace is " + ex);
        }
        calanderDate.setTime(convertdate);
        logger.debug("Year: " + calanderDate.get(Calendar.YEAR));
        return calanderDate;
    }
}