package com.easyvisa.quartz;

import org.quartz.spi.InstanceIdGenerator;

import java.util.Calendar;
import java.util.UUID;

public class EvQuartzInstanceIdGenerator implements InstanceIdGenerator {

    @Override
    public String generateInstanceId() {
        return "EasyVisa:" + UUID.randomUUID().toString() + ":" + Calendar.getInstance().getTimeInMillis();
    }

}
