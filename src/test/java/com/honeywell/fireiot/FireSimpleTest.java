package com.honeywell.fireiot;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 10:21 AM 3/20/2019
 */

public class FireSimpleTest {

    @Test
    public void test1() {
//        LocalDate localDate = LocalDate.now().minusDays(20);
//        System.out.println(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
//        System.out.println(localDate);
//        System.out.println(localDate.with(TemporalAdjusters.firstDayOfMonth()));
//        System.out.println(localDate.with(TemporalAdjusters.lastDayOfMonth()));


        long start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        System.out.println("XXXXXXXXXXXXXXXX" + start);
        System.out.println("yyyyyyyyyyyyyyyyy" + end);
    }

    @Test
    public void test() {
        Optional<String> ss = Optional.ofNullable(null);
        System.out.println(ss.isPresent());
        System.out.println(ss.get());
    }
}
