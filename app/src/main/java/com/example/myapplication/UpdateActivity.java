package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {

    EditText name, email,phone;
    TextView  department;
    Button update_btn;
    String getDepartment;

    Custom_dialog_Class dialog_class=new Custom_dialog_Class(UpdateActivity.this);


    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        name=findViewById(R.id.update_name);
        department=findViewById(R.id.update_department);
        email=findViewById(R.id.update_email);
        phone=findViewById(R.id.update_phone);
        update_btn=findViewById(R.id.update_btn);
        firestore=FirebaseFirestore.getInstance();

        Intent intent=getIntent();
        String getName =intent.getStringExtra("name");
        getDepartment =intent.getStringExtra("department");
        String getEmail =intent.getStringExtra("email");
        String getPhone =intent.getStringExtra("phone");
        String getId=intent.getStringExtra("id");

        Log.d("msg","(update_activity) name is "+ getName +" department is "+ getDepartment +" email is "+ getEmail +" phone is "+ getPhone+" firestore id is "+getId);

        name.setText(getName);
        department.setText(getDepartment);
        email.setText(getEmail);
        phone.setText(getPhone);


       update_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               dialog_class.startLoading();

               String editedName=name.getText().toString().toLowerCase().trim();
               String editedDepartment=department.getText().toString().toLowerCase().trim();
               String editedEmail=email.getText().toString().toLowerCase().trim();
               String editedPhone=phone.getText().toString().toLowerCase().trim();


               filedCheck(editedName,editedDepartment,editedEmail,editedPhone,getId,getPhone);
           }
       });


    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(UpdateActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }


    public void filedCheck(String name, String department, String email, String phone,String getId,String getPhone){

        if(name(name) && department(department) && email(email) && phone(phone)){

            retrieveFields(name,department,email,phone,getId,getPhone);

        }else{
            dialog_class.stopLoading();
        }

    }


    public Boolean name(String name){

        if(name.length()!=0){
            Log.d("msg","(update_activity) name is "+name);
            return true;
        }else{
            Log.d("msg","(update_activity) name filed can't be empty");
            Toast.makeText(this, "name filed can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public Boolean department(String department){

        if(department.length()!=0){

            Log.d("msg","(update_activity) department is "+department);
            return true;
        }else{
            Log.d("msg","(update_activity) department filed can't be empty");
            Toast.makeText(this, "department filed can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


    }
    public Boolean email(String email){

        if(email.length()!=0){
            if(email.endsWith("@gmail.com")) {

                Log.d("msg", "(update_activity) email is " + email);
                return true;
            }else {
                Log.d("msg","(update_activity) please enter valid email address");
                Toast.makeText(this, "please enter valid email address", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Log.d("msg","(update_activity) email filed can't be empty");
            Toast.makeText(this, "email filed can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public Boolean phone(String phone){

        if(phone.length()!=0){

            if(phone.length()==10) {
                Log.d("msg", "(update_activity) phone is " + phone);
                return true;
            }else{
                Log.d("msg","(update_activity) phone number must be 10 ");
                Toast.makeText(this, "phone number must be 10 ", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Log.d("msg","(update_activity) phone filed can't be empty");
            Toast.makeText(this, "phone filed can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


    }



    public void retrieveFields(String name,String department,String email,String phone,String id,String getPhone){

        firestore.collection("profiles").document("departments").collection(department).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        String isEmailExist="";
                        String isPhoneExist="";

                        List<DocumentSnapshot>list=queryDocumentSnapshots.getDocuments();

                        for(DocumentSnapshot snapshot:list){

                            if(!(snapshot.getId().equals(id))) {

                                if (email.equals(snapshot.getString("email"))) {

                                    isEmailExist = "yes";
                                }
                                if (phone.equals(snapshot.getString("phone"))) {

                                    isPhoneExist = "yes";
                                }

                            }else{

                                Log.d("msg","(update_activity) id is equaled,so we skipped it ");
                            }



                        }

                        repeatEmailAndPhoneCheck(name,department,email,phone,id,isEmailExist,isPhoneExist,getPhone);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                        Log.d("msg","(update_activity) failed to retrieve data in firestore");
                    }
                });

    }


    public void repeatEmailAndPhoneCheck(String name,String department,String email,String phone,String id,String isEmailExist,String isPhoneExist,String getPhone){


        if ( isEmailExist.length()>0 && isPhoneExist.length()==0){

            dialog_class.stopLoading();

            Log.d("msg","(update_activity) email "+email+" already exist");
            Toast.makeText(this,email+" already exist " , Toast.LENGTH_SHORT).show();

        }else if(isEmailExist.length()==0 && isPhoneExist.length()>0){

            dialog_class.stopLoading();

            Log.d("msg","(update_activity) phone number "+phone+" already exist ");
            Toast.makeText(this, phone+" already exist", Toast.LENGTH_SHORT).show();

        }else if(isEmailExist.length()>0 && isPhoneExist.length()>0 ){

            dialog_class.stopLoading();

            Log.d("msg","(update_activity) both email "+email+" and phone number "+phone+" already exist ");
            Toast.makeText(this,"both "+email+" and "+phone+" already exist " , Toast.LENGTH_SHORT).show();

        }
        else{


            Log.d("msg","(update_activity) email "+email+" and phone number "+phone+" are unique and ready to update in firestore ");
            updateInFireStore(name,department,email,phone,id,getPhone);


        }
    }





    public void updateInFireStore(String name,String department,String email,String phone,String id,String getPhone){

        DocumentReference documentReference=firestore.collection("profiles").document("departments").collection(department).document(id);
        Map<String,Object>data=new HashMap<>();

        data.put("name",name);
        data.put("department",department);
        data.put("email",email);
        data.put("phone",phone);

        documentReference.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.d("msg","(update_activity) successfully updated in firestore");
                Toast.makeText(UpdateActivity.this, "successfully updated", Toast.LENGTH_SHORT).show();

                findInFavouriteFireStore(name,department,email,phone,getPhone);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog_class.stopLoading();

                Log.d("msg","(update_activity) failed to  updated in firestore "+e);
                Toast.makeText(UpdateActivity.this, "failed to  update", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void findInFavouriteFireStore(String name,String department,String email,String phone,String getPhone){

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("loggedUserData", MODE_PRIVATE);
        String userName = sharedPreferences.getString("name", "");

        firestore.collection("allPersonFavourite").document("favourite").collection(userName).whereEqualTo("phone",getPhone).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if ((!queryDocumentSnapshots.isEmpty())) {

                            DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String id = snapshot.getId();

                            Log.d("msg", "(update_activity) we find this data in favourite firestore ");
                            Log.d("msg", "(update_activity) favourite firestore name is " + name + " department is " + department + " email is " + email + " getPhone is " + getPhone + " id is " + id);

                            updateInFavouriteFireStore(name,department,email,phone,id);

                        }else{

                            dialog_class.stopLoading();

                            Log.d("msg","(update_activity) favourite firestore is empty");
                            Intent intent=new Intent(UpdateActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                        Log.d("msg","(update_activity) failed to retrieve in favourite firestore "+e);
                    }
                });

    }

    public  void updateInFavouriteFireStore(String name,String department,String email,String phone,String id){

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("loggedUserData", MODE_PRIVATE);
        String userName = sharedPreferences.getString("name", "");

        DocumentReference documentReference= firestore.collection("allPersonFavourite").document("favourite").collection(userName).document(id);
        Map<String,Object>data=new HashMap<>();

        data.put("name",name);
        data.put("department",department);
        data.put("email",email);
        data.put("phone",phone);

        documentReference.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                dialog_class.stopLoading();

                Log.d("msg","(update_activity) successfully updated in favourite firestore ");

                Intent intent=new Intent(UpdateActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                        Log.d("msg","(update_activity) failed to update in favourite firestore "+e);
                    }
                });
    }






}
