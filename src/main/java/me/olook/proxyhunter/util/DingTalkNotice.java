package me.olook.proxyhunter.util;

import lombok.RequiredArgsConstructor;
import me.olook.proxyhunter.ProxyHunterProperties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author red
 * @date 2020-11-29 14:01
 * @email vayci2012@gmail.com
 */
@Component
@RequiredArgsConstructor
public class DingTalkNotice {

    private final ProxyHunterProperties properties;

    private static final String url = "https://oapi.dingtalk.com/robot/send?access_token=";

    public void send(String msg){
        try {
            String payload = "{\n" +
                    "    \"title\":\"代理池异常告警\",\n" +
                    "    \"msgtype\":\"markdown\",\n" +
                    "    \"markdown\":{\n" +
                    "        \"title\":\"告警\",\n" +
                    "        \"text\":\"代理池异常告警\\n\\n > 请及时更换配置 \\n\\n > 异常信息:"+msg+"\"\n" +
                    "    }\n" +
                    "}";
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url+properties.getSentry().getToken());
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE,"application/json");
            StringEntity entity = new StringEntity(payload,"utf-8");
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity res = response.getEntity();
            String responseContent = EntityUtils.toString(res, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
