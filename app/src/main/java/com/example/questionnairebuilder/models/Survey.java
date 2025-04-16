package com.example.questionnairebuilder.models;

import com.example.questionnairebuilder.utilities.FirebaseManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Survey {
    public enum SurveyStatus {
        Draft, Published, Close
    }

    public enum Theme {
        Blue, Red, Green, Purple
    }

    private String ID;
    private String surveyTitle;
    private String description;
    private Date dueDate;
    private SurveyStatus status;
    private User author;
    private Date created;
    private Date modified;
    private Theme theme;

    private List<Question> questions;
    //private AnalyticsManager analytics;
    //private ErrorHandler errorHandler;
    private List<User> surveyViewers;

    // Constructors

    public Survey() {
        this.questions = new ArrayList<>();
        this.surveyViewers = new ArrayList<>();
    }

    // Getter and Setter Methods
    public String getID() {
        return ID;
    }

    public Survey setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getSurveyTitle() {
        return surveyTitle;
    }

    public Survey setSurveyTitle(String surveyTitle) {
        this.surveyTitle = surveyTitle;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Survey setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Survey setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public SurveyStatus getStatus() {
        return status;
    }

    public Survey setStatus(SurveyStatus status) {
        this.status = status;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public Survey setAuthor(User author) {
        this.author = author;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public Survey setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getModified() {
        return modified;
    }

    public Survey setModified(Date modified) {
        this.modified = modified;
        return this;
    }

    public Theme getTheme() {
        return theme;
    }

    public Survey setTheme(Theme theme) {
        this.theme = theme;
        return this;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Survey setQuestions(List<Question> questions) {
        this.questions = questions;
        return this;
    }

    /*public AnalyticsManager getAnalyticsManager() {
        return analytics;
    }*/

    /*public void setAnalyticsManager(AnalyticsManager analytics) {
        this.analytics = analytics;
    }*/

    /*public ErrorHandler getErrorHandler() {
        return errorHandler;
    }*/

    /*public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }*/

    public List<User> getSurveyViewers() {
        return surveyViewers;
    }

    public Survey setSurveyViewers(List<User> surveyViewers) {
        this.surveyViewers = surveyViewers;
        return this;
    }

    // Methods from UML

    public List<Question> getSurveyQuestions() {
        return questions;
    }

    /*public Map<String, String> getAnalytics() {
        if (analytics != null) {
            return analytics.generateReport();
        }
        return null;
    }*/

    public void save() {
        FirebaseManager.getInstance().addSurvey(this);
    }

    /*public void deleteQuestion(int questionID) {
        questions.removeIf(q -> q.getQuestionId() == questionID);
    }*/

    public void share() {
        // TODO: Implement share logic (e.g., email, link generation)
    }

}
