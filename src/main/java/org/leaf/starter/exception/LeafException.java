package org.leaf.starter.exception;public class LeafException extends RuntimeException {    private final String message;    public LeafException(String message) {        super(message);        this.message = message;    }    @Override    public String getMessage() {        return message;    }}