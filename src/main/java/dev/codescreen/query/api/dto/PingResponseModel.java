package dev.codescreen.query.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PingResponseModel {
    private String serverTime;
}
