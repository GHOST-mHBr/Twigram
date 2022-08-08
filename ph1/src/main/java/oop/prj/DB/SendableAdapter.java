package oop.prj.DB;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import oop.prj.model.Group;
import oop.prj.model.User;
import oop.prj.model.Sendable;

public class SendableAdapter implements JsonSerializer<Sendable>, JsonDeserializer<Sendable> {

    private static SendableAdapter instance = null;

    private SendableAdapter() {
    }

    private SendableAdapter(SendableAdapter other) {
    }

    @Override
    public JsonElement serialize(Sendable src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jo = new JsonObject();
        jo.addProperty("receiver_id", String.valueOf(src.getReceiverId()));
        String str = src.getReceiverClass().getName();
        jo.addProperty("receiver_class_name", str);
        return jo;
    }

    public static SendableAdapter getInstance() {
        if (instance == null)
            instance = new SendableAdapter();
        return instance;
    }

    @Override
    public Sendable deserialize(JsonElement e, Type t, JsonDeserializationContext des)
            throws JsonParseException {
        JsonObject jo = e.getAsJsonObject();
        Integer id = Integer.parseInt(jo.get("receiver_id").getAsString());
        Class<?> res;
        try {
            res = Class.forName(jo.get("receiver_class_name").getAsString());
            if (res.getSimpleName().equals("User")) {
                return User.getWithId(id);
            } else if (res.getSimpleName().equals("Group")) {
                return Group.getWithId(id);
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        return null;
    }

}
