package com.pankovdv.gateway.gatewayraft;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class RestServiceImpl{

    @Autowired
    private RestTemplate restTemplate;

    public String sendGetRequest(String url) {
        log.info("SEND GET --> " + url);
        try {
            var response = restTemplate.getForObject(url, String.class);
            log.info("GET  <-- " + response);
            return response;
        } catch (RestClientException e) {
            log.info("GET  <-- 404. " + e.getLocalizedMessage());
            return "error";
        }
    }

    public <T> ResponseEntity<T> sendPostRequest(String url, Object payload, Class<T> respType) {
        log.debug("SEND POST --> " + url + ".\n Payload: " + payload);
        try {
            return restTemplate.postForEntity(url, payload, respType);
            //log.debug("GET  <-- " + response);
        } catch (RestClientException e) {
            log.debug("GET  <-- 404. " + e.getLocalizedMessage());
            return null;
        }
    }

    public String buildUrl(Integer port, String path) {
        return "http://localhost:" + port + path;
    }
}
