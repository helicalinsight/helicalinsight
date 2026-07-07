package com.helicalinsight.efw.controller;

import com.helicalinsight.efw.BackgroundService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


/**
 * @author Somen
 *         Created by helical021 on 23/4/2020.
 */
@Component
public class HelicalInsightAppInitStopImpl implements ApplicationListener<ContextRefreshedEvent> {


    public static int counter = 0;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (counter == 0) {
            BackgroundService.startAllService();
            counter++;
        }
    }


}