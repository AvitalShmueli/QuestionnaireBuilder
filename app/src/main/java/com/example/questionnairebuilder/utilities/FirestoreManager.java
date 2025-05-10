package com.example.questionnairebuilder.utilities;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.questionnairebuilder.interfaces.OneQuestionCallback;
import com.example.questionnairebuilder.interfaces.OneSurveyCallback;
import com.example.questionnairebuilder.interfaces.QuestionsCallback;
import com.example.questionnairebuilder.interfaces.SurveysCallback;
import com.example.questionnairebuilder.listeners.OnImageUploadListener;
import com.example.questionnairebuilder.listeners.OnUserFetchListener;
import com.example.questionnairebuilder.listeners.OnUserSaveListener;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.example.questionnairebuilder.models.SingleChoiceQuestion;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import java.util.ArrayList;

public class FirestoreManager {
    private static FirestoreManager instance;
    private FirebaseFirestore database;
    private CollectionReference surveysRef;
    private CollectionReference questionsRef;
    private CollectionReference usersRef;

    private FirestoreManager() {
        database = FirebaseFirestore.getInstance();
        surveysRef = database.collection("Surveys");
        questionsRef = database.collection("Questions");
        usersRef = database.collection("Users");
    }

    public static synchronized FirestoreManager getInstance() {
        if (instance == null)
            instance = new FirestoreManager();
        return instance;
    }

    public void addSurvey(Survey survey) {
        surveysRef.document(survey.getID()).set(survey);
    }

    public ListenerRegistration listenToAllSurveys(SurveysCallback callback) {
        return surveysRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    callback.onError(e);
                    return;
                }

                List<Survey> surveyList = new ArrayList<>();
                if (snapshots != null) {
                    for (DocumentSnapshot document : snapshots) {
                        Survey survey = document.toObject(Survey.class);
                        if (survey != null) {
                            survey.setID(document.getId());
                            surveyList.add(survey);
                        }
                    }
                }
                callback.onSurveysLoaded(surveyList);
            }
        });
    }

    public ListenerRegistration listenToMySurveys(String currentUserId, SurveysCallback callback) {
        return surveysRef.whereEqualTo("author.uid", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    if (value != null) {
                        List<Survey> surveyList = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Survey survey = document.toObject(Survey.class);
                            if (survey != null) {
                                survey.setID(document.getId());
                                surveyList.add(survey);
                            }
                        }
                        callback.onSurveysLoaded(surveyList);
                    }
                });
    }

    public void getSurveyById(String surveyId, OneSurveyCallback callback) {
        surveysRef.document(surveyId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Survey survey = documentSnapshot.toObject(Survey.class);
                        if (survey != null) {
                            survey.setID(documentSnapshot.getId()); // set ID manually
                            callback.onSurveyLoaded(survey);
                        } else {
                            callback.onError(new Exception("Survey is null"));
                        }
                    } else {
                        callback.onError(new Exception("Survey not found"));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));
    }

    public void addQuestion(Question question) {
        questionsRef.document(question.getQuestionID()).set(question);
    }

    public ListenerRegistration listenToSurveyQuestions(String surveyID, QuestionsCallback callback) {
        return questionsRef.whereEqualTo("surveyID", surveyID)
                .orderBy("order")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    if (value != null) {
                        List<Question> questionList = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Log.d("FirestoreDoc", "Document: " + document.getData());
                            Object mandatoryRaw = document.get("mandatory");
                            if (mandatoryRaw != null) {
                                Log.d("FirestoreMandatory", "mandatoryRaw: " + mandatoryRaw + " (type = " + mandatoryRaw.getClass().getSimpleName() + ")");
                            }
                            String typeString = document.getString("type");
                            QuestionTypeEnum type = QuestionTypeEnum.valueOf(typeString);
                            Question question = null;
                            switch (type) {
                                case OPEN_ENDED_QUESTION:
                                    question = document.toObject(OpenEndedQuestion.class);
                                    break;
                                case SINGLE_CHOICE:
                                case YES_NO:
                                case DROPDOWN:
                                    question = document.toObject(SingleChoiceQuestion.class);
                                    break;
                                case MULTIPLE_CHOICE:
                                    question = document.toObject(MultipleChoiceQuestion.class);
                                    break;
                                case DATE:
                                    question = document.toObject(DateQuestion.class);
                                    break;
                                case RATING_SCALE:
                                    question = document.toObject(RatingScaleQuestion.class);
                                    break;
                            }
                            if (question != null) {
                                question.setQuestionID(document.getId());
                                Boolean mandatory = document.getBoolean("mandatory");
                                if (mandatory != null) {
                                    question.setMandatory(mandatory);
                                }
                                questionList.add(question);
                            }
                        }
                        callback.onQuestionsLoaded(questionList);
                    }

                });
    }

    public void getQuestionById(String questionId, OneQuestionCallback callback) {
        questionsRef.document(questionId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String typeString = documentSnapshot.getString("type");
                        QuestionTypeEnum type = QuestionTypeEnum.valueOf(typeString);
                        Question question = null;
                        switch (type) {
                            case OPEN_ENDED_QUESTION:
                                question = documentSnapshot.toObject(OpenEndedQuestion.class);
                                break;
                            case SINGLE_CHOICE:
                            case YES_NO:
                            case DROPDOWN:
                                question = documentSnapshot.toObject(SingleChoiceQuestion.class);
                                break;
                            case MULTIPLE_CHOICE:
                                question = documentSnapshot.toObject(MultipleChoiceQuestion.class);
                                break;
                            case DATE:
                                question = documentSnapshot.toObject(DateQuestion.class);
                                break;
                            case RATING_SCALE:
                                question = documentSnapshot.toObject(RatingScaleQuestion.class);
                                break;
                        }
                        if (question != null) {
                            question.setQuestionID(documentSnapshot.getId()); // set ID manually
                            callback.onQuestionLoaded(question);
                        } else {
                            callback.onError(new Exception("Question is null"));
                        }
                    } else {
                        callback.onError(new Exception("Question not found"));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));
    }

    public void uploadUserProfileImage(String uid, Uri imageUri, OnImageUploadListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("ProfileImages").child(uid + ".jpg");
        storageRef.putFile(imageUri)
                .continueWithTask(task -> storageRef.getDownloadUrl())
                .addOnSuccessListener(uri -> listener.onUploaded(uri.toString()));
    }

    public void saveUser(User user, OnUserSaveListener listener) {
        usersRef.document(user.getUid())
                .set(user)
                .addOnCompleteListener(task -> listener.onSaved(task.isSuccessful()));
    }

    public void getUserData(String uid, OnUserFetchListener listener) {
        usersRef.document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        listener.onFetched(user);
                    } else {
                        listener.onFetched(null);
                    }
                })
                .addOnFailureListener(e -> listener.onFetched(null));
    }
}