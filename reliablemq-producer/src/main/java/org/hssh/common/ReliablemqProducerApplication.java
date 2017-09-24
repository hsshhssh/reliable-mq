package org.hssh.common;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
public class ReliablemqProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReliablemqProducerApplication.class, args);
	}

	private static final String QUEUE = "hello";


	@GetMapping("hello")
	public boolean hello(@RequestParam("message") String message)
	{
		// 预发送
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("queryUrl", "http://localhost:8080/checkDone?id=" + 1));
		params.add(new BasicNameValuePair("queue", QUEUE));
		params.add(new BasicNameValuePair("message", message));
		HttpResult httpResult = HttpUtils.post("http://localhost:8090/preSend", params);

		if(200 != httpResult.getStatus() || Integer.valueOf(httpResult.getContent()) <= 0)
		{
			return false;
		}

		// TODO 处理逻辑

		// 发送
		List<NameValuePair> paramsSend = new ArrayList<>();
		paramsSend.add(new BasicNameValuePair("id", httpResult.getContent()));
		HttpUtils.post("http://localhost:8090/confirmSend", paramsSend);

		return true;
	}

	/**
	 *	检查逻辑是否处理
	 * @return
     */
	@GetMapping("checkDone")
	public boolean checkDone(@RequestParam("id") int id)
	{
		//TODO 查询逻辑 判断业务是否已经处理

		return true;
	}
}
