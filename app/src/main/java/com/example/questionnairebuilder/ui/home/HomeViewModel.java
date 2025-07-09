package com.example.questionnairebuilder.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questionnairebuilder.interfaces.SurveysWithCountCallback;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.SurveyWithResponseCount;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mUsername = new MutableLiveData<>();
    private final MutableLiveData<String> mCurrentUserId;
    private final MutableLiveData<List<SurveyWithResponseCount>> surveysLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<SurveyWithResponseCount>> fakeSurveysLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private List<SurveyWithResponseCount> currentSurveys = new ArrayList<>();
    private ListenerRegistration listenerRegistration;

    public HomeViewModel() {
        mCurrentUserId = new MutableLiveData<>();
        mCurrentUserId.setValue(AuthenticationManager.getInstance().getCurrentUser().getUid());
        mUsername.setValue(null);
        getCurrentUserUsername();
    }

    public LiveData<String> getUsername() {
        return mUsername;
    }

    private void getCurrentUserUsername(){
        FirestoreManager firebaseManager = FirestoreManager.getInstance();
        AuthenticationManager authenticationManager = AuthenticationManager.getInstance();
        if (authenticationManager.getCurrentUser() != null) {
            String currentUserId = authenticationManager.getCurrentUser().getUid();
            firebaseManager.getUserData(currentUserId, user -> {
                if (user != null) {
                    mUsername.setValue(user.getUsername());
                }
                else mUsername.setValue(null);
            });
        }
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<List<SurveyWithResponseCount>> getSurveys() {
        return surveysLiveData;
    }

    public LiveData<List<SurveyWithResponseCount>> getFakeSurveys() {
        return fakeSurveysLiveData;
    }

    public void startListeningFake() {
        // Dummy data
        List<SurveyWithResponseCount> fakeSurveys = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Survey survey = new Survey();
            survey.setID(UUID.randomUUID().toString());
            survey.setSurveyTitle("Survey Title " + i);
            survey.setDescription("This is description " + i);
            survey.setDueDate(new Date());
            survey.setStatus(i % 2 == 0 ? Survey.SurveyStatus.Draft : Survey.SurveyStatus.Published);
            fakeSurveys.add(new SurveyWithResponseCount(survey));
        }
        fakeSurveysLiveData.setValue(fakeSurveys);
    }

    public void startListening() {
        isLoadingLiveData.setValue(true);
        listenerRegistration = FirestoreManager.getInstance().listenToMyActiveSurveysWithResponseCount(mCurrentUserId.getValue(), new SurveysWithCountCallback() {
            @Override
            public void onSurveysLoaded(List<SurveyWithResponseCount> surveysWithCount) {
                currentSurveys = new ArrayList<>(surveysWithCount);
                surveysLiveData.setValue(currentSurveys);
                isLoadingLiveData.setValue(false);
            }

            @Override
            public void onSurveyCountUpdated(int position, int count) {
                if (position >= 0 && position < currentSurveys.size()) {
                    currentSurveys.get(position).setResponseCount(count);
                    List<SurveyWithResponseCount> updatedList = new ArrayList<>(currentSurveys);
                    surveysLiveData.setValue(updatedList);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("pttt", "Failed to load active surveys: " + e.getMessage());
                isLoadingLiveData.setValue(false);
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