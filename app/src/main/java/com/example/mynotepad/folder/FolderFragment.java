package com.example.mynotepad.folder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mynotepad.App;
import com.example.mynotepad.AppDatabase;
import com.example.mynotepad.MainActivity;
import com.example.mynotepad.R;
import com.example.mynotepad.checklist.CheckListDao;
import com.example.mynotepad.checklist.CheckListText;
import com.example.mynotepad.checklist_row_type.TitleCheckNotes;
import com.example.mynotepad.databinding.FragmentFolderBinding;
import com.example.mynotepad.helpers.MyDialogFragment;
import com.example.mynotepad.multiline.MultilineDao;
import com.example.mynotepad.multiline.MultilineText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//пиздец логика
public class FolderFragment extends Fragment implements FolderItemsAdapter.OnFolderListener {
    RecyclerView foldersList;
    public List<FolderItem> folders = new ArrayList<>();
    private FolderItem item;
    private FolderItemsAdapter adapter;
    public int position = 0;
    //так и не пригодился, но пусть будет
    private FolderDiffUtil foldersDiffUtil;

    public FolderFragment() {
        AppDatabase db = App.getInstance().getDatabase();
        MultilineDao multilineDao = db.multilineDao();
        List<MultilineText> multilineList = multilineDao.getAll();
        CheckListDao checkListDao = db.checkListDao();
        List<CheckListText> checklistList = checkListDao.getAll();
        for (int i = 0; i < multilineList.size(); i++) {
            MultilineText dataItem = multilineList.get(i);
            item = new FolderItem(R.drawable.iconfoldermultiline, dataItem.id, dataItem.titleText, dataItem.datetime);
            folders.add(item);
        }
        //пока будем считать, что title удалить нельзя
        for (int i = 0; i < checklistList.size(); i++) {
            CheckListText dataItem = checklistList.get(i);
            item = new FolderItem(R.drawable.iconfolderchecklist, dataItem.id, ((TitleCheckNotes) dataItem.Notes.get(0)).titleText, dataItem.datetime);
            folders.add(item);
        }
        Collections.sort(folders, FolderItem.CompareByDate());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        FragmentFolderBinding binding = FragmentFolderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        foldersList = binding.recyclerFolder;
        foldersList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FolderItemsAdapter(folders, this);
        foldersList.setAdapter(adapter);
        foldersDiffUtil = new FolderDiffUtil(adapter.data, folders);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setPositiveButton("Delete", (dialogInterface, i) -> {
                            item = folders.get(viewHolder.getBindingAdapterPosition());
                            if (item.id_image == R.drawable.iconfoldermultiline) {
                                AppDatabase db = App.getInstance().getDatabase();
                                MultilineDao multilineDao = db.multilineDao();
                                multilineDao.delete(multilineDao.getById(item.id_db));
                            } else if (item.id_image == R.drawable.iconfolderchecklist) {
                                AppDatabase db = App.getInstance().getDatabase();
                                CheckListDao checkListDao = db.checkListDao();
                                checkListDao.delete(checkListDao.getById(item.id_db));
                            }
                            folders.remove(viewHolder.getBindingAdapterPosition());
                            adapter.notifyItemRemoved(viewHolder.getBindingAdapterPosition());
                        })
                        .setNegativeButton("Cancel", (dialogInterface, i) -> adapter.notifyItemChanged(viewHolder.getBindingAdapterPosition()))
                        .setOnCancelListener(dialogInterface -> adapter.notifyItemChanged(viewHolder.getBindingAdapterPosition()))
                        .create()
                        .show();
            }
        }).attachToRecyclerView(foldersList);
        return view;
    }

    @Override
    public void onFolderClick(int position) {
        item = folders.get(position);
        if (item.id_image == R.drawable.iconfoldermultiline) {
            AppDatabase db = App.getInstance().getDatabase();
            MultilineDao multilineDao = db.multilineDao();
            MultilineText multilineText = multilineDao.getById(item.id_db);
            //ёбаный пиздец
            ((MainActivity) requireActivity()).updateMultiline(multilineText, position);
        } else if (item.id_image == R.drawable.iconfolderchecklist) {
            AppDatabase db = App.getInstance().getDatabase();
            CheckListDao checkListDao = db.checkListDao();
            CheckListText checkListText = checkListDao.getById(item.id_db);
            //ёбаный пиздец
            ((MainActivity) requireActivity()).updateCheckList(checkListText, position);
        }
    }

    public void textSizeChange() {
        for (int i = 0; i < folders.size(); i++) {
            if (foldersList.findViewHolderForLayoutPosition(i) == null) continue;
            ((FolderItemsAdapter.ItemViewHolder) Objects.requireNonNull(foldersList.findViewHolderForLayoutPosition(i))).bind(folders.get(i));
        }
    }
}
