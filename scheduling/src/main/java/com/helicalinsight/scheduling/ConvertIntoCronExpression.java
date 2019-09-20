/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.scheduling;

import net.sf.json.JSONObject;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * This class is responsible to convert String into cron expression
 * </p>
 *
 * @author Prashansa
 */

public class ConvertIntoCronExpression implements ICronExpresssionOperation {
    private static final Logger logger = LoggerFactory.getLogger(ConvertIntoCronExpression.class);

    /**
     * <p>
     * This method is responsible to convert the <code>JSONObject</code> into
     * cron expression.
     * </p>
     * <p>
     * <code>JSONObject</code> must contain StartDate,Frequency,RepeatsEvery.
     * </p>
     * <p>
     * Default schedule time is 00:00:00(hh:mm:ss)
     * </p>
     * <p>
     * This method is depend on three other method
     * </p>
     * <li><ui>cronExpressinForDailyFrequency()</ui></li> <li>
     * <ui>cronExpressinForMonthlyFrequency()</ui></li> <li>
     * <ui>cronExpressionForYearlyFrequency()</ui></li>
     *
     * @return cronexpression
     * @author Prashansa
     */

    @Override

    public String convertDateIntoCronExpression(JSONObject jsonObject) {
        String cronExpression = "";
        String EndDate = "";
        String DaysofWeek = "";
        String ScheduledTime = "";
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        if (jsonObject != null) {
            String StartDate = jsonObject.getString("StartDate");
            String Frequency = jsonObject.getString("Frequency");
            int RepeatsEvery = jsonObject.getInt("RepeatsEvery");
            logger.debug("Contain Schedule Time:  " + jsonObject.containsKey("ScheduledTime"));
            if (jsonObject.containsKey("ScheduledTime")) {
                ScheduledTime = jsonObject.getString("ScheduledTime");
                if (ScheduledTime != null && !ScheduledTime.equals("")) {
                    String[] tokens = ScheduledTime.split(":");
                    hours = Integer.parseInt(tokens[0]);
                    minutes = Integer.parseInt(tokens[1]);
                    seconds = Integer.parseInt(tokens[2]);
                }
            }
            if (jsonObject.containsKey("endsRadio")) {
                logger.debug("endsRadio");
                if (jsonObject.getString("endsRadio").equalsIgnoreCase("on")) {
                    EndDate = jsonObject.getString("EndDate");
                } else if (jsonObject.getString("endsRadio").equalsIgnoreCase("never")) {
                    EndDate = "never";
                } else if (jsonObject.getString("endsRadio").equalsIgnoreCase("after")) {
                    EndDate = jsonObject.getString("EndAfterExecutions");
                }
            }
            if (Frequency != null && !Frequency.isEmpty()) {
                if (Frequency.equalsIgnoreCase("daily")) {
                    cronExpression = cronExpressinForDailyFrequency(RepeatsEvery, StartDate, EndDate, hours, minutes,
                            seconds);
                } else if (Frequency.equalsIgnoreCase("Monthly")) {
                    logger.debug("MONTHLY BLOCK");
                    String repeatBy = jsonObject.getString("RepeatBy");
                    cronExpression = cronExpressinForMonthlyFrequency(RepeatsEvery, StartDate, EndDate,
                            repeatBy.trim(), hours, minutes, seconds);
                } else if (Frequency.equalsIgnoreCase("yearly")) {
                    logger.debug("YEARLY BLOCK..");
                    cronExpression = cronExpressionForYearlyFrequency(RepeatsEvery, StartDate, EndDate, hours,
                            minutes, seconds);
                } else if (Frequency.equalsIgnoreCase("Weekly")) {
                    logger.debug("WEEKLY BLOCK..");
                    DaysofWeek = jsonObject.getString("DaysofWeek");
                    cronExpression = cronExpressionForWeeklyFrequency(RepeatsEvery, StartDate, EndDate, DaysofWeek,
                            hours, minutes, seconds);
                } else {
                    logger.error("Frequency must have one of the following value Daily,Monthly,Weekly,Yearly");
                }
            } else {
                logger.error("Frequency is null.");
            }
            logger.debug("cronExpression before validate: " + cronExpression);
            if (CronExpression.isValidExpression(cronExpression)) {
                logger.debug("Validate cron expression:");
                return cronExpression;
            } else {
                logger.error("Cron Expression is invalid");
            }
        }
        return cronExpression;
    }

