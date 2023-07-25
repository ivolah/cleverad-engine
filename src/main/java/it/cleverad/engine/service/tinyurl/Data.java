
package it.cleverad.engine.service.tinyurl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Data {

    @SerializedName("domain")
    @Expose
    public String domain;
    @SerializedName("alias")
    @Expose
    public String alias;
    @SerializedName("deleted")
    @Expose
    public Boolean deleted;
    @SerializedName("archived")
    @Expose
    public Boolean archived;
    @SerializedName("analytics")
    @Expose
    public Analytics analytics;
    @SerializedName("tags")
    @Expose
    public List<Object> tags;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("expires_at")
    @Expose
    public Object expiresAt;
    @SerializedName("tiny_url")
    @Expose
    public String tinyUrl;
    @SerializedName("url")
    @Expose
    public String url;

}
