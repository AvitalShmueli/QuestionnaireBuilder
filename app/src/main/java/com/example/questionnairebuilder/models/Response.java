package com.example.questionnairebuilder.models;

import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Response {
    private String surveyID;
    private String questionID;
    private String responseID;
    private String userID;
    private Timestamp created = new Timestamp(new Date());
    private Timestamp modified = new Timestamp(new Date());
    private List<String> responseValues;

    public Response() {
        responseValues = new ArrayList<>();
    }

    public String getSurveyID() {
        return surveyID;
    }

    public Response setSurveyID(String surveyID) {
        this.surveyID = surveyID;
        return this;
    }

    public String getQuestionID() {
        return questionID;
    }

    public Response setQuestionID(String questionID) {
        this.questionID = questionID;
        return this;
    }

    public String getResponseID() {
        return responseID;
    }

    public Response setResponseID(String responseID) {
        this.responseID = responseID;
        return this;
    }

    public String getUserID() {
        return userID;
    }

    public Response setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public Timestamp getCreated() {
        return created;
    }

    public Response setCreated(Timestamp created) {
        this.created = created;
        return this;
    }

    public Timestamp getModified() {
        return modified;
    }

    public Response setModified(Timestamp modified) {
        this.modified = modified;
        return this;
    }

    public List<String> getResponseValues() {
        return responseValues;
    }

    public Response setResponseValues(List<String> responseValues) {
        this.responseValues = responseValues;
        return this;
    }

    public Response addResponse(String response){
        responseValues.add(response);
        return this;
    }

    public void save(){
        FirestoreManager.getInstance().addResponse(this);
    }
}
