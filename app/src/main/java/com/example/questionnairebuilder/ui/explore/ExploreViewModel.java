package com.example.questionnairebuilder.ui.explore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questionnairebuilder.interfaces.SurveysCallback;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class ExploreViewModel extends ViewModel {

    private final MutableLiveData<List<Survey>> surveysLiveData = new MutableLiveData<>();
    private ListenerRegistration listenerRegistration;

    public LiveData<List<Survey>> getSurveys() {
        return surveysLiveData;
    }

    public void startListening() {
        listenerRegistration = FirestoreManager.getInstance().listenToAllActiveSurveys(new SurveysCallback() {
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