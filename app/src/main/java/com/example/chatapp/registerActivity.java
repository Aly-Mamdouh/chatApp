package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatapp.common.common;
import com.example.chatapp.model.userModel;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class registerActivity extends AppCompatActivity {

    @BindView(R.id.register_et_first_name)
    TextInputEditText et_first_name;

    @BindView(R.id.register_et_last_name)
    TextInputEditText et_last_name;

    @BindView(R.id.register_et_phone)
    TextInputEditText et_phone;

    @BindView(R.id.register_et_dateOfBirth)
    TextInputEditText et_dateOfBirth;

    @BindView(R.id.register_et_bio)
    TextInputEditText et_bio;

    @BindView(R.id.register_btn_continue)
    Button btn_continue;
    FirebaseDatabase datbase;
    DatabaseReference userReference;
   //Date pickers let users select a date or range of dates. They should be suitable for the context in which they appear.
   //Date pickers can be embedded into dialogs on mobile.
    MaterialDatePicker<Long> materialDatePicker= MaterialDatePicker.Builder.datePicker().build();
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-mm-yyyy");
    Calendar calendar=Calendar.getInstance();
    boolean isSelectedBirthDate=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        setDefultData();

    }

    private void setDefultData() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        et_phone.setText(user.getPhoneNumber());
        et_phone.setEnabled(false);
        et_dateOfBirth.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                materialDatePicker.show(getSupportFragmentManager(),materialDatePicker.toString());

            }
        });
        btn_continue.setOnClickListener(v -> {
            if(!isSelectedBirthDate) {
                Toast.makeText(registerActivity.this, "please enter birthdate", Toast.LENGTH_LONG).show();
                return;
            }
            userModel userModel=new userModel();

            userModel.setFirstName(et_first_name.getText().toString());
            userModel.setLastName(et_last_name.getText().toString());
            userModel.setBio(et_bio.getText().toString());
            userModel.setBirthDate(calendar.getTimeInMillis());
            userModel.setPhone(et_phone.getText().toString());
            userModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userReference.child(userModel.getUid())
            .setValue(userModel)
            .addOnFailureListener(e -> Toast.makeText(registerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show()).addOnSuccessListener(aVoid -> {
                Toast.makeText(registerActivity.this, "Register Success!", Toast.LENGTH_LONG).show();
                common.currentUser=userModel;
                startActivity(new Intent(registerActivity.this,homeActivity.class));
                finish();
            });
        });
    }

    private void init(){
        ButterKnife.bind(this);
        datbase =FirebaseDatabase.getInstance();
        userReference=datbase.getReference(common.USER_REF);
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
         calendar.setTimeInMillis(selection);
         et_dateOfBirth.setText(simpleDateFormat.format(selection));
         isSelectedBirthDate=true;

        });
    }
}