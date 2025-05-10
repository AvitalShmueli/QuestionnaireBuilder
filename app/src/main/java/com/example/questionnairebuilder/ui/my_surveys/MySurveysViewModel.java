package com.example.questionnairebuilder.ui.my_surveys;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questionnairebuilder.interfaces.SurveysCallback;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MySurveysViewModel extends ViewModel {

    private final MutableLiveData<List<Survey>> surveysLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Survey>> fakeSurveysLiveData = new MutableLiveData<>();
    private ListenerRegistration listenerRegistration;
    private MutableLiveData<String> mCurrentUserId;

    public MySurveysViewModel() {
        mCurrentUserId = new MutableLiveData<>();
        mCurrentUserId.setValue("currentUserId");
    }

    public LiveData<String> getCurrentUserId() {
        return mCurrentUserId;
    }

    public LiveData<List<Survey>> getSurveys() {
        return surveysLiveData;
    }

    public LiveData<List<Survey>> getFakeSurveys() {
        return fakeSurveysLiveData;
    }

    public void startListeningFake() {
        // Dummy data
        List<Survey> fakeSurveys = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Survey survey = new Survey();
            survey.setID(UUID.randomUUID().toString());
            survey.setSurveyTitle("Survey Title " + i);
            survey.setDescription("This is description " + i);
            survey.setDueDate(new Date());
            survey.setStatus(i % 2 == 0 ? Survey.SurveyStatus.Draft : Survey.SurveyStatus.Published);
            fakeSurveys.add(survey);
        }
        fakeSurveysLiveData.setValue(fakeSurveys);
    }

    public void startListening() {
        listenerRegistration = FirestoreManager.getInstance().listenToMySurveys(mCurrentUserId.getValue(),new SurveysCallback() {
            @Override
            public void onSurveysLoaded(List<Survey> surveys) {
                surveysLiveData.setValue(surveys);
            }

            @Override
            public void onError(Exception e) {
                // Optional: you can post an error LiveData too
            }
        });
    }

    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopListening(); // Cleanup if ViewModel is destroyed
    }
}