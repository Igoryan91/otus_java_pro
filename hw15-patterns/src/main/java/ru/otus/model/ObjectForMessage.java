package ru.otus.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectForMessage implements Copyable<ObjectForMessage> {
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public ObjectForMessage copy() {
        ObjectForMessage objectForMessageCopy = new ObjectForMessage();
        objectForMessageCopy.setData(new ArrayList<>(this.getData()));
        return objectForMessageCopy;
    }

    @Override
    public String toString() {
        if (data != null) {
            return data.toString();
        } else {
            return "";
        }
    }
}
