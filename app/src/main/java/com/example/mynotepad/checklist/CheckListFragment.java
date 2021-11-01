package com.example.mynotepad.checklist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mynotepad.App;
import com.example.mynotepad.AppDatabase;
import com.example.mynotepad.MainActivity;
import com.example.mynotepad.R;
import com.example.mynotepad.VoiceFragment;
import com.example.mynotepad.checklist_row_type.ButtonPlus;
import com.example.mynotepad.checklist_row_type.CheckListItem;
import com.example.mynotepad.checklist_row_type.RowType;
import com.example.mynotepad.checklist_row_type.TitleCheckNotes;
import com.example.mynotepad.databinding.FragmentChecklistBinding;
import com.example.mynotepad.multiline.MultilineDao;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getSystemService;

//класс фрагмента
public class CheckListFragment extends Fragment implements RecyclerViewClickListener {

    public RecyclerView notesList;
    //массив всех объектов в RecyclerView
    public List<RowType> Notes = new ArrayList<>();
    public String datetime;
    private RowType checkListItem;
    private CheckNotesAdapter checkNotesAdapter;
    private CheckListDIffUtil checkListDIffUtil;
    private int clickedPosition;
    public long id_db;

    //конструктор воссоздаёт объект checkList по его данным из checklisttext
    public CheckListFragment(CheckListText checkListText) {
        Notes = checkListText.Notes;
        datetime = checkListText.datetime;
        id_db = checkListText.id;
    }

