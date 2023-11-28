package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.Constants;
import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.UpdateDataHeader;
import com.db.dataplatform.techtest.server.dto.DataBody;
import com.db.dataplatform.techtest.server.dto.DataHeader;
import com.db.dataplatform.techtest.server.exception.DataNotFoundException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.service.Server;
import com.db.dataplatform.techtest.server.exception.CheckSumNotMatchingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class ServerControllerTest {

	public static final String URL_PUSHDATA = "/api/v1/dataserver/data";
	public static final String URL_GETDATA = "/api/v1/dataserver/data/";
	public static final String URL_PATCHDATA = "/api/v1/dataserver/data/";

	@Mock
	private Server serverMock;

	private DataEnvelope testDataEnvelope;
	private ObjectMapper objectMapper;
	private MockMvc mockMvc;
	private ServerController serverController;

	@Before
	public void setUp() {
		serverController = new ServerController(serverMock);
		mockMvc = standaloneSetup(serverController).build();
		objectMapper = Jackson2ObjectMapperBuilder
				.json()
				.build();

		testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();
	}

	@Test
	public void testPushDataPostCallWorksAsExpected() throws Exception {
		when(serverMock.saveDataEnvelope(anyString(), any(DataEnvelope.class))).thenReturn(true);
		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		MvcResult mvcResult = mockMvc.perform(post(URL_PUSHDATA)
				.header(Constants.X_REQUEST_BODY_CHECKSUM, TestDataHelper.REQUEST_CHECKSUM)
				.content(testDataEnvelopeJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
		assertThat(checksumPass).isTrue();
		Mockito.verify(serverMock, Mockito.times(1)).saveDataEnvelope(anyString(), any());

	}

	@Test
	public void testCheckSumNotMatching() throws Exception {

		when(serverMock.saveDataEnvelope(anyString(), any()))
				.thenThrow(new CheckSumNotMatchingException("not matching checksum"));
		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		MvcResult mvcResult = mockMvc.perform(post(URL_PUSHDATA)
						.header(Constants.X_REQUEST_BODY_CHECKSUM, TestDataHelper.REQUEST_CHECKSUM_INVALID)
						.content(testDataEnvelopeJson)
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertThat(mvcResult.getResolvedException().getMessage()).contains("not matching checksum");
		Mockito.verify(serverMock, Mockito.times(1)).saveDataEnvelope(anyString(), any());

	}

	@Test
	public void testDataByBlockType() throws Exception {
		DataBody dataBody =  new DataBody();
		dataBody.setDataBody("data body");
		DataHeader dataHeader =  new DataHeader();
		dataHeader.setName("name");
		dataHeader.setBlocktype(BlockTypeEnum.BLOCKTYPEB.getType());
		dataBody.setDataHeader(dataHeader);

		when(serverMock.getDataByBlockType(any()))
				.thenReturn(Arrays.asList(dataBody));
		 mockMvc.perform(get(URL_GETDATA +dataHeader.getBlocktype())
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].dataBody").value(dataBody.getDataBody()))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].dataHeader.name").value(dataHeader.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].dataHeader.blocktype").value(dataHeader.getBlocktype()));

		Mockito.verify(serverMock, Mockito.times(1)).getDataByBlockType(any());

	}

	@Test
	public void testUpdateData() throws Exception {

		doNothing().when(serverMock).patchHeader(anyString(), any());

		UpdateDataHeader updateDataHeader =  UpdateDataHeader
				.builder()
				.blockType(BlockTypeEnum.BLOCKTYPEB)
				.build();

		 mockMvc.perform(patch(URL_PATCHDATA +"name")
						.content(objectMapper.writeValueAsString(updateDataHeader))
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		Mockito.verify(serverMock, Mockito.times(1)).patchHeader(anyString(), any());



	}

	@Test
	public void testUpdateDataNotFound() throws Exception {
		doThrow(new DataNotFoundException("Data not found"))
				.when(serverMock).patchHeader(anyString(), any());


		UpdateDataHeader updateDataHeader =  UpdateDataHeader
				.builder()
				.blockType(BlockTypeEnum.BLOCKTYPEB)
				.build();
		mockMvc.perform(patch(URL_PATCHDATA +"name")
						.content(objectMapper.writeValueAsString(updateDataHeader))
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound())
				.andReturn();

		Mockito.verify(serverMock, Mockito.times(1)).patchHeader(anyString(), any());
	}


	@Test
	public void testUpdateRequestDataValidationBadRequest() throws Exception {

		UpdateDataHeader updateDataHeader =  UpdateDataHeader
				.builder()
				.build();
		mockMvc.perform(patch(URL_PATCHDATA +"name")
						.content(objectMapper.writeValueAsString(updateDataHeader))
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isBadRequest())
				.andReturn();

		Mockito.verify(serverMock, never()).patchHeader(anyString(), any());
	}

	@Test
	public void testPathVariableDataValidationNotAllowed() throws Exception {

		UpdateDataHeader updateDataHeader =  UpdateDataHeader
				.builder()
				.blockType(BlockTypeEnum.BLOCKTYPEA)
				.build();
		mockMvc.perform(patch(URL_PATCHDATA)
						.content(objectMapper.writeValueAsString(updateDataHeader))
						.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isMethodNotAllowed())
				.andReturn();

		Mockito.verify(serverMock, never()).patchHeader(anyString(), any());
	}

}
