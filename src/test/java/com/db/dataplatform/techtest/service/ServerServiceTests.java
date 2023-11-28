package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.service.HadoopService;
import com.db.dataplatform.techtest.server.dto.DataBody;
import com.db.dataplatform.techtest.server.exception.CheckSumNotMatchingException;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.Server;
import com.db.dataplatform.techtest.server.service.impl.ServerImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {
    @Mock
    private DataStoreRepository dataStoreRepository;

    @Mock
    private  DataHeaderRepository dataHeaderRepository;

    @Mock
    private HadoopService hadoopService;


    private ModelMapper modelMapper;

    private DataBodyEntity expectedDataBodyEntity;

    private DataEnvelope testDataEnvelope;

    private ObjectMapper objectMapper;

    private Server server;


    @Before
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();

        objectMapper = Jackson2ObjectMapperBuilder
                .json()
                .build();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        expectedDataBodyEntity = modelMapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class));

        server = new ServerImpl(dataStoreRepository,dataHeaderRepository, modelMapper, objectMapper, hadoopService);
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws Exception {
        final ArgumentCaptor<DataBodyEntity> captorDataBodyEntity = ArgumentCaptor.forClass(DataBodyEntity.class);
        Mockito.doNothing().when(hadoopService).pushDataToHadoop(anyString());

        boolean success = server.saveDataEnvelope(TestDataHelper.REQUEST_CHECKSUM, testDataEnvelope);

        assertThat(success).isTrue();
        Mockito.verify(dataStoreRepository, Mockito.times(1)).save(captorDataBodyEntity.capture());
        Mockito.verify(hadoopService, Mockito.times(1)).pushDataToHadoop(anyString());

        DataBodyEntity bodyEntity =  captorDataBodyEntity.getValue();
        assertThat(bodyEntity.getDataBody()).isEqualTo(testDataEnvelope.getDataBody().getDataBody());
        assertThat(bodyEntity.getDataHeaderEntity().getName()).isEqualTo(testDataEnvelope.getDataHeader().getName());
        assertThat(bodyEntity.getDataHeaderEntity().getBlocktype()).isEqualTo(testDataEnvelope.getDataHeader().getBlockType());
    }

    @Test
    public void shouldNotSaveCheckSumNotMatching() throws Exception {
        try {
            server.saveDataEnvelope(TestDataHelper.REQUEST_CHECKSUM_INVALID, testDataEnvelope);
        }catch (CheckSumNotMatchingException checkSumNotMatchingException){
            assertThat(checkSumNotMatchingException.getMessage()).isEqualTo("Server check sum not matching with client checksum");
        }
    }

    @Test
    public void shouldGetDataByBlockType() {

        DataHeaderEntity dataHeaderEntity =  TestDataHelper.createTestDataHeaderEntity(BlockTypeEnum.BLOCKTYPEA,"name", Instant.now());
        DataBodyEntity dataBodyEntity =  TestDataHelper.createTestDataBodyEntity(dataHeaderEntity);
        List<DataBodyEntity> dataBodies = new ArrayList<>();
        dataBodies.add(dataBodyEntity);

        Mockito.when(dataStoreRepository.findAllByDataHeaderBlockType(any())).thenReturn(dataBodies);

       List<DataBody> dataByBlockType =
               server.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA);

        Mockito.verify(dataStoreRepository, Mockito.times(1)).findAllByDataHeaderBlockType(any());

        assertThat(dataByBlockType.get(0).getDataBody()).isEqualTo(dataBodyEntity.getDataBody());
        assertThat(dataByBlockType.get(0).getDataCheckSum()).isEqualTo(dataBodyEntity.getDataCheckSum());
        assertThat(dataByBlockType.get(0).getDataHeader().getBlocktype()).isEqualTo(dataBodyEntity.getDataHeaderEntity().getBlocktype().getType());
        assertThat(dataByBlockType.get(0).getDataHeader().getName()).isEqualTo(dataBodyEntity.getDataHeaderEntity().getName());

    }

    @Test
    public void shouldGetEmptyResponseWhenBlockTypeDataNoRecordInDb(){
        List<DataBody> dataByBlockType =
                server.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA);

        Mockito.verify(dataStoreRepository, Mockito.times(1)).findAllByDataHeaderBlockType(any());
        assertThat(dataByBlockType).hasSize(0);


    }

}
