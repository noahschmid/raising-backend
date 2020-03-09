package ch.raising.models;

public class ErrorResponse {
    private String message;
    private Object error;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, Object error) {
        this.message = message;
        this.error = error;
    }

    public ErrorResponse(Object error) {
        this.error = error;
    }

    public String getMessage() { return this.message; }
    public Object getError() { return this.error; }
}