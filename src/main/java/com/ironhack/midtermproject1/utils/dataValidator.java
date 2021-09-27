package com.ironhack.midtermproject1.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class dataValidator {

    public static LocalDateTime returnCurrentDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return now;
    }
}
