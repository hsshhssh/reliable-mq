package org.hssh.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hssh on 2017/9/21.
 */
@Component
@RabbitListener(queues = "hello")
public class Receiver
{

    @RabbitHandler
    public void process(String content)
    {
        // TODO  保证幂等性

        System.out.println("---------------" + content);


        // 修改消息为已完成
        JSONObject obj = JSONObject.parseObject(content);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", obj.getString("id")));
        HttpUtils.post("http://localhost:8090/complete", params);
    }
}
