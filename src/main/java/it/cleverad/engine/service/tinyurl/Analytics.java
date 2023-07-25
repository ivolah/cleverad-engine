
package it.cleverad.engine.service.tinyurl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Analytics {

    @SerializedName("enabled")
    @Expose
    public Boolean enabled;
    @SerializedName("public")
    @Expose
    public Boolean _public;

}
