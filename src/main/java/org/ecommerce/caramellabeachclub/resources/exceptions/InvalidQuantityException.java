package org.ecommerce.caramellabeachclub.resources.exceptions;

public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException() {
        super();
    }

    public InvalidQuantityException(String message) {
        super(message);
    }

    public InvalidQuantityException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidQuantityException(Throwable cause) {
        super(cause);
    }
}
