package com.helicalinsight.efw.parameters;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.parameters.RelativeFunctionCatalog.Match;

/**
 * Turns a catalog token such as {@code today} or {@code month-1} into a formatted date.
 * Text that is not a token is returned unchanged.
 */
public final class ParameterEvaluationService {

    private static volatile ParameterEvaluationService shared;

    private static final DateTimeFormatter[] ANCHOR_FORMATS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    };

    private final RelativeFunctionCatalog catalog;

    public ParameterEvaluationService(RelativeFunctionCatalog catalog) {
        this.catalog = catalog == null ? RelativeFunctionCatalog.empty() : catalog;
    }

    public static ParameterEvaluationService shared() {
        ParameterEvaluationService current = shared;
        if (current == null) {
            synchronized (ParameterEvaluationService.class) {
                current = shared;
                if (current == null) {
                    current = new ParameterEvaluationService(RelativeFunctionCatalog.load());
                    shared = current;
                }
            }
        }
        return current;
    }

    public String evaluate(String raw, EvaluationRequest request) {
        if (raw == null || request == null || catalog.isEmpty()) {
            return raw;
        }
        Match match = catalog.match(raw);
        if (match == null) {
            return raw;
        }
        RelativeFunctionDefinition function = match.function();
        if (function.isNow() && request.singleValue()) {
            return raw;
        }
        ZoneId zone = request.zone() == null ? ZoneId.systemDefault() : request.zone();
        ZonedDateTime base = baseInstant(match.anchored(), request.anchorDate(), zone, request.clock());
        ZonedDateTime shifted = shift(base, function, match.modifier());
        ZonedDateTime result = function.isNow() ? base : align(shifted, function.getBoundary(), request.endOfRange());
        try {
            return DateTimeFormatter.ofPattern(request.pattern()).format(result);
        } catch (IllegalArgumentException | DateTimeException ex) {
            throw new EfwServiceException("Cannot format parameter value " + raw + " with pattern " + request.pattern());
        }
    }

    private static ZonedDateTime baseInstant(boolean anchored, String anchorText, ZoneId zone, Clock clock) {
        if (!anchored) {
            return ZonedDateTime.now(clock.withZone(zone));
        }
        if (anchorText == null || anchorText.isBlank()) {
            throw new EfwServiceException("Relative parameter uses an anchor but no anchor date was provided");
        }
        LocalDateTime anchor = parseAnchor(anchorText.trim());
        return anchor.atZone(zone);
    }

    private static LocalDateTime parseAnchor(String anchorText) {
        for (DateTimeFormatter formatter : ANCHOR_FORMATS) {
            try {
                return LocalDateTime.parse(anchorText, formatter);
            } catch (DateTimeParseException ignored) {
                try {
                    return LocalDate.parse(anchorText, formatter).atStartOfDay();
                } catch (DateTimeParseException ignoredAgain) {
                    // try the next format
                }
            }
        }
        throw new EfwServiceException("Anchor date " + anchorText + " is not a supported date");
    }

    private static ZonedDateTime shift(ZonedDateTime base, RelativeFunctionDefinition function, String modifier) {
        if ("NOW".equals(function.getBoundary()) || function.getUnit() == null) {
            return base;
        }
        long amount = function.getOffset();
        int sign = "LAST".equals(function.getDirection()) ? -1 : 1;
        if (modifier != null && function.isAcceptsModifier()) {
            sign = modifier.charAt(0) == '-' ? -1 : 1;
            amount = Long.parseLong(modifier.substring(1));
        }
        amount = sign * amount * function.getMultiplier();
        return base.plus(amount, chronoUnit(function.getUnit()));
    }

    private static ZonedDateTime align(ZonedDateTime value, String boundary, boolean end) {
        ZoneId zone = value.getZone();
        LocalDate date = value.toLocalDate();
        return switch (boundary) {
            case "DAY" -> at(date, end, zone);
            case "WEEK" -> at(date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(end ? 6 : 0), end, zone);
            case "MONTH" -> end
                    ? at(date.with(TemporalAdjusters.lastDayOfMonth()), true, zone)
                    : at(date.withDayOfMonth(1), false, zone);
            case "QUARTER" -> {
                int startMonth = ((date.getMonthValue() - 1) / 3) * 3 + 1;
                LocalDate start = LocalDate.of(date.getYear(), startMonth, 1);
                yield end ? at(start.plusMonths(3).minusDays(1), true, zone) : at(start, false, zone);
            }
            case "YEAR" -> end
                    ? at(date.with(TemporalAdjusters.lastDayOfYear()), true, zone)
                    : at(date.withDayOfYear(1), false, zone);
            case "HOUR" -> end
                    ? value.withMinute(59).withSecond(59).withNano(0)
                    : value.truncatedTo(ChronoUnit.HOURS);
            case "MINUTE" -> end
                    ? value.withSecond(59).withNano(0)
                    : value.truncatedTo(ChronoUnit.MINUTES);
            case "SECOND" -> value.truncatedTo(ChronoUnit.SECONDS);
            case "NOW" -> value;
            default -> value;
        };
    }

    private static ZonedDateTime at(LocalDate date, boolean end, ZoneId zone) {
        LocalTime time = end ? LocalTime.of(23, 59, 59) : LocalTime.MIDNIGHT;
        return ZonedDateTime.of(date, time, zone);
    }

    private static ChronoUnit chronoUnit(String unit) {
        return switch (unit) {
            case "WEEKS" -> ChronoUnit.WEEKS;
            case "MONTHS" -> ChronoUnit.MONTHS;
            case "YEARS" -> ChronoUnit.YEARS;
            case "HOURS" -> ChronoUnit.HOURS;
            case "MINUTES" -> ChronoUnit.MINUTES;
            case "SECONDS" -> ChronoUnit.SECONDS;
            default -> ChronoUnit.DAYS;
        };
    }
}
