package oop.prj.DB;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

    private static LocalDateTimeSerializer instance = null;

    private LocalDateTimeSerializer() {

    }

    private LocalDateTimeSerializer(LocalDateTimeSerializer other) {

    }

    public static LocalDateTimeSerializer getInstance() {
        if (instance == null) {
            instance = new LocalDateTimeSerializer();
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

}
