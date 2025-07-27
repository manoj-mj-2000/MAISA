//$Id$
package com.aisa.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.aisa.model.GeminiResponse;
import com.aisa.model.QuestionRequest;
import com.aisa.model.UrlQuestionRequest;

import reactor.core.publisher.Mono;

@Service
public class GeminiService {
	
	private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
	
	@Value("${gemini.api.key}")
	private String API_KEY;
	
	private WebClient webClient;
	
	public GeminiService(WebClient.Builder builder) {
		this.webClient = builder.baseUrl(API_URL).build();
	}
	
	public Mono<GeminiResponse> getAnswer(QuestionRequest question) {
		
		String prompt = question.getPdfText() + "\n\nQ: " + question.getQuestion();
		
		Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt)
                ))
            )
        );
		
		return webClient.post()
				.uri(uriBuilder -> uriBuilder.queryParam("key", API_KEY).build())
			    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			    .bodyValue(body)
			    .retrieve()
			    .onStatus(HttpStatusCode::isError, response ->
			        response.bodyToMono(String.class)
			            .flatMap(error -> Mono.error(new RuntimeException("Gemini API Error: " + error)))
			    )
			    .bodyToMono(Map.class)
			    .map(this::parseGeminiResponse);
		
	}
	
	public CompletableFuture<GeminiResponse> getAnswerAsync(QuestionRequest question){
		return getAnswer(question).toFuture().thenApply(response -> response);
	}
	
	public Mono<GeminiResponse> getAnswerFromUrl(UrlQuestionRequest request) {
		return Mono.fromFuture(
		        CompletableFuture.supplyAsync(() -> extractTextFromUrl(request.getUrl()))
		            .thenCompose(text -> getAnswer(new QuestionRequest(text, request.getQuestion())).toFuture())
		    );
	}
	
	private String extractTextFromUrl(String url) {
	    try {
	        Document doc = Jsoup.connect(url).get();
	        return doc.body().text(); // simplified; we can improve this
	    } catch (IOException e) {
	        return "Failed to fetch content from the URL.";
	    }
	}
	
	private GeminiResponse parseGeminiResponse(Map<String, Object> responseBody) {
	    if(responseBody.containsKey("candidates")) {
	        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
	        if (!candidates.isEmpty()) {
	            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
	            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
	            if (!parts.isEmpty()) {
	                return new GeminiResponse((String) parts.get(0).get("text"));
	            }
	        }
	    }
	    return new GeminiResponse("No answer found");
	}
	
}
