package oop.prj.DB;

import java.lang.reflect.Type;

import org.joda.time.LocalDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JodaLocalDateTimeAdapter implements JsonSerializer<LocalDateTime> , JsonDeserializer<LocalDateTime> {

    private static JodaLocalDateTimeAdapter instance = null;

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jo = new JsonObject();
        jo.addProperty("String value",src.toString());
        return jo;
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jo = json.getAsJsonObject();
        LocalDateTime res = LocalDateTime.parse(jo.get("String value").getAsString());
        return res;
    }

    public static Object getInstance() {
        if(instance == null)
            instance = new JodaLocalDateTimeAdapter();
        return instance;
    }
}