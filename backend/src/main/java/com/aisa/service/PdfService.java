//$Id$
package com.aisa.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService {

	@Async("pdfTaskExecutor")
	public CompletableFuture<String> extractFromPdfFile(MultipartFile file) {
		try (InputStream inputStream = file.getInputStream(); 
	             PDDocument document = PDDocument.load(inputStream)) {
	             
	            PDFTextStripper stripper = new PDFTextStripper();
	            String text = stripper.getText(document);
	            return CompletableFuture.completedFuture(text);

	        } catch (Exception e) {
	            return CompletableFuture.failedFuture(e);
	        }
	}
}
