package niteknightt.lichessapi;

import com.google.gson.annotations.SerializedName;

public class LichessVariant {
    public LichessEnums.VariantKey key;
    public String name;
    @SerializedName("short")
    public String shortName;
}
