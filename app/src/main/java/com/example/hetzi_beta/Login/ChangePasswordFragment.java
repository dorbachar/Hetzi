package com.example.hetzi_beta.Login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {
    private EditText    mOldPassword;
    private EditText    mNewPassword;
    private Button      mChangePassButton;
    private ImageView   mSuccess;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root_view = inflater.inflate(R.layout.fragment_change_password, container, false);

        mOldPassword        = root_view.findViewById(R.id.old_pass_EditText);
        mNewPassword        = root_view.findViewById(R.id.new_pass_EditText);
        mChangePassButton   = root_view.findViewById(R.id.change_pass_Button);
        mSuccess            = root_view.findViewById(R.id.success_ImageView);

        Utils.disableButton(mChangePassButton, getActivity(), "round");

        mOldPassword.addTextChangedListener(watcher);
        mNewPassword.addTextChangedListener(watcher);

        mChangePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboardFrom(getActivity(), root_view.findViewById(R.id.change_pass_root_RelativeLayout));
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), mOldPassword.getText().toString());
                user.reauthenticate(credential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mSuccess.setImageResource(R.drawable.baseline_check_circle_green_48dp);
                                FirebaseAuth.getInstance().getCurrentUser().updatePassword(mNewPassword.getText().toString());
                                Toast.makeText(getActivity(),"סיסמא שונתה", Toast.LENGTH_SHORT).show();
                                Utils.disableButton(mChangePassButton, getActivity(), "round");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mSuccess.setImageResource(R.drawable.baseline_highlight_off_red_48dp);
                                if (e instanceof FirebaseAuthInvalidUserException) {
                                    Toast.makeText(getActivity(), "שגיאת התחברות", Toast.LENGTH_SHORT).show();
                                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(getActivity(),"סיסמא ישנה לא נכונה", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        return root_view;
    }


    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            if (    mOldPassword.getText().toString().length() != 0 &&
                    mNewPassword.getText().toString().length() != 0) {
                Utils.enableButton(mChangePassButton, getActivity(), "round");
            }
        }
    };
}
