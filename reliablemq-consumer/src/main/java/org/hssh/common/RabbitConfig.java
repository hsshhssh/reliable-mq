package org.hssh.common;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hssh on 2017/9/21.
 */
@Configuration
public class RabbitConfig
{

    @Bean
    public Queue helloQueue()
    {
        return new Queue("hello");
    }

}
