package oop.prj.DB;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> , JsonDeserializer<LocalDateTime> {

    private static LocalDateTimeAdapter instance = null;

    private LocalDateTimeAdapter() {

    }

    private LocalDateTimeAdapter(LocalDateTimeAdapter other) {

    }

    public static LocalDateTimeAdapter getInstance() {
        if (instance == null) {
            instance = new LocalDateTimeAdapter();
        }
        return instance;
    }

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        if(src == null){
            return null;
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("String value", src.toString());
        return jo;
        // return null;
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jo = json.getAsJsonObject();
        

        return LocalDateTime.parse(jo.get("String value").getAsString());
        // return null;
    }

}
