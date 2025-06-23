package com.omnilinx.file_upload_rest_svc.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class RestCallTimeIntervalDispatcher {

    @Value("${rest.call.enabled:true}")
    private boolean enabled;
    @Value("${rest.call.time.interval.begin}")
    private String begin;
    @Value("${rest.call.time.interval.end}")
    private String end;
    @Value("${rest.call.time.zone-id:UTC}")
    private String zoneId;
    @Value("${date.time.format.pattern}")
    private String dateTimeFormat;


    public boolean isWithinAllowedTimeInterval() {
        if (!enabled) return true;

        TimeInterval result = buildTimeInterval();
        return validate(result);
    }

    private boolean validate(TimeInterval timeInterval) {
        log.info("Validating for allowed time window for sending.");
        log.info("Now: {} | Allowed: {} - {}", timeInterval.now(), timeInterval.beginTime(), timeInterval.endTime());

        if (timeInterval.beginTime().isBefore(timeInterval.endTime())) {
            return !timeInterval.now().isBefore(timeInterval.beginTime())
                    && !timeInterval.now().isAfter(timeInterval.endTime());
        } else {
            return !timeInterval.now().isBefore(timeInterval.beginTime())
                    || !timeInterval.now().isAfter(timeInterval.endTime());
        }
    }

    private TimeInterval buildTimeInterval() {
        DateTimeFormatter startFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        LocalTime beginTime = LocalTime.parse(begin, startFormatter);
        LocalTime endTime = LocalTime.parse(end, endFormatter);
        LocalTime now = ZonedDateTime.now(ZoneId.of(zoneId)).toLocalTime();;
        return new TimeInterval(beginTime, endTime, now);
    }

    private record TimeInterval(LocalTime beginTime, LocalTime endTime, LocalTime now) {

    }
}
