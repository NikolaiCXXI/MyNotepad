package com.example.mynotepad.multiline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mynotepad.MainActivity;
import com.example.mynotepad.checklist.CheckNotesAdapter;
import com.example.mynotepad.databinding.FragmentMultilineBinding;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class MultilineFragment extends Fragment {

    public EditText titleText, noteText;
    public long id_db;
    //чзх?
    String titleString, noteString;
    public String datetime;
    FragmentMultilineBinding binding;

    public MultilineFragment(MultilineText multilineText) {
        titleString = multilineText.titleText;
        noteString = multilineText.noteText;
        datetime = multilineText.datetime;
        id_db = multilineText.id;
    }

    public MultilineFragment() {
        titleString = "";
        noteString = "";
        datetime = "";
        id_db = 0;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMultilineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        titleText = binding.titleTextMultiline;
        noteText = binding.noteTextMultiline;
        titleText.setTextSize(Math.round(MainActivity.mScaleFactor * 52));
        noteText.setTextSize(Math.round(MainActivity.mScaleFactor * 39));
        titleText.setText(titleString);
        noteText.setText(noteString);
        /*((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
        );*/
    }

    public void textSizeChange() {
        titleText.setTextSize(Math.round(MainActivity.mScaleFactor * 52));
        noteText.setTextSize(Math.round(MainActivity.mScaleFactor * 39));
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) requireActivity()).save();
    }

    public void prev() {
        titleText.requestFocus();
        titleText.setSelection(titleText.getText().length());
    }

    public void next() {
        noteText.requestFocus();
        noteText.setSelection(noteText.getText().length());
    }

    public void startStop() {
        displaySpeechRecognizer();
    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            if (titleText.hasFocus()) {
                titleText.append(spokenText);
            } else {
                noteText.append(spokenText);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*@Override
    public void onStop() {
        super.onStop();
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }*/
}
