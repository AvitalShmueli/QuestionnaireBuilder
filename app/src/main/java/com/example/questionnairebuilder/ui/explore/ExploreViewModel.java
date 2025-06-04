package com.example.questionnairebuilder.ui.explore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.questionnairebuilder.interfaces.OneSurveyCallback;
import com.example.questionnairebuilder.interfaces.SurveyResponsesStatusCallback;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.SurveyResponseStatus;
import com.example.questionnairebuilder.models.SurveyWithResponseStatus;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreViewModel extends ViewModel {

    private final MutableLiveData<List<SurveyWithResponseStatus>> surveysWithResponsesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<SurveyWithResponseStatus>> filteredSurveysLiveData = new MutableLiveData<>();
    private ListenerRegistration listenerRegistration;
    private String statusFilter = SurveyResponseStatus.ResponseStatus.PENDING.name();

    public LiveData<List<SurveyWithResponseStatus>> getSurveysWithResponses() {
        return surveysWithResponsesLiveData;
    }

    public LiveData<List<SurveyWithResponseStatus>> getFilteredSurveys() {
        return filteredSurveysLiveData;
    }

    public void setStatusFilter(String status) {
        this.statusFilter = status;
        applyFilter();
    }

    public void startListening() {
        String currentUserId = AuthenticationManager.getInstance().getCurrentUser().getUid();
        listenerRegistration = FirestoreManager.getInstance().listenToSurveyResponseStatuses(currentUserId, new SurveyResponsesStatusCallback() {
            @Override
            public void onResponseStatusesLoaded(List<SurveyResponseStatus> responseStatuses) {
                // Now we have responseStatuses for the current user. Fetch Surveys for these.
                if (responseStatuses.isEmpty()) {
                    surveysWithResponsesLiveData.setValue(new ArrayList<>()); // No data
                    return;
                }

                List<SurveyWithResponseStatus> combinedList = new ArrayList<>(Collections.nCopies(responseStatuses.size(), null));

                // Counter to check when all survey fetches are done
                final int[] counter = {0};
                for (int i = 0; i < responseStatuses.size(); i++) {
                    final int index = i;
                    SurveyResponseStatus responseStatus = responseStatuses.get(i);

                    FirestoreManager.getInstance().getSurveyById(responseStatus.getSurveyId(), new OneSurveyCallback() {
                        @Override
                        public void onSurveyLoaded(Survey survey) {
                            if (survey != null) {
                                combinedList.set(index, new SurveyWithResponseStatus(survey, responseStatus));
                            }
                            counter[0]++;
                            if (counter[0] == responseStatuses.size()) {
                                // Filter out any nulls (in case some surveys were not found)
                                List<SurveyWithResponseStatus> finalList = new ArrayList<>();
                                for (SurveyWithResponseStatus item : combinedList) {
                                    if (item != null) finalList.add(item);
                                }
                                surveysWithResponsesLiveData.setValue(finalList);
                                applyFilter();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            counter[0]++;
                            if (counter[0] == responseStatuses.size()) {
                                List<SurveyWithResponseStatus> finalList = new ArrayList<>();
                                for (SurveyWithResponseStatus item : combinedList) {
                                    if (item != null) finalList.add(item);
                                }
                                surveysWithResponsesLiveData.setValue(finalList);
                                applyFilter();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // Handle Firestore error
                surveysWithResponsesLiveData.setValue(new ArrayList<>());
            }
        });
    }

    private void applyFilter() {
        if (surveysWithResponsesLiveData.getValue() == null) return;
        List<SurveyWithResponseStatus> allSurveys = surveysWithResponsesLiveData.getValue();

        List<SurveyWithResponseStatus> filtered = new ArrayList<>();
        for (SurveyWithResponseStatus item : allSurveys) {
            if (item.getResponseStatus() != null && item.getResponseStatus().getStatus() != null) {
                String status = item.getResponseStatus().getStatus().toString();

                if (statusFilter.equals(SurveyResponseStatus.ResponseStatus.PENDING.toString())) {
                    // Show PENDING and IN_PROGRESS when "Pending" tab is selected
                    if (status.equals(SurveyResponseStatus.ResponseStatus.PENDING.toString()) ||
                            status.equals(SurveyResponseStatus.ResponseStatus.IN_PROGRESS.toString())) {
                        filtered.add(item);
                    }
                } else {
                    // Normal filtering for other tabs
                    if (status.equals(statusFilter)) {
                        filtered.add(item);
                    }
                }
            }
        }
        filteredSurveysLiveData.setValue(filtered);
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