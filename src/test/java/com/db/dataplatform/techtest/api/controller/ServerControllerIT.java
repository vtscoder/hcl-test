package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TechTestApplication;
import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.client.Constants;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TechTestApplication.class)
public class ServerControllerIT {

    @Autowired
    private ServerController serverController;
    private MockMvc mvc;
    private ObjectMapper objectMapper;

    @Autowired
    private DataStoreRepository dataStoreRepository;

    @Autowired
    private DataHeaderRepository dataHeaderRepository;

    @BeforeEach
    public void init(){
        this.mvc = MockMvcBuilders.standaloneSetup(serverController).build();
        objectMapper = Jackson2ObjectMapperBuilder
                .json()
                .build();
        dataStoreRepository.deleteAll();
        dataHeaderRepository.deleteAll();
    }

    @AfterEach
    public void after(){
        dataStoreRepository.deleteAll();
        dataHeaderRepository.deleteAll();
    }

    @Test
   public void shouldTestPushData() throws Exception {

        DataEnvelope testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();

        this.mvc.perform(MockMvcRequestBuilders.post("/api/v1/dataserver/data")
                .header(Constants.X_REQUEST_BODY_CHECKSUM, TestDataHelper.REQUEST_CHECKSUM)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDataEnvelope))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<DataHeaderEntity> dataHeaderEntities = dataHeaderRepository.findAll();
        assertThat(dataHeaderEntities.get(0).getId()).isNotNull();
        assertThat(dataHeaderEntities.get(0).getCreatedTimestamp()).isNotNull();
        assertThat(dataHeaderEntities.get(0).getName()).isEqualTo(testDataEnvelope.getDataHeader().getName());
        assertThat(dataHeaderEntities.get(0).getBlocktype()).isEqualTo(testDataEnvelope.getDataHeader().getBlockType());

        List<DataBodyEntity> dataBodyEntities = dataStoreRepository.findAll();
        assertThat(dataBodyEntities.get(0).getId()).isNotNull();
        assertThat(dataBodyEntities.get(0).getCreatedTimestamp()).isNotNull();
        assertThat(dataBodyEntities.get(0).getDataCheckSum()).isEqualTo(TestDataHelper.REQUEST_CHECKSUM);
        assertThat(dataBodyEntities.get(0).getDataBody()).isEqualTo(testDataEnvelope.getDataBody().getDataBody());
        assertThat(dataBodyEntities.get(0).getDataHeaderEntity().getId()).isEqualTo(dataHeaderEntities.get(0).getId());
    }

    @Test
    public void shouldTestPushDataValidationErrors() throws Exception {
        DataEnvelope testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObjectWithEmptyName();
        this.mvc.perform(MockMvcRequestBuilders.post("/api/v1/dataserver/data")
                        .header(Constants.X_REQUEST_BODY_CHECKSUM, TestDataHelper.REQUEST_CHECKSUM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDataEnvelope))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    public void shouldGetDataByBlockType() throws Exception {

        DataHeaderEntity dataHeaderEntity =  TestDataHelper.createTestDataHeaderEntity(BlockTypeEnum.BLOCKTYPEA,"name", Instant.now());
        DataBodyEntity dataBodyEntity =  TestDataHelper.createTestDataBodyEntity(dataHeaderEntity);
        DataHeaderEntity dataHeaderEntity_1 =  TestDataHelper.createTestDataHeaderEntity(BlockTypeEnum.BLOCKTYPEA,"name1", Instant.now());
        DataBodyEntity dataBodyEntity_1 =  TestDataHelper.createTestDataBodyEntity(dataHeaderEntity_1);
        DataHeaderEntity dataHeaderEntity_2 =  TestDataHelper.createTestDataHeaderEntity(BlockTypeEnum.BLOCKTYPEB,"name3", Instant.now());
        DataBodyEntity dataBodyEntity_2 =  TestDataHelper.createTestDataBodyEntity(dataHeaderEntity_2);
        dataStoreRepository.saveAll(Arrays.asList(dataBodyEntity, dataBodyEntity_1, dataBodyEntity_2));

        this.mvc.perform(MockMvcRequestBuilders.get("/api/v1/dataserver/data/blocktypea")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dataBody").value(dataBodyEntity.getDataBody()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dataCheckSum").value(dataBodyEntity.getDataCheckSum()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdTimestamp").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dataHeader.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dataHeader.name").value(dataHeaderEntity.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dataHeader.blocktype").value(dataHeaderEntity.getBlocktype().getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dataHeader.createdTimestamp").isNotEmpty())

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dataBody").value(dataBodyEntity_1.getDataBody()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dataCheckSum").value(dataBodyEntity_1.getDataCheckSum()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].createdTimestamp").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dataHeader.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dataHeader.name").value(dataHeaderEntity_1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dataHeader.blocktype").value(dataHeaderEntity_1.getBlocktype().getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dataHeader.createdTimestamp").isNotEmpty());
    }


}
