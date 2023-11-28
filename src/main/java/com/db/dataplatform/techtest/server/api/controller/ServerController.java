package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.Constants;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.UpdateDataHeader;
import com.db.dataplatform.techtest.server.service.Server;
import com.db.dataplatform.techtest.server.exception.CheckSumNotMatchingException;
import com.db.dataplatform.techtest.server.exception.DownStreamPushDataException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.convertor.BlockTypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/v1/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;

    @PostMapping(value = "/data", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> saveData(
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
        catch (DownStreamPushDataException downStreamPushDataException){
            log.error(downStreamPushDataException.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, downStreamPushDataException.getMessage());
        }
        log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        return ResponseEntity.ok(checksumPass);
    }

    @GetMapping(value = "/data/{blockType}")
    public ResponseEntity<List<com.db.dataplatform.techtest.server.dto.DataBody>> getDataByBlockType(@PathVariable BlockTypeEnum blockType) {
        return ResponseEntity.ok(server.getDataByBlockType(blockType));
    }


    @PutMapping(value = "/data/{name}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateHeader(
            @PathVariable String name,
            @Valid @RequestBody UpdateDataHeader updateDataHeader) throws Exception {
        server.updateHeader(name, updateDataHeader);
        return ResponseEntity.ok().build();
    }

    @InitBinder
    public void initBinder(final WebDataBinder webdataBinder) {
        webdataBinder.registerCustomEditor(BlockTypeEnum.class, new BlockTypeConverter());
    }

}
