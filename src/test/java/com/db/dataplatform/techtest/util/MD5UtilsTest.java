package com.db.dataplatform.techtest.util;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.util.MD5Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class MD5UtilsTest {

    ObjectMapper objectMapper;

    @BeforeEach
    void init(){
        objectMapper = Jackson2ObjectMapperBuilder
                .json()
                .build();
    }

    @Test
    public void testMD5CheckSum() throws Exception{
        DataEnvelope testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();
       String checkSum = MD5Utils.checkSum(objectMapper, testDataEnvelope);
        assertThat(checkSum).isEqualTo("20fbe850565cff2baf0c33d85c4efcc9");
    }
}
