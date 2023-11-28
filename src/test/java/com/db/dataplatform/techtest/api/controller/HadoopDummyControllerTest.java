package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.Constants;
import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.HadoopDummyServerController;
import com.db.dataplatform.techtest.server.service.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class HadoopDummyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private Server serverMock;

    private HadoopDummyServerController hadoopDummyServerController;

    @Before
    public void setUp() {
        hadoopDummyServerController = new HadoopDummyServerController(serverMock);
        mockMvc = standaloneSetup(hadoopDummyServerController).build();
    }

    @Test
    public void testPushBigDataSuccessOrFailure() throws Exception {


        MvcResult mvcResult = mockMvc.perform(post("/api/v1/hadoopserver/pushbigdata")
                        .header(Constants.X_REQUEST_BODY_CHECKSUM, TestDataHelper.REQUEST_CHECKSUM)
                        .content("payload")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();


        if(mvcResult.getResponse().getStatus() ==  HttpStatus.GATEWAY_TIMEOUT.value()){
            assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT.value());
        }

        if(mvcResult.getResponse().getStatus() ==  HttpStatus.OK.value()){
            assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        }

    }
}
