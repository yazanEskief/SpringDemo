package de.fhws.fiw.fds.springDemoApp.util;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnlinkResponse {

    private String status;

    private String error;

    private String operation;

    public UnlinkResponse() {
    }

    public UnlinkResponse(String status, String error, String operation) {
        this.status = status;
        this.error = error;
        this.operation = operation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}

