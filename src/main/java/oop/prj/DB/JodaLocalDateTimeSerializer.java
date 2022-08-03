package oop.prj.DB;

import java.lang.reflect.Type;

import org.joda.time.LocalDateTime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JodaLocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jo = new JsonObject();
        jo.addProperty("String value",src.toString());
        return jo;
    }   
}