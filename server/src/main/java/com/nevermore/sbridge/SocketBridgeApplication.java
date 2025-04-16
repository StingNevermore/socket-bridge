package com.nevermore.sbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.nevermore.sbridge.props.SbridgeProperties;

/**
 * @author Snake
 */
@SpringBootApplication
@EnableConfigurationProperties(SbridgeProperties.class)
public class SocketBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketBridgeApplication.class, args);
    }
}
