package com.batman.server.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RuntimeProperties {
    public String JWTSecret;
}
