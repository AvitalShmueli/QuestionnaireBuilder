package com.example.questionnairebuilder.ui.question_types;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.adapters.IconsAdapter;
import com.example.questionnairebuilder.databinding.FragmentRatingQuestionBinding;
import com.example.questionnairebuilder.models.IconItem;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;


public class RatingQuestionFragment extends Fragment {

    private FragmentRatingQuestionBinding binding;
    private TextInputLayout ratingQuestion_TIL_question;
    private TextInputEditText ratingQuestion_TXT_question;
    private MaterialSwitch ratingQuestion_SW_mandatory;
    private MaterialButton ratingQuestion_BTN_save;
    private MaterialButton ratingQuestion_BTN_cancel;
    private AutoCompleteTextView ratingQuestion_DD_RatingScaleLevel;
    private AutoCompleteTextView ratingQuestion_DD_RatingScaleIcon;
    private ShapeableImageView ratingQuestion_IMG_selectedIcon;
    private Integer selectedRatingScaleLevel = 5;
    private Integer selectedRatingScaleIcon = null;

    public RatingQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRatingQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();

        return root;
    }

    private void createBinding() {
        ratingQuestion_BTN_save = binding.ratingQuestionBTNSave;
        ratingQuestion_BTN_cancel = binding.ratingQuestionBTNCancel;
        ratingQuestion_TIL_question = binding.ratingQuestionTILQuestion;
        ratingQuestion_TXT_question = binding.ratingQuestionTXTQuestion;
        ratingQuestion_SW_mandatory = binding.ratingQuestionSWMandatory;

        // Rating Scale Level dropdown
        ratingQuestion_DD_RatingScaleLevel = binding.ratingQuestionDDRatingScaleLevel;
        initDropDownValues();

        // Rating Scale Level dropdown
        ratingQuestion_DD_RatingScaleIcon = binding.ratingQuestionDDRatingScaleIcon;
        ratingQuestion_IMG_selectedIcon = binding.ratingQuestionIMGSelectedIcon;
        initIconsDropDownValues();

        ratingQuestion_BTN_cancel.setOnClickListener(v -> requireActivity().finish());
        ratingQuestion_BTN_save.setOnClickListener(v -> save());
    }


    private void initDropDownValues() {
        ArrayList<Integer> itemsRatingScaleLevel = new ArrayList<>();
        for(int i = 1 ; i <= 5 ; i++)
            itemsRatingScaleLevel.add(i);

        String strRatingScaleLevel = selectedRatingScaleLevel.toString();
        ratingQuestion_DD_RatingScaleLevel.setText(strRatingScaleLevel);
        ArrayAdapter<Integer> adapterItems_RatingScaleLevel = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsRatingScaleLevel);
        ratingQuestion_DD_RatingScaleLevel.setAdapter(adapterItems_RatingScaleLevel);
        ratingQuestion_DD_RatingScaleLevel.setOnItemClickListener((adapterView, view, position, id) -> selectedRatingScaleLevel = adapterItems_RatingScaleLevel.getItem(position));
    }

    private void initIconsDropDownValues() {
        IconItem[] iconItems = {
                new IconItem(R.drawable.ic_heart),
                new IconItem(R.drawable.ic_star),
                new IconItem(R.drawable.ic_thumb_up)
        };

        IconsAdapter adapter = new IconsAdapter(requireContext(), iconItems);
        ratingQuestion_DD_RatingScaleIcon.setAdapter(adapter);

        if(selectedRatingScaleIcon == null) {
            selectedRatingScaleIcon = R.drawable.ic_star;
            ratingQuestion_IMG_selectedIcon.setImageResource(R.drawable.ic_star);
        }
        ratingQuestion_DD_RatingScaleIcon.setOnItemClickListener((parent, view, position, id) -> {
            IconItem selected = (IconItem) parent.getItemAtPosition(position);
            selectedRatingScaleIcon = selected.iconResId;
            ratingQuestion_IMG_selectedIcon.setImageResource(selectedRatingScaleIcon);
        });

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
            Question q = new RatingScaleQuestion(questionTitle)
                    .setRatingScaleLevel(selectedRatingScaleLevel)
                    .setIconResourceId(selectedRatingScaleIcon)
                    .setMandatory(mandatory);
            q.save();
        }
    }

    private boolean isValid(){
        return ratingQuestion_TXT_question.getText() != null &&
                !ratingQuestion_TXT_question.getText().toString().trim().isEmpty();
    }
}