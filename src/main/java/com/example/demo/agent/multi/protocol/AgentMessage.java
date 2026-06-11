package com.example.demo.agent.multi.protocol;

public class AgentMessage {

    public enum MessageType {
        TASK,
        RESULT,
        FEEDBACK,
        FINAL
    }

    private String sender;
    private String receiver;
    private String content;
    private MessageType type;
    private int round;

    public AgentMessage() {
    }

    public AgentMessage(String sender, String receiver, String content, MessageType type, int round) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type;
        this.round = round;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + sender + " -> " + receiver + " (round " + round + "): " + content;
    }
}
