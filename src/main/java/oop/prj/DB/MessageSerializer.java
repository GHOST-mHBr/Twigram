package oop.prj.DB;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import oop.prj.model.Message;

public class MessageSerializer implements JsonSerializer<Message> {

    private static MessageSerializer instance = null;

    private MessageSerializer() {
    }

    private MessageSerializer(MessageDeserializer other) {

    }

    @Override
    public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jo = new JsonObject();
        jo.addProperty("receiverId", src.getReceiver().getReceiverId());
        jo.addProperty("receiverClassName", src.getReceiver().getReceiverClass().getName());
        jo.addProperty("context", src.getContext());
        jo.addProperty("ownerId", src.getOwner().getID());
        jo.addProperty("msgId", src.getId());
        return jo;
    }

    public static MessageSerializer getInstance() {
        if (instance == null)
            instance = new MessageSerializer();
        return instance;
    }

}
