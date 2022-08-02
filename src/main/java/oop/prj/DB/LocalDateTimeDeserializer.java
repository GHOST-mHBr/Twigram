package oop.prj.DB;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

    private static LocalDateTimeDeserializer instance = null;

    private LocalDateTimeDeserializer(){}
    private LocalDateTimeDeserializer(LocalDateTimeDeserializer other){}

    public static LocalDateTimeDeserializer getInstance(){
        if(instance == null){
            instance = new LocalDateTimeDeserializer();
        }
        return instance;
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jo = json.getAsJsonObject();
        

        return LocalDateTime.parse(jo.get("String value").getAsString());
        // return null;
    }

}
