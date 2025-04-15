package com.product.event;



public  class KafkaMessageEvent {
    private final String topic;
    private final String message;

    public KafkaMessageEvent(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "KafkaMessageEvent{" +
                "topic='" + topic + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}