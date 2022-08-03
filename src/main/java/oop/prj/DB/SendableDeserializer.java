package oop.prj.DB;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import oop.prj.model.Group;
import oop.prj.model.RawUser;
import oop.prj.model.Sendable;

public class SendableDeserializer implements JsonDeserializer<Sendable> {

    private static SendableDeserializer instance = null;
    private static HashMap<String, Class<? extends Sendable>> mapper = new HashMap<>();

    static {
        mapper.put("Group", Group.class);
        mapper.put("RawUser", RawUser.class);
    }

    private SendableDeserializer() {
    }

    private SendableDeserializer(SendableDeserializer other) {
    }

    public static void register(String classNameStr, Class<? extends Sendable> class_) {
        mapper.put(classNameStr, class_);
    }

    public static SendableDeserializer getInstance() {
        if (instance == null)
            instance = new SendableDeserializer();
        return instance;
    }

    @Override
    public Sendable deserialize(JsonElement e, Type t, JsonDeserializationContext des)
            throws JsonParseException {
        JsonObject jo = e.getAsJsonObject();
        Integer id =Integer.parseInt(jo.get("receiver_id").getAsString());
        Class<?> res;
        try {
            res = Class.forName(jo.get("receiver_class_name").getAsString());
            if(res.getSimpleName().equals("RawUser")){
                return RawUser.getWithId(id);
            }else if(res.getSimpleName().equals("Group")) {
                return Group.getWithId(id);
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        return null;
    }

}
