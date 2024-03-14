package com.chat;

import com.chat.controller.ChatController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@SpringBootTest
class ChatAppApplicationTests {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private ChatController chatController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}


	@Test
	void testAskApi_Success() {
		// Arrange
		String APIURL = "https://chatgpt-gpt4-ai-chatbot.p.rapidapi.com/ask";
		String APIKEY = "ed9c6c1541mshf49bd03bac77beap12a657jsn85f7a005d7ef";
		String HOSTNAME = "chatgpt-gpt4-ai-chatbot.p.rapidapi.com";

		String requestBody = "{\"partial_text\":\"What is OpenAI?\"}";
		String responseBody = "{\"completed_text\":\"OpenAI is an artificial intelligence research laboratory consisting of the for-profit corporation OpenAI LP and its parent company, OpenAI Inc.\"}";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-RapidAPI-Key", APIKEY);
		headers.set("X-RapidAPI-Host", HOSTNAME);

		ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

		when(restTemplate.exchange(APIURL, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class))
				.thenReturn(responseEntity);

		Map<String, String> requestBodyMap = new HashMap<>();
		requestBodyMap.put("partial_text", "What is OpenAI?");

		// Act
		ResponseEntity<String> response = chatController.askApi(requestBodyMap);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(responseBody, response.getBody());

		verify(restTemplate, times(1)).exchange(APIURL, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);
	}

	@Test
	void testAskApi_HttpClientErrorException() {
		// Arrange
		String errorMessage = "Unauthorized";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.UNAUTHORIZED, errorMessage);

		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
				.thenThrow(httpClientErrorException);

		Map<String, String> requestBodyMap = new HashMap<>();
		requestBodyMap.put("partial_text", "What is OpenAI?");

		// Act
		ResponseEntity<String> response = chatController.askApi(requestBodyMap);

		// Assert
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals(errorMessage, response.getBody());
	}
}

