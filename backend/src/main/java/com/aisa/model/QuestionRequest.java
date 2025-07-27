//$Id$
package com.aisa.model;

public class QuestionRequest {

	private String question;
	private String pdfText;
	
	public QuestionRequest(String pdfText, String question) {
		this.question = question;
		this.pdfText = pdfText;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getPdfText() {
		return pdfText;
	}
	public void setPdfText(String pdfText) {
		this.pdfText = pdfText;
	}
	
}
