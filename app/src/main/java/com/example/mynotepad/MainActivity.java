package com.example.mynotepad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mynotepad.checklist.CheckListConverter;
import com.example.mynotepad.checklist.CheckListDao;
import com.example.mynotepad.checklist.CheckListFragment;
import com.example.mynotepad.checklist.CheckListText;
import com.example.mynotepad.checklist_row_type.CheckListItem;
import com.example.mynotepad.checklist_row_type.TitleCheckNotes;
import com.example.mynotepad.databinding.ActivityMainBinding;
import com.example.mynotepad.databinding.FragmentMultilineBinding;
import com.example.mynotepad.databinding.ItemChecklistNoteBinding;
import com.example.mynotepad.databinding.ItemChecklistTitleBinding;
import com.example.mynotepad.folder.FolderFragment;
import com.example.mynotepad.multiline.MultilineDao;
import com.example.mynotepad.multiline.MultilineFragment;
import com.example.mynotepad.multiline.MultilineText;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    Fragment selectedFragment, selectedTopFragment, selectedBottomFragment;
    MultilineFragment multiline;
    CheckListFragment checkList;
    FolderFragment folderFragment;
    VoiceFragment voiceFragment;

    private final String pref_filename = "user_settings",
            key_size_factor = "scale_factor",
            key_title_size = "title_size",
            key_note_size = "note_size",
            key_folder_title_size = "folder_title_size",
            key_folder_date_size = "key_folder_date_size";
    SharedPreferences preferences;
    private ScaleGestureDetector mScaleDetector;
    public static float mScaleFactor;

    //константы для пустого окна и активности с кнопками "сохранить" и "папка"
    public static final EmptyFragment TOP_EMPTY_FRAGMENT = new EmptyFragment();
    public static final EmptyFragment BOTTOM_EMPTY_FRAGMENT = new EmptyFragment();
    public static final TopLayoutFragment TOP_LAYOUT_FRAGMENT = new TopLayoutFragment();
    public static final VoiceFragment VOICE_FRAGMENT = new VoiceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //сначала инициализация bottom navigation
        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.setOnItemSelectedListener(navListener);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("folder")) {
                selectedTopFragment = TOP_EMPTY_FRAGMENT;
                selectedBottomFragment = BOTTOM_EMPTY_FRAGMENT;
                folderFragment = new FolderFragment();
                selectedFragment = folderFragment;
                // organize with code block
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentTopPlace, selectedTopFragment)
                        .replace(R.id.fragmentBottomPlace, selectedBottomFragment)
                        .replace(R.id.fragmentPlace, selectedFragment)
                        .commit();
                hideKeyboard();
            } else {
                selectedTopFragment = TOP_LAYOUT_FRAGMENT;
                selectedBottomFragment = VOICE_FRAGMENT;
                if (savedInstanceState.containsKey("multiline")) {
                    multiline = new MultilineFragment((MultilineText) Objects.requireNonNull(savedInstanceState.getSerializable("multiline")));
                    selectedFragment = multiline;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentTopPlace, selectedTopFragment)
                            .replace(R.id.fragmentBottomPlace, selectedBottomFragment)
                            .replace(R.id.fragmentPlace, selectedFragment)
                            .commit();
                    showKeyboard();
                } else if (savedInstanceState.containsKey("checklist")) {
                    checkList = new CheckListFragment((CheckListText) Objects.requireNonNull(savedInstanceState.getSerializable("checklist")));
                    selectedFragment = checkList;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentTopPlace, selectedTopFragment)
                            .replace(R.id.fragmentBottomPlace, selectedBottomFragment)
                            .replace(R.id.fragmentPlace, selectedFragment)
                            .commit();
                    showKeyboard();
                }
            }
        } else{
            /*selectedTopFragment = EMPTY_FRAGMENT;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentTopPlace, selectedTopFragment).commit();
            folderFragment = new FolderFragment();
            selectedFragment = folderFragment;*/
            selectedTopFragment = TOP_LAYOUT_FRAGMENT;
            selectedBottomFragment = VOICE_FRAGMENT;
            checkList = new CheckListFragment();
            selectedFragment = checkList;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentTopPlace, selectedTopFragment)
                    .replace(R.id.fragmentBottomPlace, selectedBottomFragment)
                    .replace(R.id.fragmentPlace, selectedFragment)
                    .commit();
            showKeyboard();
        }

        preferences = getSharedPreferences(pref_filename, MODE_PRIVATE);
        mScaleFactor = preferences.getFloat(key_size_factor, 1.f);
        if (mScaleFactor == 0.f)
            mScaleFactor = 1.f;

        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mScaleFactor *= detector.getScaleFactor();
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 1.9f));
                if (selectedFragment == checkList)
                    checkList.textSizeChange();
                else if (selectedFragment == multiline)
                    multiline.textSizeChange();
                else if (selectedFragment == folderFragment)
                    folderFragment.textSizeChange();
                preferences = getSharedPreferences(pref_filename, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat(key_size_factor, mScaleFactor);
                editor.apply();
                //Toast.makeText(getApplicationContext(), "scale", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        /*if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        if (selectedFragment == checkList && checkList.datetime.equals("")) {
            showKeyboard();
        } else {
            View view = getCurrentFocus();
            if (view != null)
                view.clearFocus();
        }
    }*/

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        *//*switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*//*
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedFragment == multiline) {
            MultilineText multilineText = new MultilineText();
            multilineText.datetime = "";
            multilineText.noteText = multiline.noteText.getText().toString();
            multilineText.titleText = multiline.titleText.getText().toString();
            multilineText.id = multiline.id_db;
            outState.putSerializable("multiline", multilineText);
        }
        if (selectedFragment == checkList) {
            CheckListText checkListText = new CheckListText();
            checkListText.datetime = "";
            checkListText.Notes = checkList.Notes;
            checkListText.id = checkList.id_db;
            outState.putSerializable("checklist", checkListText);
        }
        if (selectedFragment == folderFragment) {
            outState.putChar("folder", '0');
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener = this::onNavigationItemSelected;

    private boolean onNavigationItemSelected(MenuItem menuItem) {

        //для сохранения заметки нужен layout с соответствующей кнопкой
        if (selectedTopFragment.equals(TOP_EMPTY_FRAGMENT)) {
            selectedTopFragment = TOP_LAYOUT_FRAGMENT;
        }

        if (selectedBottomFragment.equals(BOTTOM_EMPTY_FRAGMENT)) {
            selectedBottomFragment = VOICE_FRAGMENT;
        }

        switch (menuItem.getItemId()) {
            case R.id.nav_multiline:
                save();
                multiline = new MultilineFragment();
                selectedFragment = multiline;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentTopPlace, selectedTopFragment)
                        .replace(R.id.fragmentBottomPlace, selectedBottomFragment)
                        .replace(R.id.fragmentPlace, selectedFragment)
                        .commit();
                break;
            case R.id.nav_checklist:
                save();
                checkList = new CheckListFragment();
                selectedFragment = checkList;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentTopPlace, selectedTopFragment)
                        .replace(R.id.fragmentBottomPlace, selectedBottomFragment)
                        .replace(R.id.fragmentPlace, selectedFragment)
                        .commit();
                //checkList.setCursor(checkList.Notes.size() - 2);
                break;
            default:
                save();
                menuItem.setShowAsAction(R.id.nav_checklist);
                checkList = new CheckListFragment();
                selectedFragment = checkList;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentTopPlace, selectedTopFragment)
                        .replace(R.id.fragmentBottomPlace, selectedBottomFragment)
                        .replace(R.id.fragmentPlace, selectedFragment)
                        .commit();
                //checkList.setCursor(checkList.Notes.size() - 2);
                break;
        }
        showKeyboard();
        showKeyboard();
        return true;
    }

    //View view
    public void onButtonSaveClick(View view) {
        save();
    }

    public void save() {
        if (selectedFragment == multiline && multiline.noteText != null && multiline.titleText != null) {
            if ((!multiline.noteText.getText().toString().equals("") || !multiline.titleText.getText().toString().equals(""))) {
                MultilineText multilineText = new MultilineText();
                multilineText.datetime = getCurrentDate();
                multilineText.noteText = multiline.noteText.getText().toString();
                multilineText.titleText = multiline.titleText.getText().toString();
                AppDatabase db = App.getInstance().getDatabase();
                MultilineDao multilineDao = db.multilineDao();
                if (multiline.id_db != 0) {
                    multilineText.id = multiline.id_db;
                    if (!multilineDao.getById(multiline.id_db).titleText.equals(multilineText.titleText) || !multilineDao.getById(multiline.id_db).noteText.equals(multilineText.noteText))
                        multilineDao.update(multilineText);
                } else {
                    multiline.id_db = multilineDao.insert(multilineText);
                }
            }
        } else if (selectedFragment == checkList && checkList.Notes.size() > 2) {
            if ((!((TitleCheckNotes) checkList.Notes.get(0)).titleText.equals("") || ((CheckListItem) checkList.Notes.get(1)).checkBox || !((CheckListItem) checkList.Notes.get(1)).noteText.equals("") || checkList.Notes.size() > 3)) {
                CheckListText checkListText = new CheckListText();
                checkListText.datetime = getCurrentDate();
                checkListText.Notes = checkList.Notes;
                AppDatabase db = App.getInstance().getDatabase();
                CheckListDao checkListDao = db.checkListDao();
                if (checkList.id_db != 0) {
                    checkListText.id = checkList.id_db;
                    CheckListConverter checkListConverter = new CheckListConverter();
                    if (!checkListConverter.listToString(checkListDao.getById(checkList.id_db).Notes).equals(checkListConverter.listToString(checkListText.Notes)))
                        checkListDao.update(checkListText);
                } else {
                    checkList.id_db = checkListDao.insert(checkListText);
                }
            }
        }
    }

    public void onButtonFolderClick(View view) {
        //ёбаные костыли
        save();
        hideKeyboard();
        selectedTopFragment = TOP_EMPTY_FRAGMENT;
        selectedBottomFragment = BOTTOM_EMPTY_FRAGMENT;
        folderFragment = new FolderFragment();
        selectedFragment = folderFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentTopPlace, selectedTopFragment)
                .replace(R.id.fragmentBottomPlace, selectedBottomFragment)
                .replace(R.id.fragmentPlace, selectedFragment)
                .commit();
    }

    public void onButtonPrevClick(View view) {

        if (selectedFragment == checkList) {
            checkList.prev();
        } else if (selectedFragment == multiline) {
            multiline.prev();
        }
    }

    public void onButtonNextClick(View view) {
        if (selectedFragment == checkList) {
            checkList.next();
        } else if (selectedFragment == multiline) {
            multiline.next();
        }
    }

    public void onButtonStartStopClick(View view) {
        displaySpeechRecognizer();
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU");
        mStartForResult.launch(intent);
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (selectedFragment == checkList) {
                        checkList.voiceInput(result);
                    } else if (selectedFragment == multiline) {
                        multiline.voiceInput(result);
                    }
                }
            });

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ROOT);
        //в зависимости от региона нужно брать соответствующее время
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public void updateMultiline(MultilineText multilineText) {
        selectedTopFragment = TOP_LAYOUT_FRAGMENT;
        selectedBottomFragment = VOICE_FRAGMENT;
        multiline = new MultilineFragment(multilineText);
        selectedFragment = multiline;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentTopPlace, selectedTopFragment)
                .replace(R.id.fragmentBottomPlace, selectedBottomFragment)
                .replace(R.id.fragmentPlace, selectedFragment)
                .commit();
        hideKeyboard();
        View view = getCurrentFocus();
        if (view != null)
            view.clearFocus();
    }

    public void updateCheckList(CheckListText checkListText) {
        selectedTopFragment = TOP_LAYOUT_FRAGMENT;
        selectedBottomFragment = VOICE_FRAGMENT;
        checkList = new CheckListFragment(checkListText);
        selectedFragment = checkList;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentTopPlace, selectedTopFragment)
                .replace(R.id.fragmentBottomPlace, selectedBottomFragment)
                .replace(R.id.fragmentPlace, selectedFragment)
                .commit();
        hideKeyboard();
        View view = getCurrentFocus();
        if (view != null)
            view.clearFocus();
    }

    public void showKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
        );
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        mScaleDetector.onTouchEvent(ev);
        return true;
    }
}
