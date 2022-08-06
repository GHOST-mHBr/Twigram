package oop.prj.model;

public interface Sendable {
    void messageReceived(Message msg)throws IllegalAccessException;
    Integer getReceiverId();
    Class<? extends Sendable> getReceiverClass();
}
