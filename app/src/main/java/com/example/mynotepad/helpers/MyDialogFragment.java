package com.example.mynotepad.helpers;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

//не пригодился
public class MyDialogFragment extends DialogFragment{
    public boolean flag = false;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setPositiveButton("Cancel", (dialogInterface, i) -> this.flag = false);
        builder.setNegativeButton("Delete", (dialogInterface, i) -> this.flag = true);
        builder.setCancelable(true);
        return builder.create();
    }
}
