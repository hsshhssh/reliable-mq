package org.hssh.common;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by hssh on 2017/9/21.
 */
@Component
public class Sender
{
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(String queue, String content)
    {
        this.amqpTemplate.convertAndSend(queue, content);
    }


}
