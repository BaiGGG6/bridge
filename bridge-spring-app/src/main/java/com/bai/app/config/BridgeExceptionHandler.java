package com.bai.app.config;

import com.bai.bridge.Exception.PluginException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BridgeExceptionHandler {

    @ExceptionHandler(PluginException.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(200).body("An error occurred: " + ex.getMessage());
    }

}