    public CheckListFragment() {
        checkListItem = new TitleCheckNotes();
        Notes.add(checkListItem);

        //notesList always has at least one element
        checkListItem = new CheckListItem();
        Notes.add(checkListItem);

        checkListItem = new ButtonPlus();
        Notes.add(checkListItem);
        datetime = "";
        id_db = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        FragmentChecklistBinding binding = FragmentChecklistBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        /*view.setOnTouchListener((view, motionEvent) -> {
            mScaleDetector.onTouchEvent(motionEvent);
            return true;
        });*/
        notesList = binding.recyclerCheckList;
        notesList.setLayoutManager(new LinearLayoutManager(getContext()));
        checkNotesAdapter = new CheckNotesAdapter(Notes, this);
        notesList.setAdapter(checkNotesAdapter);
        checkListDIffUtil = new CheckListDIffUtil(checkNotesAdapter.data, Notes);
        /*noteCheckList = ((CheckNotesAdapter.CheckListViewHolder) Objects.requireNonNull(notesList.findViewHolderForAdapterPosition(Notes.size() - 3))).checkListItemBinding.noteCheckList;
        noteCheckList.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(noteCheckList, InputMethodManager.SHOW_IMPLICIT);*/

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            //do
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                if (dragged.getBindingAdapterPosition() > 0 && dragged.getBindingAdapterPosition() < Notes.size() - 1 && target.getBindingAdapterPosition() > 0 && target.getBindingAdapterPosition() < Notes.size() - 1) {
                    int position_dragged = dragged.getBindingAdapterPosition();
                    int position_target = target.getBindingAdapterPosition();
                    Collections.swap(Notes, position_dragged, position_target);
                    checkNotesAdapter.notifyItemMoved(position_dragged, position_target);
                    if (position_dragged == Notes.size() - 2 || position_target == Notes.size() - 2) {
                        ((CheckNotesAdapter.CheckListViewHolder) Objects.requireNonNull(notesList.findViewHolderForAdapterPosition(position_dragged))).checkListItemBinding.noteCheckList.setOnFocusChangeListener(null);
                        ((CheckNotesAdapter.CheckListViewHolder) Objects.requireNonNull(notesList.findViewHolderForAdapterPosition(position_target))).checkListItemBinding.noteCheckList.setOnFocusChangeListener(null);
                        //((CheckNotesAdapter.CheckListViewHolder) Objects.requireNonNull(notesList.findViewHolderForAdapterPosition(Notes.size() - 2))).checkListItemBinding.noteCheckList.setOnFocusChangeListener(getFocusListener);
                    }
                    //setCursor(position_target);
                }
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int k = viewHolder.getBindingAdapterPosition();
                if (k > 0 && k < Notes.size() - 1) {
                    new AlertDialog.Builder(viewHolder.itemView.getContext())
                            .setPositiveButton("Delete", (dialogInterface, i) -> {
                                Notes.remove(viewHolder.getBindingAdapterPosition());
                                checkNotesAdapter.notifyItemRemoved(viewHolder.getBindingAdapterPosition());
                            })
                            .setNegativeButton("Cancel", (dialogInterface, i) -> checkNotesAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition()))
                            .setOnCancelListener(dialogInterface -> checkNotesAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition()))
                            .create()
                            .show();
                } else checkNotesAdapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                //setCursor(viewHolder.getBindingAdapterPosition());
            }
        }).attachToRecyclerView(notesList);

        /*((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
        );*/
        return view;
    }

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Toast.makeText(getContext(), "onViesCreated", Toast.LENGTH_SHORT);
        Log.d("!CheckList", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!onViewCreated");
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
        );
    }*/

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //showKeyBoard();
        *//*((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
        );*//*
        //setCursor(Notes.size() - 2);
    }*/

    /*@Override
    public void onStart() {
        super.onStart();
        if (datetime.equals("")) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
            );
        }
        else {
            View view = getActivity().getCurrentFocus();
            if (view != null)
                view.clearFocus();
        }
    }*/

    /*@Override
    public void onResume() {
        super.onResume();
        ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
        );
        setCursor(Notes.size() - 2);
    }*/

    /*@Override
    public void onStop() {
        super.onStop();
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }*/

    /*@Override
    public void onResume() {
        super.onResume();
        setCursor(Notes.size() - 2);
    }*/

    /*@Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        return true;
    }*/

    public void textSizeChange() {
        for (int i = 0; i < Notes.size(); i++) {
            int k = checkNotesAdapter.getItemViewType(i);
            switch (k) {
                case RowType.TITLE_ROW_TYPE:
                    if (notesList.findViewHolderForLayoutPosition(i) == null) continue;
                    ((CheckNotesAdapter.TitleViewHolder) Objects.requireNonNull(notesList.findViewHolderForLayoutPosition(i))).bind((TitleCheckNotes) Notes.get(i));
                    break;
                case RowType.CHECKLIST_ROW_TYPE:
                    if (notesList.findViewHolderForLayoutPosition(i) == null) continue;
                    ((CheckNotesAdapter.CheckListViewHolder) Objects.requireNonNull(notesList.findViewHolderForLayoutPosition(i))).bind((CheckListItem) Notes.get(i));
                    break;
                default:
                    break;
            }
        }
    }

    public void setCursor(int position) {
        if (position != 0) {
            if (position < 0) {
                position = Notes.size() - 2;
                notesList.scrollToPosition(position);
                EditText noteCheckList = ((CheckNotesAdapter.CheckListViewHolder) Objects.requireNonNull(notesList.findViewHolderForAdapterPosition(position))).checkListItemBinding.noteCheckList;
                noteCheckList.requestFocus();
                noteCheckList.setSelection(noteCheckList.getText().length());
            } else if (position >= Notes.size() - 1) {
                notesList.scrollToPosition(position - 1);
                addCheckNote();
                EditText noteCheckList = ((CheckNotesAdapter.CheckListViewHolder) Objects.requireNonNull(notesList.findViewHolderForAdapterPosition(position - 1))).checkListItemBinding.noteCheckList;
                noteCheckList.requestFocus();
                noteCheckList.setSelection(noteCheckList.getText().length());
            } else if (position <= Notes.size() - 2) {
                notesList.scrollToPosition(position);
                EditText noteCheckList = ((CheckNotesAdapter.CheckListViewHolder) Objects.requireNonNull(notesList.findViewHolderForAdapterPosition(position))).checkListItemBinding.noteCheckList;
                noteCheckList.requestFocus();
                noteCheckList.setSelection(noteCheckList.getText().length());
            }
        } else {
            notesList.scrollToPosition(position);
            EditText titleText = ((CheckNotesAdapter.TitleViewHolder) Objects.requireNonNull(notesList.findViewHolderForAdapterPosition(position))).titleChecklistBinding.titleTextCheckList;
            titleText.requestFocus();
            titleText.setSelection(titleText.getText().length());
        }
        checkNotesAdapter.clickedPosition = position;
    }

    public void showKeyBoard() {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
        );
    }

    public void prev() {
        if (notesList.hasFocus())
            setCursor(checkNotesAdapter.clickedPosition - 1);
    }

    public void next() {
        if (notesList.hasFocus()) {
            setCursor(checkNotesAdapter.clickedPosition + 1);
        }
    }

    public void voiceInput(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            List<String> results = result.getData().getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            spokenText = textToReminder(spokenText);

            int k = checkNotesAdapter.getItemViewType(checkNotesAdapter.clickedPosition);
            switch (k) {
                case RowType.TITLE_ROW_TYPE:
                    EditText titleChecklist = ((CheckNotesAdapter.TitleViewHolder) Objects.requireNonNull(notesList.findViewHolderForAdapterPosition(checkNotesAdapter.clickedPosition))).titleChecklistBinding.titleTextCheckList;
                    titleChecklist.append(spokenText);
                    break;
                case RowType.CHECKLIST_ROW_TYPE:
                    EditText noteCheckList = ((CheckNotesAdapter.CheckListViewHolder) Objects.requireNonNull(notesList.findViewHolderForAdapterPosition(checkNotesAdapter.clickedPosition))).checkListItemBinding.noteCheckList;
                    noteCheckList.append(spokenText);
                    break;
                default:
                    break;
            }
        }
    }

    public String textToReminder(String text) {
        return ((MainActivity) requireActivity()).textToReminder(text);
    }
    /*public String textToReminder(String text) {
        String[] result = {"", ""};

        Pattern pattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])\\s[а-я]+\\s[в]\\s[0-2]?[0-9][:][0-5]?[0-9]");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            result[0] = text.substring(start, end);
            result[1] = matcher.replaceFirst("");
            result[1] = result[1].replace("  ", "\n");
        } else return text;

        try {
            DateTimeFormatter formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formatter = new DateTimeFormatterBuilder()
                        .appendPattern("d MMMM в H:m")
                        .parseDefaulting(ChronoField.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                        .toFormatter(Locale.forLanguageTag("ru-RU"));
            }
            LocalDateTime dateTime;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dateTime = LocalDateTime.parse(result[0], formatter);
                if (dateTime.isBefore(LocalDateTime.now(ZoneId.systemDefault())))
                    dateTime = dateTime.plusYears(1);
                result[0] = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"));
            }
        } catch (Exception e) {
            return text;
        }
        return result[0] + "\n" + result[1].trim();
    }*/

    void addCheckNote() {
        checkListItem = new CheckListItem();
        Notes.add(Notes.size() - 1, checkListItem);

        checkNotesAdapter.notifyItemInserted(Notes.size() - 2);
        //both ways are possible
        //DiffUtil.DiffResult checkListDiffResult = DiffUtil.calculateDiff(checkListDIffUtil);
        //checkListDiffResult.dispatchUpdatesTo(checkNotesAdapter);
        notesList.scrollToPosition(Notes.size() - 1);
        //Objects.requireNonNull((CheckNotesAdapter.CheckListViewHolder) notesList.findViewHolderForAdapterPosition(Notes.size() - 3)).checkListItemBinding.noteCheckList.setOnFocusChangeListener(null);

        //setCursor(Notes.size() - 3);
    }

    private final View.OnFocusChangeListener getFocusListener = (view, b) -> {
        if (b) {
            addCheckNote();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) requireActivity()).save();
    }

    //Можно обойтись обычным RecyclerViewAdapter и не писать это, оставить только класс DiffUtil
    static DiffUtil.ItemCallback<CheckListFragment> DIFF_CALLBACK = new DiffUtil.ItemCallback<CheckListFragment>() {
        @Override
        public boolean areItemsTheSame(@NonNull CheckListFragment oldItem, @NonNull CheckListFragment newItem) {
            return oldItem.Notes.equals(newItem.Notes);
        }

        @Override
        public boolean areContentsTheSame(@NonNull CheckListFragment oldItem, @NonNull CheckListFragment newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public void recyclerViewListClicked(View view, int position) {
        clickedPosition = position;
    }
}
