package com.myapplicationdev.android.p07ps;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecond extends Fragment {
    Button btnAddText;
    EditText etWord;
    TextView tvFrag2;
    public FragmentSecond() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tvFrag2 = view.findViewById(R.id.tvFrag2);
        btnAddText = view.findViewById(R.id.btnAddTextFrag2);
        etWord = view.findViewById(R.id.etWord);

        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = PermissionChecker.checkSelfPermission(
                        getActivity(), Manifest.permission.READ_SMS);

                if(permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.READ_SMS},0);

                    return;
                }

                Uri uri = Uri.parse("content://sms");

                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getActivity().getContentResolver();

                String filter = "body Like ?";
                String word = etWord.getText().toString();
                String[] filterArgs = {"%" + word + "%"};

                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillies = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa", dateInMillies);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvFrag2.setText(smsBody);
            }
        });

        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    btnAddText.performClick();
                }else{
                    Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
