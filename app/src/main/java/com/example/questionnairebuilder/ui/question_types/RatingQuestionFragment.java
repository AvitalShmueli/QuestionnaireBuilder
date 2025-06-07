package com.example.questionnairebuilder.ui.question_types;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.questionnairebuilder.EditQuestionActivity;
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.adapters.IconsAdapter;
import com.example.questionnairebuilder.databinding.FragmentRatingQuestionBinding;
import com.example.questionnairebuilder.interfaces.SaveHandler;
import com.example.questionnairebuilder.interfaces.UnsavedChangesHandler;
import com.example.questionnairebuilder.models.IconItem;
import com.example.questionnairebuilder.utilities.RatingDrawableManager;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.UUID;


public class RatingQuestionFragment extends Fragment implements UnsavedChangesHandler, SaveHandler {

    private FragmentRatingQuestionBinding binding;
    private TextInputLayout ratingQuestion_TIL_question;
    private TextInputEditText ratingQuestion_TXT_question;
    private MaterialSwitch ratingQuestion_SW_mandatory;
    private MaterialButton ratingQuestion_BTN_save;
    private MaterialButton ratingQuestion_BTN_cancel;
    private MaterialButton ratingQuestion_BTN_delete;
    private AutoCompleteTextView ratingQuestion_DD_RatingScaleLevel;
    private AutoCompleteTextView ratingQuestion_DD_RatingScaleIcon;
    private ShapeableImageView ratingQuestion_IMG_selectedIcon;
    private NestedScrollView scrollView;
    private View scrollHintBottom;
    private View scrollHintTop;
    private Integer selectedRatingScaleLevel = 5;
    private String selectedRatingScaleIcon = null;
    private String surveyID;
    private RatingScaleQuestion question;
    private int currentQuestionOrder;

