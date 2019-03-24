package com.thepracticaldeveloper.rabbitmqconfig;

import org.springframework.boot.actuate.health.Health;

public interface RabbitHealthMonitor {

    public Health checkHealth();
}
