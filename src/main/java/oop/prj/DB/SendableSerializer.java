package oop.prj.DB;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import oop.prj.model.Sendable;

public class SendableSerializer implements JsonSerializer<Sendable> {

    private static SendableSerializer instance = null;

    private SendableSerializer() {
    }

    private SendableSerializer(SendableSerializer other) {
    }

    @Override
    public JsonElement serialize(Sendable src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jo = new JsonObject();
        jo.addProperty("receiver_id", String.valueOf(src.getReceiverId()));
        String str = src.getReceiverClass().getName();
        jo.addProperty("receiver_class_name", str);
        return jo;
    }

    public static SendableSerializer getInstance() {
        if (instance == null)
            instance = new SendableSerializer();
        return instance;
    }

}
