package com.example.questionnairebuilder.utilities;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.questionnairebuilder.interfaces.SurveyResponsesStatusCallback;
import com.example.questionnairebuilder.interfaces.SurveysWithCountCallback;
import com.example.questionnairebuilder.interfaces.UpdateSurveyDetailsCallback;
import com.example.questionnairebuilder.listeners.OnCountListener;
import com.example.questionnairebuilder.interfaces.AllResponsesCallback;
import com.example.questionnairebuilder.interfaces.OnQuestionDeleteCallback;
import com.example.questionnairebuilder.interfaces.OneResponseCallback;
import com.example.questionnairebuilder.interfaces.OneQuestionCallback;
import com.example.questionnairebuilder.interfaces.OneSurveyCallback;
import com.example.questionnairebuilder.interfaces.QuestionsCallback;
import com.example.questionnairebuilder.interfaces.ResponsesCallback;
import com.example.questionnairebuilder.interfaces.SurveysCallback;
import com.example.questionnairebuilder.listeners.OnImageUploadListener;
import com.example.questionnairebuilder.listeners.OnSurveyResponseStatusListener;
import com.example.questionnairebuilder.listeners.OnUserFetchListener;
import com.example.questionnairebuilder.listeners.OnUserSaveListener;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.example.questionnairebuilder.models.Response;
import com.example.questionnairebuilder.models.SingleChoiceQuestion;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.SurveyResponseStatus;
import com.example.questionnairebuilder.models.SurveyWithResponseCount;
import com.example.questionnairebuilder.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;
import java.util.Map;

public class FirestoreManager {
    private static FirestoreManager instance;
    private final CollectionReference surveysRef;
    private final CollectionReference questionsRef;
    private final CollectionReference usersRef;
    private final CollectionReference responsesRef;
    private CollectionReference surveyResponseStatusRef;

