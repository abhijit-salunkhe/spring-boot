package com.thepracticaldeveloper.rabbitmqconfig;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.util.Assert;

import java.net.ConnectException;
import java.util.Properties;

public class RabbitQueueHealthMonitor extends AbstractHealthIndicator implements RabbitHealthMonitor {

    private static final Logger log = LoggerFactory.getLogger(RabbitQueueHealthMonitor.class);

    private RabbitAdmin rabbitAdmin;

    private Queue queue;

    public Queue getQueue() {
        return queue;
    }

    public RabbitQueueHealthMonitor(RabbitAdmin rabbitAdmin, Queue queue) {
        super("Rabbit health check failed");
        Assert.notNull(rabbitAdmin, "RabbitTemplate must not be null");
        Assert.notNull(queue, "Queue must not be null");
        this.rabbitAdmin = rabbitAdmin;
        this.queue = queue;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Assert.notNull(rabbitAdmin, "RabbitTemplate must not be null");
        Assert.notNull(queue, "Queue must not be null");
        checkQueueHealth(builder);

    }

    /*private void checkQueueHealth(Health.Builder builder) {
        Properties queueProperties = rabbitAdmin.getQueueProperties(queue.getName());

        if (queueProperties == null) {
            builder.down().withDetail("Mesaage", "Queue: " + this.queue.getName() + " is " + Status.DOWN.getCode());
        } else {
            builder.up().withDetail("Mesaage", "Queue: " + this.queue.getName()  + " is " + Status.UP.getCode());
        }
    }*/

   /* private void checkQueueHealth(Health.Builder builder) {
        Object queue_message_count = rabbitAdmin.getQueueProperties(queue.getName()).get("QUEUE_MESSAGE_COUNT");

        if (queue_message_count == null) {
            builder.down().withDetail("Mesaage", "Queue: " + this.queue.getName() + " is " + Status.DOWN.getCode());
        } else {
            builder.up().withDetail("Mesaage", "Queue: " + this.queue.getName()  + " is " + Status.UP.getCode());
        }
    }*/

    private void checkQueueHealth(Health.Builder builder) {
        AMQP.Queue.DeclareOk declareOk = null;
        try{
                declareOk = rabbitAdmin.getRabbitTemplate().execute(channel -> {
                                return channel.queueDeclarePassive(queue.getName());

                            });
        }catch (Exception e){
            declareOk = null;
            log.info("Unable to Connect to the Queue :" + this.getQueue().getName());
        }

        if (declareOk == null) {
            builder.down().withDetail("Mesaage", "Queue: " + this.queue.getName() + " is " + Status.DOWN.getCode());
        } else {
            builder.up().withDetail("Mesaage", "Queue: " + this.queue.getName()  + " is " + Status.UP.getCode());
        }

    }

    @Override
    public Health checkHealth() {
        return this.health();
    }


    /*private String getVersion() {
        return (String)this.rabbitAdmin.getRabbitTemplate().execute((channel) -> {
            channel.
            return channel.getConnection().getServerProperties().get("version").toString();
        });
    }*/


}
