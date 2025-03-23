package com.example.questionnairebuilder;

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

    private int ID;
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

    // Constructor
    public Survey() {
        this.questions = new ArrayList<>();
        this.surveyViewers = new ArrayList<>();
    }

    // Getter and Setter Methods
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSurveyTitle() {
        return surveyTitle;
    }

    public void setSurveyTitle(String surveyTitle) {
        this.surveyTitle = surveyTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public SurveyStatus getStatus() {
        return status;
    }

    public void setStatus(SurveyStatus status) {
        this.status = status;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
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

    public void setSurveyViewers(List<User> surveyViewers) {
        this.surveyViewers = surveyViewers;
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
        // TODO: Implement save logic (e.g., save to DB or local storage)
    }

    /*public void deleteQuestion(int questionID) {
        questions.removeIf(q -> q.getQuestionId() == questionID);
    }*/

    public void share() {
        // TODO: Implement share logic (e.g., email, link generation)
    }

}
