//$Id$
package com.aisa.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aisa.service.PdfService;

@RestController
@RequestMapping("/api/file")
@CrossOrigin
public class FileController {

	@Autowired
	private PdfService pdfService;
	
//	@PostMapping("/upload")
//	public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file){
//		
//		try {
//			String text = pdfService.extractFromPdfFile(file);
//			return ResponseEntity.ok(text);
//		}
//		catch (IOException e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("Failed to Parse PDF file: "+e.getMessage());
//		}
//		catch(Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("Something went wrong: "+e.getMessage());
//		}
//		
//	}
	
}
