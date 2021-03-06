package com.blstream.studybox.exam;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blstream.studybox.R;
import com.blstream.studybox.auth.login.LoginManager;
import com.blstream.studybox.events.ImproveAllEvent;
import com.blstream.studybox.events.ImproveWrongEvent;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultDialogFragment extends DialogFragment {

    private static final String TAG_CORRECT_ANSWERS = "correctAnswers";
    private static final String TAG_NUMBER_OF_QUESTIONS = "noOfQuestions";

    private int correctAnswers;
    private int noOfQuestions;
    private Activity activity;
    private LoginManager loginManager = new LoginManager();

    @Bind(R.id.congrats)
    TextView congrats;

    @Bind(R.id.total_score)
    TextView totalScore;

    @Bind(R.id.pieChart)
    PieChart pieChart;

    @Bind(R.id.my_decks)
    Button myDecks;

    @Bind(R.id.improve_result)
    Button improve;

    @Bind(R.id.improve_only_wrong)
    Button improveWrong;

    public static ResultDialogFragment newInstance(int correctAnswers, int noOfQuestions) {
        ResultDialogFragment resultFragment = new ResultDialogFragment();
        Bundle args = new Bundle();
        args.putInt(TAG_CORRECT_ANSWERS, correctAnswers);
        args.putInt(TAG_NUMBER_OF_QUESTIONS, noOfQuestions);
        resultFragment.setArguments(args);
        return resultFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        correctAnswers = getArguments().getInt(TAG_CORRECT_ANSWERS);
        noOfQuestions = getArguments().getInt(TAG_NUMBER_OF_QUESTIONS);
        activity = getActivity();
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NoDrawerTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_result, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ButterKnife.bind(this, view);

        if (loginManager.isUserLoggedIn()) {
            myDecks.setText(R.string.my_decks);
        } else {
            myDecks.setText(R.string.decks);
        }

        if (correctAnswers == noOfQuestions) {
            improve.setVisibility(View.GONE);
            improveWrong.setVisibility(View.GONE);
        }
        setCongratsMessage();
        totalScore.setText(getString(R.string.correct_answers, correctAnswers, noOfQuestions));
        customizePieChart();
        addPieChartData(correctAnswers, noOfQuestions);
    }

    private void setCongratsMessage() {
        double result = ((double) correctAnswers) / noOfQuestions;
        if (result <= 0.5) {
            congrats.setText(getString(R.string.unfortunately));
        } else if (result > 0.5 && result <= 0.8) {
            congrats.setText(getString(R.string.not_bad));
        } else {
            congrats.setText(getString(R.string.congrats));
        }
    }

    @OnClick(R.id.my_decks)
    public void backToMyDecks() {
        activity.finish();
    }

    @OnClick(R.id.improve_result)
    public void improveResult() {
        EventBus.getDefault().post(new ImproveAllEvent());
        dismiss();
    }

    @OnClick(R.id.improve_only_wrong)
    public void improveOnlyWrong() {
        EventBus.getDefault().post(new ImproveWrongEvent());
        dismiss();
    }

    private void customizePieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setDescription(" ");

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(15);
        pieChart.setTransparentCircleRadius(20);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.animateY(2000);

        pieChart.setCenterTextSize(12);
        pieChart.setCenterTextColor(ContextCompat.getColor(getContext(), R.color.colorGraphite));
        pieChart.setDrawSliceText(false);

        Legend l = pieChart.getLegend();
        l.setEnabled(false);
    }

    private void addPieChartData(int correctAnswer, int noOfQuestion) {
        float[] yData;
        String[] xData = {getString(R.string.correct_quantity), getString(R.string.incorrect_quantity)};

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.colorDarkBlue));
        colors.add(ContextCompat.getColor(getContext(), R.color.colorRaspberry));

        if (correctAnswer == noOfQuestion) {
            yData = new float[]{100};
        } else if (correctAnswer == 0) {
            yData = new float[]{100};
            colors.remove(0);
        } else {
            float good = (float) correctAnswer * 100 / noOfQuestion;
            yData = new float[]{good, 100 - good};
        }

        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i = 0; i < yData.length; i++)
            yValues.add(new Entry(yData[i], i));

        ArrayList<String> xValues = new ArrayList<>();
        Collections.addAll(xValues, xData);

        PieDataSet dataSet = new PieDataSet(yValues, getString(R.string.your_score));
        dataSet.setColors(colors);
        dataSet.setSliceSpace(5);
        dataSet.setSelectionShift(10);

        PieData data = new PieData(xValues, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
        data.setValueTextSize(20);

        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog result = new Dialog(activity, getTheme()) {
            @Override
            public void onBackPressed() {
                activity.finish();
                dismiss();
            }
        };
        return result;
    }
}
