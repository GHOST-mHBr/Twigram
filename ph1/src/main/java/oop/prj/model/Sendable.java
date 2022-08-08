package oop.prj.model;

public interface Sendable {
    void messageReceived(Message msg)throws IllegalAccessException;
    void messageRemoved(Message msg);
    Integer getReceiverId();
    Class<? extends Sendable> getReceiverClass();
}
