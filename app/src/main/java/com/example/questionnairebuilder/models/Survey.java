package com.example.questionnairebuilder.models;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.utilities.FirestoreManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Survey {
    public enum SurveyStatus {
        Draft, Published, Close
    }

    public enum SurveyTag {
        SURVEY(R.drawable.ic_survey),
        QUIZ(R.drawable.ic_quiz),
        QUESTIONNAIRE(R.drawable.ic_questionnaire),
        FEEDBACK(R.drawable.ic_feedback),
        POLL(R.drawable.ic_poll),
        EVALUATION(R.drawable.ic_evaluation),
        APPLICATION(R.drawable.ic_application),
        TEST(R.drawable.ic_test),
        REGISTRATION(R.drawable.ic_registration),
        REVIEW(R.drawable.ic_review),
        RESEARCH(R.drawable.ic_research);

        private final int iconResId;

        SurveyTag(int iconResId) {
            this.iconResId = iconResId;
        }

        public int getIconResId() {
            return iconResId;
        }
    }
    private String ID;
    private String surveyTitle;
    private String description;
    private Date dueDate;
    private SurveyStatus status;
    private User author;
    private Date created;
    private Date modified;
    private boolean newResponseAlert = false;
    private List<User> surveyViewers;
    private List<SurveyTag> tags = new ArrayList<>();

    // Constructors

    public Survey() {
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

    public boolean isNewResponseAlert() {
        return newResponseAlert;
    }

    public Survey setNewResponseAlert(boolean newResponseAlert) {
        this.newResponseAlert = newResponseAlert;
        return this;
    }

    public List<User> getSurveyViewers() {
        return surveyViewers;
    }


    public List<SurveyTag> getTags() {
        return tags;
    }

    public Survey setTags(List<SurveyTag> tags) {
        this.tags = tags;
        return this;
    }

    public Survey setSurveyViewers(List<User> surveyViewers) {
        this.surveyViewers = surveyViewers;
        return this;
    }

    public void save() {
        FirestoreManager.getInstance().addSurvey(this);
    }
}
