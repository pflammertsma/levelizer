package org.dutchaug.levelizer.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewSwitcher;

import org.dutchaug.levelizer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstructionsFragment extends DialogFragment {

    public static final String TAG = InstructionsFragment.class.getSimpleName();

    @BindView(R.id.flipper)
    protected ViewSwitcher mViewSwitcher;

    @BindView(R.id.bt_continue)
    protected Button mBtContinue;


    public static InstructionsFragment create() {
        return new InstructionsFragment();
    }

    public InstructionsFragment() {
        //no argument constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_instructions, null, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @OnClick(R.id.bt_continue)
    protected void onClickContinue() {
        if (mViewSwitcher.getDisplayedChild() == mViewSwitcher.getChildCount() - 1) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dismiss();
        } else {
            mViewSwitcher.showNext();
        }
        if (mViewSwitcher.getDisplayedChild() == mViewSwitcher.getChildCount() - 1) {
            mBtContinue.setText(R.string.instructions_done);
        } else {
            mBtContinue.setText(R.string.instructions_continue);
        }
    }

}
