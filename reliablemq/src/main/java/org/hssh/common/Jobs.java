package org.hssh.common;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hssh on 2017/9/22.
 */
@Component
public class Jobs
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SenderController senderController;

    @Autowired
    private Sender sender;

    /**
     * 状态为待发送
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void dealWithUnSend()
    {
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select id, query_url from message_record where state=1");

        List<Integer> invalidIds = new ArrayList<>();
        List<Integer> sendIds = new ArrayList<>();

        for (Map<String, Object> map : mapList)
        {
            HttpResult httpResult = HttpUtils.get((String) map.get("query_url"));
            if(200 == httpResult.getStatus())
            {
                Boolean isDone = Boolean.valueOf(httpResult.getContent());
                if(isDone)
                {
                    sendIds.add(((Long) map.get("id")).intValue());
                }
                else
                {
                    invalidIds.add(((Long) map.get("id")).intValue());
                }
            }
        }

        for (Integer id : sendIds)
        {
            senderController.confirmSend(id);
        }

        if(!CollectionUtils.isEmpty(invalidIds))
        {
            String ids = Joiner.on(",").join(invalidIds);
            String updateSql = String.format("update message_record set state=4 where id in (%s) and state=1", ids);
            jdbcTemplate.update(updateSql);
        }

    }


    /**
     * 状态为已发送
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dealwithUnComplete()
    {
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select id,queue,message from message_record where state=2");

        JSONObject temp = new JSONObject();
        for (Map<String, Object> map : mapList)
        {
            temp.put("id", map.get("id"));
            temp.put("message", map.get("message"));
            sender.send(String.valueOf(map.get("queue")), JSONObject.toJSONString(temp));
        }
    }


}
