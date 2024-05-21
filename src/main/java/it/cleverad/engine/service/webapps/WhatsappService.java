package it.cleverad.engine.service.webapps;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class WhatsappService {

    private static final String TOKEN = "iey9ro4dloyczjah";
    private static final String INSTANCE = "instance60685";

    public boolean checkNumber(String phoneNumber) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.ultramsg.com/" + INSTANCE +
                            "/contacts/check?token=" + TOKEN +
                            "&chatId=" + phoneNumber.trim() +
                            "@c.us&nocache=0")
                    .get()
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();
            Check check = new Gson().fromJson(response.body().string(), Check.class);
            return check.getStatus().equals("valid");
        } catch (IOException e) {
            log.error("Eccezione check Whatsapp :: " + e.getMessage(), e);
            return false;
        }
    }


}