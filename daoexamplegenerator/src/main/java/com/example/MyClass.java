package com.example;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyClass {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "me.yunchuang.greendao");
        addNote(schema);
        addChat(schema);
        new DaoGenerator().generateAll(schema, "E:\\Android\\workspace\\MyChat\\app\\src\\main\\java-gen");
    }
    private static void addNote(Schema schema){
        Entity note=schema.addEntity("Note");
        note.addIdProperty();
        note.addStringProperty("text").notNull();
        note.addStringProperty("comment");
        note.addDateProperty("date");
    }
    private static void addChat(Schema schema){
        Entity chat=schema.addEntity("Chat");
        chat.addIdProperty();
        chat.addStringProperty("fromId");
        chat.addStringProperty("toId");
        chat.addStringProperty("date");
        chat.addStringProperty("content");
        chat.addBooleanProperty("isRead");
        chat.addStringProperty("other");
    }
}
