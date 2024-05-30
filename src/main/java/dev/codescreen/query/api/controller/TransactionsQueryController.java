package dev.codescreen.query.api.controller;

import dev.codescreen.query.api.dto.PingResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class TransactionsQueryController {

    @GetMapping("/ping")
    public ResponseEntity<Object> ping() {
        return new ResponseEntity<>(new PingResponseModel(LocalDateTime.now().toString()), HttpStatus.OK);
    }

}