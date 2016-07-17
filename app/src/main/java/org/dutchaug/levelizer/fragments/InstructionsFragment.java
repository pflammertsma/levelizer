package org.dutchaug.levelizer.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import org.dutchaug.levelizer.R;

import butterknife.ButterKnife;

public class InstructionsFragment extends DialogFragment {

    public static final String TAG = InstructionsFragment.class.getSimpleName();


    public static InstructionsFragment create() {
        return new InstructionsFragment();
    }

    public InstructionsFragment() {
        //no argument constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_instructions, null, false);
        ButterKnife.bind(this, view);

        builder.setView(view);
        return builder.create();
    }
}
