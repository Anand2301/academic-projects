package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TextView name,department,email,phone;
    EditText editDepartment,editPhone;

    ImageView editDepartmentImg,finishDepartmentImg,editPhoneImg,finishPhoneImg;

    Button profileBtn;

    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        name=findViewById(R.id.profile_name);
        department=findViewById(R.id.profile_department);
        email=findViewById(R.id.profile_email);
        phone=findViewById(R.id.profile_phone);
        editDepartment=findViewById(R.id.profile_editable_department);
        editPhone=findViewById(R.id.profile_editable_phone);
        editDepartmentImg=findViewById(R.id.profile_departmentEdit);
        finishDepartmentImg=findViewById(R.id.profile_departmentEditFinish);
        editPhoneImg=findViewById(R.id.profile_phoneEdit);
        finishPhoneImg=findViewById(R.id.profile_phoneEditFinish);
        profileBtn =findViewById(R.id.profile_btn);

        firestore=FirebaseFirestore.getInstance();


        retrieveData(name,email,department,phone);

        editDepartmentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                department.setVisibility(View.INVISIBLE);
                editDepartment.setVisibility(View.VISIBLE);
                editDepartmentImg.setVisibility(View.INVISIBLE);
                finishDepartmentImg.setVisibility(View.VISIBLE);


            }
        });

        finishDepartmentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(department.getText().toString().equals("Phone")) && (editDepartment.getText().toString().equals("")) ){

                    department.setText(department.getText().toString());


                }else if(editDepartment.getText().toString().equals("")){

                    department.setText("Phone");

                }else{

                    department.setText(editDepartment.getText().toString());
                    updateInFireStore("department",department.getText().toString().trim());

                }


                editDepartment.setVisibility(View.INVISIBLE);
                department.setVisibility(View.VISIBLE);


                finishDepartmentImg.setVisibility(View.INVISIBLE);
                editDepartmentImg.setVisibility(View.VISIBLE);



            }
        });

        editPhoneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phone.setVisibility(View.INVISIBLE);
                editPhone.setVisibility(View.VISIBLE);
                editPhoneImg.setVisibility(View.INVISIBLE);
                finishPhoneImg.setVisibility(View.VISIBLE);
            }
        });

        finishPhoneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(phone.getText().toString().equals("Phone")) && (editPhone.getText().toString().equals("")) ){

                    phone.setText(phone.getText().toString());


                }else if(editPhone.getText().toString().equals("")){

                    phone.setText("Phone");

                }else{

                    phone.setText(editPhone.getText().toString());
                    updateInFireStore("phone",phone.getText().toString().trim());

                }


                editPhone.setVisibility(View.INVISIBLE);
                phone.setVisibility(View.VISIBLE);


                finishPhoneImg.setVisibility(View.INVISIBLE);
                editPhoneImg.setVisibility(View.VISIBLE);



            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signOut();
            }
        });



    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent(ProfileActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }


    public void retrieveData(TextView nameTextView, TextView emailTextView,TextView departmentTextView,TextView phoneTextView){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("loggedUserData",MODE_PRIVATE);
        String name=sharedPreferences.getString("name","");
        String email=sharedPreferences.getString("email","");

        String id=sharedPreferences.getString("userId","");

       firestore.collection("signedPeople").document(id).get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {

                       String department="";
                       String phone="";

                       Log.d("msg","(profile_activity) successfully retrieved user data from signedPeople firestore ");

                       department=documentSnapshot.getString("department");
                       phone=documentSnapshot.getString("phone");

                       Log.d("msg","(profile_activity) department in signedPeople firestore is "+department);
                       Log.d("msg","(profile_activity) phone in signedPeople firestore is "+phone);
                       nameTextView.setText(name);
                       emailTextView.setText(email);

                       if (department ==null){

                           departmentTextView.setText("Department");

                       }else {
                           departmentTextView.setText(department);
                       }

                       if (phone==null){

                           phoneTextView.setText("phone");

                       }else {
                           phoneTextView.setText(phone);

                       }




                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {

                       Log.d("msg","(profile_activity) failed to retrieve user data from signedPeople firestore "+e);
                   }
               });



    }

    public void  updateInFireStore(String filedName,String department_or_phone){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("loggedUserData",MODE_PRIVATE);

        String id=sharedPreferences.getString("userId","");

        DocumentReference documentReference=firestore.collection("signedPeople").document(id);

        Map<String,Object>data=new HashMap<>();

        data.put(filedName,department_or_phone);


        documentReference.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.d("msg","(profile_activity) successfully updated "+filedName+" in signedPeople firestore ");

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("msg","(profile_activity) failed to update in signedPeople firestore ");
                    }
                });



    }

    public void  signOut(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("loggedUserData",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.remove("name");
        editor.remove("email");
        editor.remove("userId");
        editor.remove("state");
        editor.commit();

        FirebaseAuth.getInstance().signOut();

        Log.d("msg","(profile_activity) you have signed out successfully ");
        Toast.makeText(this, "you have signed out successfully", Toast.LENGTH_SHORT).show();

        Intent intent=new Intent(ProfileActivity.this,SignInActivity.class);
        startActivity(intent);
        finish();



    }


}
