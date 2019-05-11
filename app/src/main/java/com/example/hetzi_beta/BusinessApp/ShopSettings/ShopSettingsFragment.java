package com.example.hetzi_beta.BusinessApp.ShopSettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hetzi_beta.Login.ChangePasswordFragment;
import com.example.hetzi_beta.Login.LoginActivity;
import com.example.hetzi_beta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShopSettingsFragment extends Fragment {
    private enum setting_pos {
        ;

        private static final int NAME = 0;
        private static final int EMAIL = 1;
        private static final int PHONE = 2;

        private static final int CHANGE_PASS = 0;
        private static final int BANK_DETAILS = 1;

        private static final int LOGOUT = 0;
        private static final int DELETE_ACCOUNT = 1;
    }

    private String[] titles1 = {
            "שם",
            "דוא\"ל",
            "טלפון",
    };

    private String[] titles2 = {
            "שנה סיסמא",
            "פרטי בנק"
    };

    private String[] titles3 = {
            "התנתקות",
            "מחיקת חשבון"

    };

    private String[] details = new String[3];
    private ListView settings_ListView1;
    private ListView settings_ListView2;
    private ListView settings_ListView3;

    public ShopSettingsFragment() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        details[setting_pos.NAME]   = user.getDisplayName();
        details[setting_pos.EMAIL]  = user.getEmail();
        details[setting_pos.PHONE]  = user.getPhoneNumber();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_shop_settings, container, false);

        ShopSettingsAdapter adapter1 = new ShopSettingsAdapter(getActivity(), titles1, details, R.layout.item_settings_list);
        settings_ListView1 = root_view.findViewById(R.id.settings_list1);
        settings_ListView1.setAdapter(adapter1);
        settings_ListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case setting_pos.NAME:

                        break;
                    case setting_pos.EMAIL:

                        break;
                    case setting_pos.PHONE:

                        break;
                }
            }
        });

        ShopSettingsAdapter adapter2 = new ShopSettingsAdapter(getActivity(), titles2, null, R.layout.item_settings_list);
        settings_ListView2 = root_view.findViewById(R.id.settings_list2);
        settings_ListView2.setAdapter(adapter2);
        settings_ListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case setting_pos.CHANGE_PASS:
                        ChangePasswordFragment nextFrag= new ChangePasswordFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.shop_settings_RelativeLayout_root, nextFrag, "findThisFragment")
                                .addToBackStack(null)
                                .commit();
                        break;
                    case setting_pos.BANK_DETAILS:

                        break;
                }
            }
        });

        ShopSettingsAdapter adapter3 = new ShopSettingsAdapter(getActivity(), titles3, null, R.layout.item_settings_list_red);
        settings_ListView3 = root_view.findViewById(R.id.settings_list3);
        settings_ListView3.setAdapter(adapter3);
        settings_ListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case setting_pos.LOGOUT:
                        showDialogLogout();
                        break;
                    case setting_pos.DELETE_ACCOUNT:

                        break;
                }
            }
        });

        return root_view;
    }

    public void showDialogLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("בטוח להתנתק?");
        builder.setCancelable(true);

        builder.setNegativeButton(Html.fromHtml("<font color='#009faf'>לא</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton(Html.fromHtml("<font color='#009faf'>כן</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_dialog_round_corners));
        alertDialog.show();
    }
}
