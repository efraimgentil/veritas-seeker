package me.efraimgentil.seeker.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Configuration
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean

@Configuration
class RabbitMQConfig {

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, jsonObjectMapper : ObjectMapper): RabbitTemplate {
        var rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = producerJackson2MessageConverter(jsonObjectMapper)
        return rabbitTemplate
    }

    @Bean
    fun producerJackson2MessageConverter(jsonObjectMapper : ObjectMapper): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter(jsonObjectMapper)
    }
}