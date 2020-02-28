package ch.raising.models;

public class Response {
    private String message;
    private Object result;

    public Response(String message) {
        this.message = message;
    }

    public Response(String message, Object result) {
        this.message = message;
        this.result = result;
    }

    public String getMessage() { return this.message; }
    public Object getResult() { return this.result; }
}