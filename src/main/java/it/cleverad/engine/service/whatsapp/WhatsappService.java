package it.cleverad.engine.service.whatsapp;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WhatsappService {

    public boolean checkNumber(String phoneNumber) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.ultramsg.com/instance60685/contacts/check?token=1347itppkx88gad4&chatId=+393475076602@c.us&nocache=0")
                .get()
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        Response response = client.newCall(request).execute();
        Check check = new Gson().fromJson(response.body().string(), Check.class);

        return Boolean.parseBoolean(check.getStatus());
    }


}