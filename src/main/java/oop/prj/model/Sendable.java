package oop.prj.model;

public interface Sendable {
    void messageReceived(Message msg);
    Integer getReceiverId();
    Class<? extends Sendable> getReceiverClass();
}
