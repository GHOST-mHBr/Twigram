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
import oop.prj.model.Message;
import oop.prj.model.User;
import oop.prj.model.Sendable;

public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

    private static MessageAdapter instance = null;

    private MessageAdapter() {
    }

    private MessageAdapter(MessageAdapter other) {

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

    public static MessageAdapter getInstance() {
        if (instance == null)
            instance = new MessageAdapter();
        return instance;
    }

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context1)
            throws JsonParseException {
        JsonObject jo = new JsonObject();
        Integer receiverId = Integer.parseInt(jo.get("receiverId").getAsString());
        Integer ownerId = Integer.parseInt(jo.get("ownerId").getAsString());
        Class<?> receiverClass = null;
        try {
            receiverClass = Class.forName(jo.get("receiverClassName").getAsString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String context = jo.get("context").getAsString();
        Integer messageId = Integer.parseInt(jo.get("msgId").getAsString());
        Sendable receiver = null;
        if (receiverClass.getSimpleName().equals("User")) {
            receiver = User.getWithId(receiverId);
        } else if (receiverClass.getSimpleName().equals("Group")) {
            receiver = Group.getWithId(receiverId);
        }
        Message msg = null;
        try {
            msg = new Message(context, User.getWithId(ownerId), receiver);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        msg.setId(messageId);

        return msg;
    }

}
