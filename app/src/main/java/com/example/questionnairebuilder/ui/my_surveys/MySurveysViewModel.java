package com.example.questionnairebuilder.ui.my_surveys;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MySurveysViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MySurveysViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my surveys fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}