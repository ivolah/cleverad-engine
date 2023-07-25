
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
public class TinyData {

    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("code")
    @Expose
    public Integer code;
    @SerializedName("errors")
    @Expose
    public List<Object> errors;

}
