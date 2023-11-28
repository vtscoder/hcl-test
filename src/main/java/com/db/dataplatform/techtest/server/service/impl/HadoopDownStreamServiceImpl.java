package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.service.HadoopService;
import com.db.dataplatform.techtest.server.exception.DownStreamPushDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class HadoopDownStreamServiceImpl implements HadoopService {
    @Value("${hadoop.data.push.api}")
   private String hadoopApi;
    private final RestTemplate restTemplate;

    // we can retry for specific exception as well, just retrying for any exception.
    @Retryable(value = Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    public void pushDataToHadoop(String payload) throws DownStreamPushDataException{
        try {
            restTemplate.postForEntity(hadoopApi, payload, String.class);
            log.info("Publish data to hadoop system was successful");
        }catch (Exception e){
            throw new DownStreamPushDataException("Publish data to hadoop system has failed after attempts");
        }
    }
}
