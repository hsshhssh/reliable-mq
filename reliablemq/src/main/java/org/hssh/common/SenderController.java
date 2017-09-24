package org.hssh.common;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hssh on 2017/9/21.
 */
@RestController
public class SenderController
{
    @Autowired
    private Sender sender;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 消息预发送
     * @return 返回主键
     */
    @PostMapping("preSend")
    public int preSend(@RequestParam("queryUrl") String queryUrl,
                       @RequestParam("queue") String queue,
                       @RequestParam("message") String message)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);

        //自增主键
        KeyHolder keyHolder = new GeneratedKeyHolder();


        // 保存消息
        jdbcTemplate.update(connection ->
        {
            String sql = "insert into message_record (query_url, queue, message, state, create_time, update_time) values (?,?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, queryUrl);
            ps.setString(2, queue);
            ps.setString(3, message);
            ps.setInt(4, 1);
            ps.setInt(5, nowTime);
            ps.setInt(6, nowTime);

            return ps;

        }, keyHolder);


        return keyHolder.getKey().intValue();
    }



    @PostMapping("confirmSend")
    public boolean confirmSend(@RequestParam("id") int id)
    {
        String sql = String.format("select queue, message from message_record where id = %d and state=1", id);

        Map<String, String> record = jdbcTemplate.queryForObject(sql, (resultSet,i) ->
        {
            Map<String, String> _temp = new HashMap<>();
            _temp.put("queue", resultSet.getString("queue"));
            _temp.put("message", resultSet.getString("message"));
            return _temp;
        });

        if(null == record)
        {
            return false;
        }


        // 修改消息为已发送
        String updateSql = String.format("update message_record set state=2 where id=%d", id);
        jdbcTemplate.update(updateSql);


        // 发送消息
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("message", record.get("message"));
        sender.send(record.get("queue"), JSONObject.toJSONString(obj));

        return true;

    }

    @PostMapping("complete")
    public boolean complete(@RequestParam("id") int id)
    {
        String sql = String.format("update message_record set state=3 where id=%d and state=2", id);
        jdbcTemplate.update(sql);

        return true;
    }

}
