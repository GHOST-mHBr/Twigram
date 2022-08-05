package oop.prj.DB;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import oop.prj.model.Group;

public class GroupAdapter implements JsonSerializer<Group> , JsonDeserializer<Group> {

    @Override
    public JsonElement serialize(Group src, Type typeOfSrc, JsonSerializationContext context) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
