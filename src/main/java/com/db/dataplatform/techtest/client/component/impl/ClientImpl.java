package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import com.db.dataplatform.techtest.server.dto.DataBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {
    private final RestTemplate restTemplate;
    public static final String URI_PUSHDATA = "http://localhost:8090/api/v1/dataserver/data";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/api/v1/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/api/v1/dataserver/update/{name}/{blockType}");

    @Override
    public void pushData(DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
        restTemplate.postForEntity(URI_PUSHDATA, dataEnvelope, Boolean.class);
        log.info("Pushing data {} to {} completed", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
    }

    @Override
    public List<DataBody> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);
//        ResponseEntity<DataBody[]> responseEntity =
//                restTemplate.getForEntity(URI_GETDATA, DataBody[].class);

        Map<String,String> variables = new HashMap<>();
        variables.put("blockType", blockType);
        ResponseEntity<List<DataBody>> responseEntity =
                restTemplate.exchange(URI_GETDATA.expand(variables),
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<DataBody>>() {
        });
        log.info("");
        return responseEntity.getBody();
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        return true;
    }


}
