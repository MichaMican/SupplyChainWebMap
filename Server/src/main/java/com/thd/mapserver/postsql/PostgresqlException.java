package com.thd.mapserver.postsql;

public class PostgresqlException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PostgresqlException(String message, Throwable throwable) {
        super(message, throwable);
    }

}

