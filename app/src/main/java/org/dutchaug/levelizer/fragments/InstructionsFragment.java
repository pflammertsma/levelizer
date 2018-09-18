package org.dutchaug.levelizer.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

    @BindView(R.id.iv_instruction2a)
    protected ImageView mIvInstruction2a;

    @BindView(R.id.iv_instruction2b)
    protected ImageView mIvInstruction2b;


    public static InstructionsFragment create() {
        return new InstructionsFragment();
    }

    public InstructionsFragment() {
        //no argument constructor
    }

    @Override
    public int getTheme() {
        return R.style.DialogStyle;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_instructions, null, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @OnClick(R.id.bt_continue)
    protected void onClickContinue() {
        if (mViewSwitcher.getDisplayedChild() == mViewSwitcher.getChildCount() - 1) {
            FragmentActivity activity = getActivity();
            if (activity instanceof InstructionsFragment.Callback) {
                ((Callback) activity).onEnterAccessibility();
            }
            dismiss();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            mViewSwitcher.showNext();
        }
        switch (mViewSwitcher.getDisplayedChild()) {
            case 0:
                mBtContinue.setText(R.string.instructions_continue);
            case 1:
                mBtContinue.setText(R.string.instructions_done);
                final ObjectAnimator anim1 = ObjectAnimator.ofFloat(mIvInstruction2b, View.ALPHA, 0, 1);
                anim1.setStartDelay(1000);
                anim1.setDuration(500);
                final ObjectAnimator anim2 = ObjectAnimator.ofFloat(mIvInstruction2b, View.ALPHA, 1, 0);
                anim2.setStartDelay(1000);
                anim2.setDuration(500);
                anim1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mIvInstruction2b.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        anim2.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });

                anim2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mIvInstruction2b.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        anim1.setStartDelay(500);
                        anim1.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });

                anim1.start();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    public interface Callback {

        void onEnterAccessibility();

    }

}