    /**
     * This method is responsible to create cron expression for daily frequency
     * for different conditions.
     *
     * @param RepeatsEvery a <code> int </code> containing repetition in between
     * @param StartDate    a <code> String </code> containing start date of execution
     * @param EndDate      a <code> String </code> containing end date of execution
     * @param hours        a <code> int </code> containing hours of execution
     * @param minutes      a <code> int </code> containing minutes of execution
     * @param seconds      a <code> int </code> containing seconds of execution
     */
    public String cronExpressinForDailyFrequency(int RepeatsEvery, String StartDate, String EndDate, int hours,
                                                 int minutes, int seconds) {
        logger.debug("Daily Frequency");
        String cronExpression = "";
        Calendar startDate = new GregorianCalendar();
        Calendar endDate = new GregorianCalendar();
        int startYear = 0;
        int endYear = 0;

        if (RepeatsEvery > 0 && StartDate != null && EndDate != null && StartDate != "" &&
                EndDate != "") {
            logger.debug("ExpressionForDailyFrequency");
            startDate = convertStringIntoDate(StartDate);
            startYear = startDate.get(Calendar.YEAR);
            logger.debug("startYear:  " + startYear);
            if (!EndDate.equalsIgnoreCase("never") && EndDate.equalsIgnoreCase("After")) {
                logger.debug("END DATE");
                endDate = convertStringIntoDate(EndDate);
                endYear = endDate.get(Calendar.YEAR);
                logger.debug("endYear:  " + endYear);
                cronExpression = seconds + " " + minutes + " " + hours + " " + "*/" +
                        RepeatsEvery + " * " + " ? " + startYear + "-" + endYear;
            } else {
                logger.debug("Else part..");
                endYear = 0;
                cronExpression = seconds + " " + minutes + " " + hours + " " + "*/" +
                        RepeatsEvery + " " + "*/1" + " ? " + startYear + "/1";
            }

        } else {
            logger.error("StartDate, EndDate any of them is null or blank or RepeatsEvery os Zero or less than zero");
        }
        logger.debug("cronExpression: " + cronExpression);
        return cronExpression;
    }

