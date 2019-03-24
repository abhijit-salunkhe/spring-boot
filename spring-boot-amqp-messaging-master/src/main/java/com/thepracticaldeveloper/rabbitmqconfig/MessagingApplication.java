package com.thepracticaldeveloper.rabbitmqconfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MessagingApplication {

    public static final String EXCHANGE_NAME = "testexchange";
    public static final String QUEUE_GENERIC_NAME = "testqueue";
    public static final String QUEUE_SPECIFIC_NAME = "testqueue";
    public static final String ROUTING_KEY = "#";

    public static void main(String[] args) {
        SpringApplication.run(MessagingApplication.class, args);
    }

    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue appQueueGeneric() {
        return new Queue(QUEUE_GENERIC_NAME);
    }

    @Bean("appQueueSpecific")
    public Queue appQueueSpecific() {
        return new Queue(QUEUE_SPECIFIC_NAME);
    }

    @Bean
    public Binding declareBindingGeneric() {
        return BindingBuilder.bind(appQueueGeneric()).to(appExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding declareBindingSpecific() {
        return BindingBuilder.bind(appQueueSpecific()).to(appExchange()).with(ROUTING_KEY);
    }

    // You can comment the two methods below to use the default serialization / deserialization (instead of JSON)
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(EXCHANGE_NAME);
        rabbitTemplate.setRoutingKey("#");
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate){
        return new RabbitAdmin(rabbitTemplate);
    }

    @Bean
    public RabbitQueueHealthMonitor rabbitQueueHealthMonitor(RabbitAdmin rabbitAdmin, @Qualifier("appQueueSpecific") Queue queue){
        return new RabbitQueueHealthMonitor(rabbitAdmin, queue);
    }

    @Bean
    public HeartBeatService<RabbitQueueHealthMonitor> rabbitHealthMonitor(){
        return new HeartBeatService<RabbitQueueHealthMonitor>();
    }




}
