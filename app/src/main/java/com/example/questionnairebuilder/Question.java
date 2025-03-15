package com.example.questionnairebuilder;

public class Question {
    private String question;
    private String type;
    private int order;

    public Question() {
    }

    public Question(String question, String type) {
        this.question = question;
        this.type = type;
    }

    public Question setQuestion(Question q){
        this.question = q.question;
        this.type = q.type;
        this.order = q.order;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public Question setQuestion(String question) {
        this.question = question;
        return this;
    }

    public String getType() {
        return type;
    }

    public Question setType(String type) {
        this.type = type;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public Question setOrder(int order) {
        this.order = order;
        return this;
    }
}
