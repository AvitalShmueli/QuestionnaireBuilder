package com.example.questionnairebuilder.interfaces;

import com.example.questionnairebuilder.models.Response;

public interface OneResponseCallback {
    void onResponseLoad(Response response);
    void onResponseLoadFailure();
}
