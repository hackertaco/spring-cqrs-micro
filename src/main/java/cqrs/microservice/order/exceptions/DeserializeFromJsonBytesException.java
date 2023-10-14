package cqrs.microservice.order.exceptions;

public class DeserializeFromJsonBytesException extends RuntimeException{
    public DeserializeFromJsonBytesException(){
        super();
    }

    public DeserializeFromJsonBytesException(String message){
        super(message);
    }

    public DeserializeFromJsonBytesException(String message, Throwable cause){
        super(message, cause);
    }
}
