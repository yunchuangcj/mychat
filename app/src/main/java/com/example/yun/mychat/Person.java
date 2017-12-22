package com.example.yun.mychat;

/**
 * Created by Yun on 2016/11/17.
 */

public class Person {
    private String name;
    private int msgcount;
    private String lastmsg;

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }

    public int getMsgcount() {
        return msgcount;
    }

    public void setMsgcount(int msgcount) {
        this.msgcount = msgcount;
    }

    public String getName() {
        return name;
    }

    public Person() {
        this.name="";
    }
    public Person(String name){
        this.name=name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
