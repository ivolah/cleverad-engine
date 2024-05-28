package it.cleverad.engine.service.tinyurl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class TinyUrlService {

    public TinyData createShort(String alias, String longUrl){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        String content = "{" +
                "  \"url\":\"" + longUrl + "\"," +
                "  \"domain\": \"tinyurl.com\"," +
                "  \"alias\": \"" + alias + "\"" +
                "}";

        log.info(content);
        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url("https://api.tinyurl.com/create?api_token=Tn60UDdH7VL9hw2TvnMPB6ZUyCCtH9QIRENotPieGjgF1abrlHOJYSVlGByJ")
                .method("POST", body)
                .addHeader("Content-Type", mediaType.type())
                .build();
        return new Gson().fromJson(getRequestBody(client, request), TinyData.class);
    }

    private String getRequestBody(OkHttpClient client, Request request) {
        String respBody = null;
        String code = null;
        try {
            Response response = client.newCall(request).execute();
            code = String.valueOf(response.code());
            respBody = response.body().string();
            log.info("RB :: " + code + " - " + respBody);

            if (code != null && code.startsWith("2")) {
                return respBody;
            } else {
                log.warn(" getRequestBody :: " + code + " - " + respBody);
                return null;
            }
        } catch (IOException e) {
            log.error(" getRequestBody :: " + code + " - " + respBody, e);
            return null;
        }
    }
}