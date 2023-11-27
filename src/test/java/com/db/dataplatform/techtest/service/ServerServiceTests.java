package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.exception.CheckSumNotMatchingException;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
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
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;

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

        server = new ServerImpl(dataBodyServiceImplMock, modelMapper, objectMapper);
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws Exception {
        final ArgumentCaptor<DataBodyEntity> captorDataBodyEntity = ArgumentCaptor.forClass(DataBodyEntity.class);

        boolean success = server.saveDataEnvelope(TestDataHelper.REQUEST_CHECKSUM, testDataEnvelope);
        assertThat(success).isTrue();
        Mockito.verify(dataBodyServiceImplMock, Mockito.times(1)).saveDataBody(captorDataBodyEntity.capture());

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
}
