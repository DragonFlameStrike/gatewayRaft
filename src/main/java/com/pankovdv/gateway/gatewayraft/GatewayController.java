package com.pankovdv.gateway.gatewayraft;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@Slf4j
public class GatewayController {

    @Autowired
    GatewayContext context;

    @Autowired
    RestServiceImpl restService;

    @GetMapping("/")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Boolean> sendMessage(@RequestParam  String message) {
        if (context.leaderPort == null) {
            findLeader();
            log.info("New Leader: port - {}", context.leaderPort);
        }
        return sendToLeader(message);
    }

    private void findLeader() {
        for (int port = 8081; port <= 8086; port++) {
            var url = restService.buildUrl(port, "/api/v1/gateway/get-leader");
            var leaderPort = restService.sendGetRequest(url);
            log.info(leaderPort);
            if (!Objects.equals(leaderPort, "error")) {
                context.leaderPort = leaderPort;
                log.info("new leader port: {}", leaderPort);
            }
        }
    }

    private ResponseEntity<Boolean> sendToLeader(String message) {
        var url = restService.buildUrl(Integer.valueOf(context.leaderPort), "/api/v1/gateway/");
        var result = restService.sendPostRequest(url, message, Boolean.class);
        if (result == null || !Objects.equals(result.getBody(), Boolean.TRUE)) {
            log.info("Leader deprecated. Send message error.");
            findLeader();
        }
        return result;
    }
}
