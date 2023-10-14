package cqrs.microservice.order.exceptions;

public class SerializeToJsonBytesException extends RuntimeException{
    public SerializeToJsonBytesException(){
        super();
    }
    public SerializeToJsonBytesException(String message){
        super(message);
    }
    public SerializeToJsonBytesException(String message, Throwable cause){
        super(message, cause);
    }

}
