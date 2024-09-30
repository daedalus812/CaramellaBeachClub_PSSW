package org.ecommerce.caramellabeachclub.resources.exceptions;

public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException(String message) {
        super(message);
    }

}