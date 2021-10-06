package com.example.mynotepad.multiline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mynotepad.MainActivity;
import com.example.mynotepad.databinding.FragmentMultilineBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
        );
    }
    public void textSizeChange(){
        titleText.setTextSize(Math.round(MainActivity.mScaleFactor * 52));
        noteText.setTextSize(Math.round(MainActivity.mScaleFactor * 39));
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) requireActivity()).save();
    }
    @Override
    public void onStop() {
        super.onStop();
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
}