    private FirestoreManager() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        surveysRef = database.collection("Surveys");
        questionsRef = database.collection("Questions");
        usersRef = database.collection("Users");
        responsesRef = database.collection("Responses");
        surveyResponseStatusRef = database.collection("SurveyResponseStatus");
    }

    public static synchronized FirestoreManager getInstance() {
        if (instance == null)
            instance = new FirestoreManager();
        return instance;
    }

    public void addSurvey(Survey survey) {
        surveysRef.document(survey.getID()).set(survey);
    }

    public void updateSurvey(String surveyId, Map<String, Object> updates, UpdateSurveyDetailsCallback callback) {
        surveysRef.document(surveyId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    // After update, fetch the updated survey document
                    surveysRef.document(surveyId).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Survey updatedSurvey = documentSnapshot.toObject(Survey.class);
                                    if (updatedSurvey != null) {
                                        updatedSurvey.setID(documentSnapshot.getId()); // set ID manually
                                        callback.onSuccess(updatedSurvey);
                                    } else {
                                        callback.onFailure(new Exception("Survey is null"));
                                    }
                                    Log.d("pttt FirestoreManager", "Survey status updated and fetched.");
                                    callback.onSuccess(updatedSurvey);
                                } else {
                                    if (callback != null)
                                        callback.onFailure(new Exception("Survey not found after update."));
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("pttt FirestoreManager", "Failed to fetch updated survey: ", e);
                                if (callback != null) callback.onFailure(e);
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e("pttt FirestoreManager", "Failed to update survey status: ", e);
                    if (callback != null) callback.onFailure(e);
                });
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

    public ListenerRegistration listenToMyActiveSurveys(String currentUserId, SurveysCallback callback) {
        return surveysRef.whereEqualTo("author.uid", currentUserId)
                .where(Filter.or(
                        Filter.equalTo("status",Survey.SurveyStatus.Draft),
                        Filter.equalTo("status",Survey.SurveyStatus.Published)
                )).orderBy("dueDate")
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

    // Combined method in your FirestoreManager
    public ListenerRegistration listenToMyActiveSurveysWithResponseCount(String currentUserId, SurveysWithCountCallback callback) {
        return surveysRef.whereEqualTo("author.uid", currentUserId)
                .where(Filter.or(
                        Filter.equalTo("status",Survey.SurveyStatus.Draft),
                        Filter.equalTo("status",Survey.SurveyStatus.Published)
                )).orderBy("dueDate")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    if (value != null) {
                        List<SurveyWithResponseCount> surveyList = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Survey survey = document.toObject(Survey.class);
                            if (survey != null) {
                                survey.setID(document.getId());
                                surveyList.add(new SurveyWithResponseCount(survey));
                            }
                        }

                        // First callback with surveys (response counts still loading)
                        callback.onSurveysLoaded(surveyList);

                        // Then fetch response counts for each survey
                        fetchResponseCountsForSurveys(surveyList, callback);
                    }
                });
    }

    private void fetchResponseCountsForSurveys(List<SurveyWithResponseCount> surveysWithCount, SurveysWithCountCallback callback) {
        for (int i = 0; i < surveysWithCount.size(); i++) {
            final int position = i;
            SurveyWithResponseCount surveyWithCount = surveysWithCount.get(i);
            String surveyId = surveyWithCount.getSurvey().getID();

            getSurveyResponseStatusCount(
                    surveyId,
                    Arrays.asList(SurveyResponseStatus.ResponseStatus.IN_PROGRESS, SurveyResponseStatus.ResponseStatus.COMPLETED),
                    new OnCountListener() {
                        @Override
                        public void onCountSuccess(int count) {
                            surveyWithCount.setResponseCount(count);
                            callback.onSurveyCountUpdated(position, count);
                        }

                        @Override
                        public void onCountFailure(Exception e) {
                            surveyWithCount.setResponseCount(0);
                            callback.onSurveyCountUpdated(position, 0);
                        }
                    }
            );
        }
    }

    public ListenerRegistration listenToAllActiveSurveys(SurveysCallback callback) {
        return surveysRef.where(Filter.or(
                        Filter.equalTo("status",Survey.SurveyStatus.Draft),
                        Filter.equalTo("status",Survey.SurveyStatus.Published)
                )).orderBy("dueDate")
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

    public ListenerRegistration listenToMySurveys(String currentUserId, SurveysCallback callback) {
        return surveysRef.whereEqualTo("author.uid", currentUserId)
                .orderBy("dueDate")
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

    public ListenerRegistration listenToMySurveysWithResponseCount(String currentUserId, SurveysWithCountCallback callback) {
        return surveysRef.whereEqualTo("author.uid", currentUserId)
                .orderBy("dueDate")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    if (value != null) {
                        List<SurveyWithResponseCount> surveyList = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Survey survey = document.toObject(Survey.class);
                            if (survey != null) {
                                survey.setID(document.getId());
                                surveyList.add(new SurveyWithResponseCount(survey));
                            }
                        }
                        callback.onSurveysLoaded(surveyList);
                        fetchResponseCountsForSurveys(surveyList, callback);
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
                .addOnFailureListener(callback::onError);
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
                            Question question = mapToQuestion(document);
                            if (question != null) {
                                questionList.add(question);
                            }
                        }
                        callback.onQuestionsLoaded(questionList);
                    }
                });
    }

    public void getSurveyQuestionsOnce(String surveyID, QuestionsCallback callback) {
        Log.d("pttt", "Fetching questions once for surveyID: " + surveyID);

        questionsRef.whereEqualTo("surveyID", surveyID)
                .orderBy("order")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("pttt", "Query successful. Documents found: " + queryDocumentSnapshots.size());

                    List<Question> questions = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Log.d("pttt", "Processing document: " + doc.getId());
                        try {
                            Question question = mapToQuestion(doc);
                            if (question != null) {
                                questions.add(question);
                            } else {
                                Log.w("pttt", "mapToQuestion returned null for document: " + doc.getId());
                            }
                        } catch (Exception e) {
                            Log.e("pttt", "Error parsing document: " + e.getMessage());
                        }
                    }
                    Log.d("pttt", "Returning " + questions.size() + " parsed questions.");
                    callback.onQuestionsLoaded(questions);
                })
                .addOnFailureListener(e -> {
                    Log.e("pttt", "Firestore query failed: " + e.getMessage(), e);
                    callback.onError(e);
                });
    }

    private Question mapToQuestion(DocumentSnapshot document) {
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
        }
        return question;
    }

    public void deleteQuestion(Question question, OnQuestionDeleteCallback callback){
        questionsRef.document(question.getQuestionID())
                .delete()
                .addOnSuccessListener(aVoid -> callback.onDelete())
                .addOnFailureListener(e -> callback.onError(e));
    }

    public void getQuestionById(String questionId, OneQuestionCallback callback) {
        questionsRef.document(questionId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Question question = mapToQuestion(documentSnapshot);
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
                .addOnFailureListener(callback::onError);
    }

    public void fixQuestionOrder(String surveyID) {
        questionsRef
                .whereEqualTo("surveyID", surveyID)
                .orderBy("order")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int index = 1;
                    WriteBatch batch = FirebaseFirestore.getInstance().batch();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        DocumentReference ref = doc.getReference();
                        batch.update(ref, "order", index++);
                    }

                    batch.commit()
                            .addOnSuccessListener(unused -> Log.d("pttt", "Order fixed after deletion."))
                            .addOnFailureListener(e -> Log.e("pttt", "Failed to fix order: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("pttt", "Failed to load questions for reordering: " + e.getMessage()));
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
/*
    /**
     * Create or update a SurveyResponseStatus document for a user-survey pair
     * @param status survey's response status item to create or update
     *  @param onSuccess callback on success
     *  @param onFailure callback on failure
     */
   /* public void addSurveyResponseStatus(SurveyResponseStatus status, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        String docId = status.getSurveyId() + "_" + status.getUserId();
        surveyResponseStatusRef.document(docId)
                .set(status)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }*/

    public Task<Void> createSurveyResponseStatus(SurveyResponseStatus status) {
        String docId = status.getSurveyId() + "_" + status.getUserId();
        return surveyResponseStatusRef.document(docId).set(status);
    }


    /**
     * Update status field only (e.g. to mark completed)
     * @param surveyId unique identifier of the survey
     * @param userId unique identifier of the user
     * @param newStatus new response status
     * @param onSuccess callback on success
     * @param onFailure callback on failure
     */
    public void updateSurveyResponseStatus(String surveyId, String userId, SurveyResponseStatus.ResponseStatus newStatus, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        String docId = surveyId + "_" + userId;
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        if (newStatus == SurveyResponseStatus.ResponseStatus.COMPLETED) {
            updates.put("completedAt", new Date());
        }
        if (newStatus == SurveyResponseStatus.ResponseStatus.IN_PROGRESS) {
            updates.put("startedAt", new Date());
        }

        surveyResponseStatusRef.document(docId)
                .update(updates)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getSurveyResponseStatus(String surveyId, String userId, OnSurveyResponseStatusListener listener) {
        String docId = surveyId + "_" + userId;
        surveyResponseStatusRef.document(docId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        SurveyResponseStatus status = documentSnapshot.toObject(SurveyResponseStatus.class);
                        listener.onSuccess(status);
                    } else {
                        listener.onFailure(new Exception("No status found for surveyId: " + surveyId + " and userId: " + userId));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void getSurveyResponseStatusCount(String surveyId, List<SurveyResponseStatus.ResponseStatus> statuses, OnCountListener listener) {
        Query query = surveyResponseStatusRef.whereEqualTo("surveyId", surveyId);

        if (statuses != null && !statuses.isEmpty()) {
            // Convert enum list to list of strings
            List<String> statusStrings = new ArrayList<>();
            for (SurveyResponseStatus.ResponseStatus status : statuses) {
                statusStrings.add(status.name());
            }
            query = query.whereIn("status", statusStrings);
        }

        query.count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(aggregateQuerySnapshot -> {
                    int count = (int) aggregateQuerySnapshot.getCount();
                    listener.onCountSuccess(count);
                })
                .addOnFailureListener(listener::onCountFailure);
    }

    public void addResponse(Response response) {
        responsesRef.document(response.getResponseID()).set(response);
    }

    public void getResponse(String surveyID, String questionID, String userID, OneResponseCallback callback) {
        responsesRef.whereEqualTo("surveyID", surveyID)
                .whereEqualTo("questionID", questionID)
                .whereEqualTo("userID", userID)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                callback.onResponseLoad(null);
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Response response = document.toObject(Response.class);
                                    callback.onResponseLoad(response);
                                }
                            }
                        } else {
                            callback.onResponseLoadFailure();
                        }
                    }
                });
    }

    public void getUserResponsesForSurvey(String surveyId, String userId, ResponsesCallback callback) {
        responsesRef.whereEqualTo("surveyID", surveyId)
                .whereEqualTo("userID", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Boolean> answeredQuestions = new HashMap<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String questionID = doc.getString("questionID");
                        Boolean isMandatory = doc.getBoolean("mandatory");
                        if (questionID != null && isMandatory != null) {
                            answeredQuestions.put(questionID, isMandatory);
                        }
                    }
                    callback.onResponsesLoaded(answeredQuestions);
                })
                .addOnFailureListener(callback::onError);
    }


    public void countSurveysQuestions(String surveyID, OnCountListener callback){
        Query query = questionsRef.whereEqualTo("surveyID", surveyID);
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER)
                .addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Count fetched successfully
                            AggregateQuerySnapshot snapshot = task.getResult();
                            callback.onCountSuccess((int) snapshot.getCount());
                            Log.d("pttt", "Count: " + snapshot.getCount());
                        } else {
                            callback.onCountFailure(new Exception("Could not fetch questions count"));
                            Log.d("pttt", "Count failed: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(callback::onCountFailure);
      }

    public void getAllResponsesForSurvey(String surveyId, AllResponsesCallback callback) {
        responsesRef.whereEqualTo("surveyID", surveyId)
                .get()
                .addOnSuccessListener(snapshot -> callback.onResponsesLoaded(snapshot.getDocuments()))
                .addOnFailureListener(callback::onError);

    }

    public ListenerRegistration listenToSurveyResponseStatuses(String userId, SurveyResponsesStatusCallback callback) {
        return surveyResponseStatusRef.whereEqualTo("userId", userId)
                .orderBy("startedAt", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        callback.onError(e);
                        return;
                    }
                    List<SurveyResponseStatus> list = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        SurveyResponseStatus srs = doc.toObject(SurveyResponseStatus.class);
                        list.add(srs);
                    }
                    callback.onResponseStatusesLoaded(list);
                });
    }

}