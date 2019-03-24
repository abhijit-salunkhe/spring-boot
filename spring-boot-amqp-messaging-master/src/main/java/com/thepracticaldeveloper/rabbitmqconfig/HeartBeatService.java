package com.thepracticaldeveloper.rabbitmqconfig;

import org.springframework.boot.actuate.health.Health;


public class HeartBeatService<T extends  RabbitHealthMonitor> {


    Health checkRabbitMQHealth(T t){

       return  t.checkHealth();
    }

}