    public RatingQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionArgs bundle of question's details.
     * @return A new instance of fragment DateQuestionFragment.
     */
    public static RatingQuestionFragment newInstance(Bundle questionArgs) {
        RatingQuestionFragment fragment = new RatingQuestionFragment();
        fragment.setArguments(questionArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            surveyID = args.getString("surveyID");
            currentQuestionOrder = args.getInt("order");
            if (args.getString("questionID") != null) { // edit question
                question = (RatingScaleQuestion) new RatingScaleQuestion(args.getString("questionTitle"))
                        .setIconResourceName(args.getString("iconResourceName"))
                        .setRatingScaleLevel(args.getInt("ratingScaleLevel"))
                        .setMandatory(args.getBoolean("mandatory"))
                        .setQuestionID(args.getString("questionID"))
                        .setSurveyID(surveyID)
                        .setOrder(args.getInt("order"));
            }
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (hasUnsavedChanges()) {
                            // Call the dialog from the activity
                            ((EditQuestionActivity) requireActivity()).showCancelConfirmationDialog();
                        } else {
                            requireActivity().finish();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRatingQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();
        initView();
        loadQuestionDetails(question);

        return root;
    }

    private void createBinding() {
        scrollView = binding.scrollView;
        scrollHintBottom = binding.scrollHintBottom;
        scrollHintTop = binding.scrollHintTop;
        ratingQuestion_BTN_save = binding.ratingQuestionBTNSave;
        ratingQuestion_BTN_cancel = binding.ratingQuestionBTNCancel;
        ratingQuestion_BTN_delete = binding.ratingQuestionBTNDelete;
        ratingQuestion_TIL_question = binding.ratingQuestionTILQuestion;
        ratingQuestion_TXT_question = binding.ratingQuestionTXTQuestion;
        ratingQuestion_SW_mandatory = binding.ratingQuestionSWMandatory;
        ratingQuestion_DD_RatingScaleLevel = binding.ratingQuestionDDRatingScaleLevel;
        ratingQuestion_DD_RatingScaleIcon = binding.ratingQuestionDDRatingScaleIcon;
        ratingQuestion_IMG_selectedIcon = binding.ratingQuestionIMGSelectedIcon;
    }

    private void initView(){
        initDropDownValues();// Rating Scale Level dropdown
        initIconsDropDownValues(); // Rating Scale Level dropdown
        ratingQuestion_BTN_cancel.setOnClickListener(v -> cancel());
        ratingQuestion_BTN_save.setOnClickListener(v -> save());
        if(question != null) {
            ratingQuestion_BTN_delete.setVisibility(VISIBLE);
            ratingQuestion_BTN_delete.setOnClickListener(v -> delete());
        }
        else  ratingQuestion_BTN_delete.setVisibility(GONE);

        initScrollHint();
    }

    private void initScrollHint(){
        // Hide the gradient when scrolled to bottom
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View child = scrollView.getChildAt(0);

                if (child != null) {
                    int scrollViewHeight = scrollView.getHeight();
                    int contentHeight = child.getHeight();

                    boolean atTop = scrollView.getScrollY() == 0;
                    boolean atBottom = (scrollView.getScrollY() + scrollViewHeight) >= (contentHeight - 1); // tolerance

                    scrollHintTop.setVisibility(atTop ? View.GONE : View.VISIBLE);
                    scrollHintBottom.setVisibility(atBottom ? View.GONE : View.VISIBLE);
                }
            }
        });

        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            View child = scrollView.getChildAt(0);
            if (child != null) {
                boolean canScroll = (scrollView.getScrollY() + scrollView.getHeight()) < (child.getHeight() - 1);
                scrollHintBottom.setVisibility(canScroll ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void initDropDownValues() {
        ArrayList<Integer> itemsRatingScaleLevel = new ArrayList<>();
        for(int i = 1 ; i <= 5 ; i++)
            itemsRatingScaleLevel.add(i);

        String strRatingScaleLevel = selectedRatingScaleLevel.toString();
        ratingQuestion_DD_RatingScaleLevel.setText(strRatingScaleLevel);
        ArrayAdapter<Integer> adapterItems_RatingScaleLevel = new ArrayAdapter<>(requireActivity(), R.layout.item_dropdown, itemsRatingScaleLevel);
        ratingQuestion_DD_RatingScaleLevel.setAdapter(adapterItems_RatingScaleLevel);
        ratingQuestion_DD_RatingScaleLevel.setOnItemClickListener((adapterView, view, position, id) -> selectedRatingScaleLevel = adapterItems_RatingScaleLevel.getItem(position));
    }

    private void initIconsDropDownValues() {
        IconItem[] iconItems = {
                new IconItem("ic_heart_filled"),
                new IconItem("ic_star_filled"),
                new IconItem("ic_thumb_up_filled")
        };

        IconsAdapter adapter = new IconsAdapter(requireContext(), iconItems);
        ratingQuestion_DD_RatingScaleIcon.setAdapter(adapter);

        if(selectedRatingScaleIcon == null) {
            selectedRatingScaleIcon = "ic_star_filled";
        }

        RatingDrawableManager.init();
        ratingQuestion_IMG_selectedIcon.setImageResource(RatingDrawableManager.getValueByKey(selectedRatingScaleIcon));

        ratingQuestion_DD_RatingScaleIcon.setOnItemClickListener((parent, view, position, id) -> {
            IconItem selected = (IconItem) parent.getItemAtPosition(position);
            selectedRatingScaleIcon = selected.iconResName;
            ratingQuestion_IMG_selectedIcon.setImageResource(RatingDrawableManager.getValueByKey(selectedRatingScaleIcon));
        });

    }

    private void loadQuestionDetails(RatingScaleQuestion q){
        if(q == null) {
            return;
        }

        ratingQuestion_TXT_question.setText(q.getQuestionTitle());
        ratingQuestion_SW_mandatory.setChecked(q.isMandatory());
        selectedRatingScaleLevel = q.getRatingScaleLevel();
        selectedRatingScaleIcon = q.getIconResourceName();

        initDropDownValues();// Rating Scale Level dropdown
        initIconsDropDownValues(); // Rating Scale Level dropdown
    }

    private void save() {
        String questionTitle = null;
        if (!isValid())
            ratingQuestion_TIL_question.setError(getString(R.string.error_required));
        else {
            ratingQuestion_TIL_question.setError(null);
            if(ratingQuestion_TXT_question.getText() != null)
                questionTitle = ratingQuestion_TXT_question.getText().toString().trim();
            boolean mandatory = ratingQuestion_SW_mandatory.isChecked();
            if(question == null) {
                question = (RatingScaleQuestion) new RatingScaleQuestion(questionTitle)
                        .setRatingScaleLevel(selectedRatingScaleLevel)
                        .setIconResourceName(selectedRatingScaleIcon)
                        .setQuestionID(UUID.randomUUID().toString())
                        .setSurveyID(surveyID)
                        .setMandatory(mandatory)
                        .setOrder(currentQuestionOrder);
            }
            else {
                question.setRatingScaleLevel(selectedRatingScaleLevel)
                        .setIconResourceName(selectedRatingScaleIcon)
                        .setQuestionTitle(questionTitle)
                        .setMandatory(mandatory);
            }
            question.save();
            requireActivity().finish();
        }
    }

    private boolean isValid(){
        return ratingQuestion_TXT_question.getText() != null &&
                !ratingQuestion_TXT_question.getText().toString().trim().isEmpty();
    }

    private void cancel(){
        if(hasUnsavedChanges())
            ((EditQuestionActivity) requireActivity()).showCancelConfirmationDialog();
        else
            requireActivity().finish();
    }

    private void delete(){
        ((EditQuestionActivity) requireActivity()).showDeleteConfirmationDialog(question);
    }

    @Override
    public boolean hasUnsavedChanges() {
        // TODO: compare to stored question
        /*return ratingQuestion_TXT_question.getText() != null &&
                !ratingQuestion_TXT_question.getText().toString().trim().isEmpty();*/

        String currentText = ratingQuestion_TXT_question.getText() != null
                ? ratingQuestion_TXT_question.getText().toString().trim()
                : "";
        boolean currentMandatory = ratingQuestion_SW_mandatory.isChecked();
        Integer currentRatingScaleLevel = selectedRatingScaleLevel;
        String currentIconResourceName = selectedRatingScaleIcon;

        String originalText = question != null ? question.getQuestionTitle() : "";
        boolean originalMandatory = question != null && question.isMandatory();
        Integer originalRatingScaleLevel = question != null ? question.getRatingScaleLevel() : null;
        String originalIconResourceName = question != null ? question.getIconResourceName() : null;

        boolean textChanged = !currentText.equals(originalText);
        boolean mandatoryChanged = currentMandatory != originalMandatory;
        boolean ratingScaleLevelChanged = !currentRatingScaleLevel.equals(originalRatingScaleLevel);
        boolean iconResourceChanged = !currentIconResourceName.equals(originalIconResourceName);

        return textChanged || mandatoryChanged || ratingScaleLevelChanged || iconResourceChanged;
    }

    @Override
    public void onSaveClicked() {
        save();
    }
}