    /**
     * This method is responsible to create cron expression for monthly
     * frequency for different conditions.
     *
     * @param RepeatsEvery
     * @param StartDate    a <code> String </code> containing start date of execution
     * @param EndDate      a <code> String </code> containing end date of execution
     * @param hours        a <code> int </code> containing hours of execution
     * @param minutes      a <code> int </code> containing minutes of execution
     * @param seconds      a <code> int </code> containing seconds of execution
     * @author Prashansa
     */
    public String cronExpressinForMonthlyFrequency(int RepeatsEvery, String StartDate, String EndDate,
                                                   String repeatBy, int hours, int minutes, int seconds) {
        logger.debug("contains" + repeatBy.contains("]"));
        String cronExpression = "";
        Calendar startDate = new GregorianCalendar();
        Calendar endDate = new GregorianCalendar();
        int startYear = 0;
        int endYear = 0;
        int startDatee = 0;
        // int startMonth = 0;
        int dayOfWeek = 0;
        int WeekOfMonth = 0;
        logger.debug("repeatBy" + repeatBy);
        if (RepeatsEvery > 0 && StartDate != null && EndDate != null && repeatBy != null &&
                StartDate != "" && !StartDate.trim().isEmpty() && EndDate != "" && !EndDate.trim().isEmpty() &&
                repeatBy != "" &&
                !repeatBy.trim().isEmpty() && !repeatBy.contains("]")) {
            logger.debug("ExpressinForMonthlyFrequency");
            startDate = convertStringIntoDate(StartDate);
            startYear = startDate.get(Calendar.YEAR);
            startDatee = startDate.get(Calendar.DAY_OF_MONTH);
            dayOfWeek = startDate.get(Calendar.DAY_OF_WEEK);
            WeekOfMonth = startDate.get(Calendar.DAY_OF_WEEK_IN_MONTH);
            logger.debug("startDatee:" + startDatee);

            if (repeatBy.equalsIgnoreCase("dayOfTheMonth")) {
                if (!EndDate.equalsIgnoreCase("never") && EndDate.equalsIgnoreCase("After")) {
                    logger.debug("startYear" + startYear + "endYear:" +
                            endYear);
                    endDate = convertStringIntoDate(EndDate);
                    endYear = endDate.get(Calendar.YEAR);
                    logger.debug("END DATE");
                    cronExpression = seconds + " " + minutes + " " + hours + " " + startDatee + "" +
                            " " + "*/" + RepeatsEvery + " ? " + startYear + "-" + endYear;
                } else {
                    cronExpression = seconds + " " + minutes + " " + hours + " " + startDatee + "" +
                            " " + "*/" + RepeatsEvery + " ? " + "*";
                }

            } else if ((repeatBy.equalsIgnoreCase("dayOfTheWeek"))) {
                if (!EndDate.equalsIgnoreCase("never") && EndDate.equalsIgnoreCase("After")) {
                    endDate = convertStringIntoDate(EndDate);
                    endYear = endDate.get(Calendar.YEAR);
                    cronExpression = seconds + " " + minutes + " " + hours + " " + "? " + " */" +
                            RepeatsEvery + " " + dayOfWeek + "#" + WeekOfMonth + " " + startYear + "-" + endYear;
                } else {
                    cronExpression = seconds + " " + minutes + " " + hours + " " + "? " + " " + "" +
                            " */" + RepeatsEvery + " " + dayOfWeek + "#" + WeekOfMonth + " " + "*";
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
     * This method is responsible to create cron expression for yearly frequency
     * for different conditions.
     *
     * @param repeatsEvery An int
     * @param startDate    a <code> String </code> containing start date of execution
     * @param endDate      a <code> String </code> containing end date of execution
     * @param hours        a <code> int </code> containing hours of execution
     * @param minutes      a <code> int </code> containing minutes of execution
     * @param seconds      a <code> int </code> containing seconds of execution
     * @author Prashansa
     */

    public String cronExpressionForYearlyFrequency(int repeatsEvery, String startDate, String endDate, int hours,
                                                   int minutes, int seconds) {
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
            if (!endDate.equalsIgnoreCase("never") && endDate.equalsIgnoreCase("After")) {
                endDateCalander = convertStringIntoDate(endDate);
                if (endYear > startYear + repeatsEvery) {
                    endYear = endDateCalander.get(Calendar.YEAR);
                    cronExpression = seconds + " " + minutes + " " + hours + " " + startDatee + "" +
                            " " + startMonth + " ? " + " " + startYear + "-" + endYear + "/" +
                            repeatsEvery;
                } else {
                    logger.error("End year must be grater than start year repeat count");
                }
            } else {
                cronExpression = seconds + " " + minutes + " " + hours + " " + startDatee + " " +
                        startMonth + " ? " + " " + startYear + "/" + repeatsEvery;
            }

        } else {
            logger.error("StartDate, EndDate any of them is null or blank " + "or RepeatsEvery " +
                    "os Zero or less than zero");
        }
        return cronExpression;
    }

    /**
     * This method is responsible to create cron expression for weekly frequency
     * for different conditions.
     *
     * @param repeatsEvery An int
     * @param startDate    a <code> String </code> containing start date of execution
     * @param endDate      a <code> String </code> containing end date of execution
     * @param hours        a <code> int </code> containing hours of execution
     * @param minutes      a <code> int </code> containing minutes of execution
     * @param seconds      a <code> int </code> containing seconds of execution
     * @author Prashansa
     */

    public String cronExpressionForWeeklyFrequency(int repeatsEvery, String startDate, String endDate,
                                                   String daysOfWeek, int hours, int minutes, int seconds) {
        String cronExpression = "";
        String DayOfWeek = "";
        if (repeatsEvery > 0 && startDate != null && endDate != null && daysOfWeek != null &&
                !(startDate.isEmpty() && endDate.isEmpty() && daysOfWeek.isEmpty())) {
            logger.debug("WEEKLY BLOCK");
            logger.debug("daysOfWeek: test" + daysOfWeek);
            logger.debug("daysOfWeek:  " + daysOfWeek.substring(1, daysOfWeek.length() - 1));
            Calendar startDateCalander = new GregorianCalendar();
            Calendar endDateCalander = new GregorianCalendar();
            int startYear = 0;
            int endYear = 0;
            startDateCalander = convertStringIntoDate(startDate);
            startYear = startDateCalander.get(Calendar.YEAR);


            ArrayList<String> arrayList = new ArrayList<>();

            logger.debug("contains ," + daysOfWeek.contains(","));
            if (daysOfWeek.contains(",")) {
                StringTokenizer stringTokenizer = new StringTokenizer(daysOfWeek.substring(1,
                        daysOfWeek.length() - 1), ",");
                while (stringTokenizer.hasMoreTokens()) {
                    String weekday = stringTokenizer.nextToken();
                    logger.debug("weekday: " + weekday);
                    arrayList.add(weekday);
                }
            } else {
                daysOfWeek = daysOfWeek.substring(1, daysOfWeek.length() - 1);
                arrayList.add(daysOfWeek);
            }
            logger.debug("arrayList" + arrayList.size());
            String weekName = "";
            for (int weekCount = 0; weekCount < arrayList.size(); weekCount++) {
                String weekDay = "";
                for (int count = 0; count < 7; count++) {
                    logger.debug("arrayList.get(weekCount):  " + arrayList.get(weekCount).substring(1,
                            arrayList.get(weekCount).length() - 1));
                    if (weekDay.contains("\"")) {
                        weekDay = arrayList.get(weekCount).substring(1, arrayList.get(weekCount).length() - 1);
                        logger.debug("weekDay:  " + weekDay);

                    } else {
                        weekDay = arrayList.get(weekCount);
                        logger.debug("weekDay: " + weekDay);
                    }
                    if (weekDay.equalsIgnoreCase("monday")) {
                        weekName = "MON";
                    } else if (weekDay.equalsIgnoreCase("tuesday")) {
                        weekName = "TUE";
                    } else if (weekDay.equalsIgnoreCase("wednesday")) {
                        weekName = "WED";
                    } else if (weekDay.equalsIgnoreCase("thursday")) {
                        weekName = "THU";
                    } else if (weekDay.equalsIgnoreCase("friday")) {
                        weekName = "FRI";
                    } else if (weekDay.equalsIgnoreCase("saturday")) {
                        weekName = "SAT";
                    } else if (weekDay.equalsIgnoreCase("sunday")) {
                        weekName = "SUN";
                    }
                    logger.debug("weekName:  " + weekName);
                }
                DayOfWeek = DayOfWeek + "," + weekName;
                logger.debug("DayOfWeek:  " + DayOfWeek);
            }
            logger.debug("DayOfWeek: " + DayOfWeek);
            DayOfWeek = DayOfWeek.substring(1, DayOfWeek.length());
            if (!endDate.equalsIgnoreCase("never") && endDate.equalsIgnoreCase("After")) {
                endDateCalander = convertStringIntoDate(endDate);
                endYear = endDateCalander.get(Calendar.YEAR);
                cronExpression = seconds + " " + minutes + " " + hours + " " + "? " + "*/1" + " " + DayOfWeek + "/" +
                        repeatsEvery + " " + startYear + "-" + endYear;

            } else {
                cronExpression = seconds + " " + minutes + " " + hours + " " + "? " + "*/1" + " " + DayOfWeek + "/" +
                        repeatsEvery + " " + startYear + "/1";
            }
        } else {
            logger.error("StartDate, EndDate any of them is null or blank " + "or RepeatsEvery or Zero or less than " +
                    "zero");
        }
        logger.debug("DayOfWeek: " + DayOfWeek);
        return cronExpression;
    }

    /**
     * <p>
     * This method is responsible to convert String into <code>Calendar</code>
     * </p>
     *
     * @param date
     * @return Calendar
     */
    public Calendar convertStringIntoDate(String date) {
        Calendar calanderDate = new GregorianCalendar();
        DateFormat formatter = null;
        Date convertdate = null;
        try {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            convertdate = (Date) formatter.parse(date);
        } catch (ParseException ex) {
            logger.error("Exception stack trace is ", ex);
        }
        calanderDate.setTime(convertdate);
        logger.debug("Year: " + calanderDate.get(Calendar.YEAR));
        return calanderDate;
    }
}
