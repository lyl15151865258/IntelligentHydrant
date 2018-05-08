package cn.njmeter.intelligenthydrant.network.bean;

public class MessageEvent {

    private String className;

    private String message;

    public MessageEvent(String message,String className) {
        this.message = message;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}