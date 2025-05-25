package com.example.questionnairebuilder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.interfaces.AnalyzableQuestion;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyzeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_OPEN_ENDED = 0;
    private static final int TYPE_SINGLE_CHOICE = 1;
    private static final int TYPE_MULTIPLE_CHOICE = 2;
    private static final int TYPE_DROPDOWN = 3;
    private static final int TYPE_YES_NO = 4;
    private static final int TYPE_RATING_SCALE = 5;

    private List<Question> questionList;

    public AnalyzeAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    @Override
    public int getItemViewType(int position) {
        QuestionTypeEnum type = questionList.get(position).getType();
        switch (type) {
            case OPEN_ENDED_QUESTION:
                return TYPE_OPEN_ENDED;
            case SINGLE_CHOICE:
                return TYPE_SINGLE_CHOICE;
            case MULTIPLE_CHOICE:
                return TYPE_MULTIPLE_CHOICE;
            case DROPDOWN:
                return TYPE_DROPDOWN;
            case YES_NO:
                return TYPE_YES_NO;
            case RATING_SCALE:
                return TYPE_RATING_SCALE;
            default:
                return TYPE_SINGLE_CHOICE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_OPEN_ENDED:
                return new OpenEndedViewHolder(inflater.inflate(R.layout.item_analysis_open, parent, false));
            case TYPE_SINGLE_CHOICE:
                return new PieChartViewHolder(inflater.inflate(R.layout.item_analysis_single_choice, parent, false));
            case TYPE_MULTIPLE_CHOICE:
                return new PieChartViewHolder(inflater.inflate(R.layout.item_analysis_multiple_choice, parent, false));
            case TYPE_DROPDOWN:
                return new PieChartViewHolder(inflater.inflate(R.layout.item_analysis_dropdown, parent, false));
            case TYPE_YES_NO:
                return new PieChartViewHolder(inflater.inflate(R.layout.item_analysis_yes_no, parent, false));
            case TYPE_RATING_SCALE:
                return new BarChartViewHolder(inflater.inflate(R.layout.item_analysis_rating_scale, parent, false));
            default:
                throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Question q = questionList.get(position);

        if (holder instanceof OpenEndedViewHolder) {
            OpenEndedQuestion openQ = (OpenEndedQuestion) q;
            OpenEndedViewHolder vh = (OpenEndedViewHolder) holder;
            vh.title.setText(openQ.getQuestionTitle());
            vh.summary.setText(openQ.getAnalysisResult() != null ? openQ.getAnalysisResult() : "Analysis not available.");
        } else if (holder instanceof PieChartViewHolder && q instanceof AnalyzableQuestion) {
            PieChartViewHolder vh = (PieChartViewHolder) holder;
            vh.title.setText(q.getQuestionTitle());
            setupPieChart(vh.chart, (AnalyzableQuestion) q);
        } else if (holder instanceof BarChartViewHolder && q instanceof AnalyzableQuestion) {
            BarChartViewHolder vh = (BarChartViewHolder) holder;
            vh.title.setText(q.getQuestionTitle());
            setupBarChart(vh.chart, (AnalyzableQuestion) q);
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    // === ViewHolder Classes ===

    static class OpenEndedViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView title, summary;

        public OpenEndedViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemAnalysis_LBL_title);
            summary = itemView.findViewById(R.id.itemAnalysis_LBL_summary);
        }
    }

    static class PieChartViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView title;
        PieChart chart;

        public PieChartViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemAnalysis_LBL_title);
            chart = itemView.findViewById(R.id.itemAnalysis_LBL_chartPie);
        }
    }

    static class BarChartViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView title;
        BarChart chart;

        public BarChartViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemAnalysis_LBL_title);
            chart = itemView.findViewById(R.id.itemAnalysis_LBL_chartBar);
        }
    }

    // === Chart Helpers ===

    private void setupPieChart(PieChart chart, AnalyzableQuestion question) {
        Map<String, Integer> data = question.getAnswerDistribution(); // You implement this
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData pieData = new PieData(dataSet);
        chart.setData(pieData);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.invalidate();
    }

    private void setupBarChart(BarChart chart, AnalyzableQuestion question) {
        Map<String, Integer> data = question.getAnswerDistribution();
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Responses");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = (int) value;
                return i >= 0 && i < labels.size() ? labels.get(i) : "";
            }
        });

        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setFitBars(true);
        chart.invalidate();
    }

}



