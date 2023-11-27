package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.client.Constants;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.CheckSumNotMatchingException;
import com.db.dataplatform.techtest.server.util.MD5Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Controller
@RequestMapping("/api/v1/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pushData(
            @RequestHeader(Constants.X_REQUEST_BODY_CHECKSUM) String requestCheckSum,
            @Valid @RequestBody DataEnvelope dataEnvelope) throws Exception {
        boolean checksumPass;
        try {
            log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
            checksumPass = server.saveDataEnvelope(requestCheckSum, dataEnvelope);
        }catch (CheckSumNotMatchingException checkSumNotMatchingException){
            log.error(checkSumNotMatchingException.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, checkSumNotMatchingException.getMessage());
        }
        log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        return ResponseEntity.ok(checksumPass);
    }

}
