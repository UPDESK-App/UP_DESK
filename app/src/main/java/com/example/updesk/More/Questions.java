package com.example.updesk.More;

public class Questions {
    public String question,questionID;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public Questions(String question, String questionID) {
        this.question = question;
        this.questionID = questionID;
    }
    public Questions() {

    }

}
