//$Id$
package com.aisa.controller;

import java.util.concurrent.CompletableFuture;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aisa.model.GeminiResponse;
import com.aisa.model.QuestionRequest;
import com.aisa.model.UrlQuestionRequest;
import com.aisa.service.GeminiService;
import com.aisa.service.PdfService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/gemini")
@CrossOrigin
public class GeminiController {

	@Autowired
	private GeminiService geminiservice;
	
	@Autowired
    private PdfService pdfservice;
	
	@PostMapping("/ask-from-pdf")
	public CompletableFuture<ResponseEntity<GeminiResponse>> askFromPdf(
	    @RequestParam("file") MultipartFile file,
	    @RequestParam("question") String question) 
	{
		return pdfservice.extractFromPdfFile(file)
		        .thenCompose(pdfText -> geminiservice.getAnswerAsync(new QuestionRequest(pdfText, question)))
		        .thenApply(ResponseEntity::ok)
		        .exceptionally(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body(new GeminiResponse("Error: " + e.getMessage())));
	}
	
	@PostMapping("/ask-from-url")
	public Mono<ResponseEntity<GeminiResponse>> askFromUrl(@RequestBody UrlQuestionRequest request) {
		return geminiservice.getAnswerFromUrl(request)
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new GeminiResponse("Error: " + ex.getMessage()))
                ));
	}
	
}
