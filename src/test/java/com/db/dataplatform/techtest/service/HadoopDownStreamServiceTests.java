package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.exception.DownStreamPushDataException;
import com.db.dataplatform.techtest.server.service.HadoopService;
import com.db.dataplatform.techtest.server.service.impl.HadoopDownStreamServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class HadoopDownStreamServiceTests {

    @Mock
    private  RestTemplate restTemplate;

   private HadoopService hadoopService;

    @Before
    public void setup() {
        hadoopService = new HadoopDownStreamServiceImpl(restTemplate);
        ReflectionTestUtils.setField(hadoopService,"hadoopApi","1");
    }

    @Test
    public void shouldTestPushDataToHadoop() throws DownStreamPushDataException {
        Mockito.when(restTemplate
                .postForEntity(ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(String.class))).thenReturn(ResponseEntity.ok().build());
        hadoopService.pushDataToHadoop("payload");

        Mockito.verify(restTemplate, Mockito.times(1))
                .postForEntity(ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(String.class));

    }

    @Test
    public void shouldTestPushDataToHadoopThrowDownStreamPushDataException() throws DownStreamPushDataException {
        Mockito.when(restTemplate
                .postForEntity(ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(String.class)))
                .thenThrow(new RuntimeException("Publish data to hadoop system has failed after attempts"));

        try {
            hadoopService.pushDataToHadoop("payload");
        }catch (Exception e)
        {
            assertThat(e.getMessage()).isEqualTo("Publish data to hadoop system has failed after attempts");
            assertThat(e).isExactlyInstanceOf(DownStreamPushDataException.class);
        }

        Mockito.verify(restTemplate,Mockito.times(1))
                .postForEntity(ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.eq(String.class));

    }
}
