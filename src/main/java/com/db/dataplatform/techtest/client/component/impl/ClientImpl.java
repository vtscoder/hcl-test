package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.Constants;
import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import com.db.dataplatform.techtest.server.api.model.UpdateDataHeader;
import com.db.dataplatform.techtest.server.dto.DataBody;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.util.MD5Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/api/v1/dataserver/data/{name}");

    private final ObjectMapper objectMapper;
    @Override
    public void pushData(DataEnvelope dataEnvelope) throws Exception{
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.X_REQUEST_BODY_CHECKSUM, MD5Utils.checkSum(objectMapper,dataEnvelope));
        HttpEntity<DataEnvelope> entity = new HttpEntity<>(dataEnvelope, headers);
        restTemplate.postForEntity(URI_PUSHDATA, entity, Boolean.class);
        log.info("Pushing data {} to {} completed", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
    }

    @Override
    public List<DataBody> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);
        Map<String,String> variables = new HashMap<>();
        variables.put("blockType", blockType);

        HttpEntity entity = new HttpEntity<>(null, null);

        ResponseEntity<List<DataBody>> responseEntity =
                restTemplate.exchange(URI_GETDATA.expand(variables),
                        HttpMethod.GET, entity, new ParameterizedTypeReference<List<DataBody>>() {
        });

        log.info("Get data by {} is completed", blockType);
        return responseEntity.getBody();
    }

    @Override
    public void updateData(String blockName, String newBlockType) {
        log.info("Updating block type to {} for block with name {}", newBlockType, blockName);
        Map<String,String> variables = new HashMap<>();
        variables.put("name", blockName);

        UpdateDataHeader updateDataHeader =  UpdateDataHeader.builder().blockType(BlockTypeEnum.valueOf(newBlockType)).build();

        restTemplate.put(URI_PATCHDATA.expand(variables), updateDataHeader);
        log.info("Update block type {} for name {}", newBlockType,blockName);
    }


}
