package oop.prj.DB;

import java.lang.reflect.Type;
import java.nio.file.attribute.GroupPrincipal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import oop.prj.model.Group;
import oop.prj.model.Message;
import oop.prj.model.RawUser;
import oop.prj.model.Sendable;

public class MessageDeserializer implements JsonDeserializer<Message> {

    private static MessageDeserializer instance = null;

    private MessageDeserializer() {

    }

    private MessageDeserializer(MessageDeserializer other) {

    }

    public static MessageDeserializer getInstance() {
        if (instance == null)
            instance = new MessageDeserializer();
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
        if (receiverClass.getSimpleName().equals("RawUser")) {
            receiver = RawUser.getWithId(receiverId);
        } else if (receiverClass.getSimpleName().equals("Group")) {
            receiver = Group.getWithId(receiverId);
        }
        Message msg = new Message(context, RawUser.getWithId(ownerId), receiver);
        msg.setId(messageId);

        return msg;
    }

}
