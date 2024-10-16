package com.appointmentscheduler.appointmentschedulingapp;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class Utility {

    public void getDate(){
        LocalDateTime now = LocalDateTime.now();

    }

    /**
     * Returns a LocalDateTime object from the given date string.
     *
     * @param dt a datetime string the format 'yyyy-MM-dd HH:mm:ss.S'
     * @return a LocalDateTime object from the given date string.
     */
    public static LocalDateTime timestampToLocalDT(String dt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return LocalDateTime.parse(dt.toString(), dtf);
    }

    public static OffsetDateTime localOffset(LocalDateTime utcLocalDT, ZoneOffset fromOffset, ZoneOffset toOffset) {
        OffsetDateTime timeInUTC = utcLocalDT.atOffset(fromOffset);
        return timeInUTC.withOffsetSameInstant(toOffset);
    }
    public static OffsetDateTime utcToLocalOffsetTimeDate(LocalDateTime utcLocalDateTime) {
        OffsetDateTime timeInUTC = utcLocalDateTime.atOffset(getUTCOffset());
        return timeInUTC.withOffsetSameInstant(getSystemOffset());
    }

    /**
     * Returns offset from UTC of the system's timezone.
     *
     * @return the offset from UTC of the system's timezone.
     */
    public static ZoneOffset getSystemOffset(){
        return ZoneOffset.systemDefault().getRules().getOffset(Instant.now());
    }

    /**
     * Returns offset of the Eastern Time from UTC.
     *
     * @return the offset of the Eastern Time from UTC.
     */
    public static ZoneOffset getETOffset(){
        return ZoneOffset.of("-05:00");
    }

    /**
     * Returns the UTC offset from itself (which is 0:00).
     *
     * @return the UTC offset from itself (which is 0:00).
     */
    public static ZoneOffset getUTCOffset(){
        return ZoneOffset.UTC;
    }

    public static String formatDate(OffsetDateTime dt){
        if (dt != null) {
            return dt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
        }
        return null;
    }


}
