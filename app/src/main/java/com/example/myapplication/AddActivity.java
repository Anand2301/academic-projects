package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddActivity extends AppCompatActivity {
    EditText name,department,email,phone;
    Button addBtn;
    FirebaseFirestore firestore;

    Custom_dialog_Class dialog_class=new Custom_dialog_Class(AddActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        name=findViewById(R.id.add_name);
        department=findViewById(R.id.add_department);
        email=findViewById(R.id.add_email);
        phone=findViewById(R.id.add_phone);
        addBtn=findViewById(R.id.add_btn);

        firestore=FirebaseFirestore.getInstance();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog_class.startLoading();

                String getName=name.getText().toString().trim();
                String getDepartment=department.getText().toString().toLowerCase().trim();
                String getEmail=email.getText().toString().trim();
                String getPhone=phone.getText().toString().trim();

                filedCheck(getName,getDepartment,getEmail,getPhone);
            }
        });



    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent(AddActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void filedCheck(String name, String department, String email, String phone){


        if(name(name) && department(department) && email(email) && phone(phone)){

            retrieveEmailAndPhone(name,department,email,phone);

        }else{
            dialog_class.stopLoading();
        }

    }


    public Boolean name(String name){

        if(name.length()!=0){
            Log.d("msg","(add_activity) name is "+name);
            return true;
        }else{
            Log.d("msg","(add_activity) name filed can't be empty");
            Toast.makeText(this, "name filed can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public Boolean department(String department){

        if(department.length()!=0){

           Log.d("msg","(add_activity) department is "+department);
            return true;
        }else{
            Log.d("msg","(add_activity) department filed can't be empty");
            Toast.makeText(this, "department filed can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


    }
    public Boolean email(String email){

        if(email.length()!=0){
            if(email.endsWith("@gmail.com")) {

                Log.d("msg", "(add_activity) email is " + email);
                return true;
            }else {
                Log.d("msg","(add_activity) please enter valid email address");
                Toast.makeText(this, "please enter valid email address", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Log.d("msg","(add_activity) email filed can't be empty");
            Toast.makeText(this, "email filed can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public Boolean phone(String phone){

        if(phone.length()!=0){

            if(phone.length()==10) {
                Log.d("msg", "(add_activity) phone is " + phone);
                return true;
            }else{
                Log.d("msg","(add_activity) phone number must be 10 length");
                Toast.makeText(this, "phone number must be 10", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Log.d("msg","(add_activity) phone filed can't be empty");
            Toast.makeText(this, "phone filed can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public void retrieveEmailAndPhone(String name,String department,String email,String phone){


        firestore.collection("profiles").document("departments").collection(department).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        String repeatedEmail="";
                        String repeatedPhone="";

                        if(!(queryDocumentSnapshots.isEmpty())){

                            List<DocumentSnapshot>list=queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot snapshot:list){

                                if(email.equals(snapshot.getString("email"))){

                                   repeatedEmail=email;


                                }
                                if(phone.equals(snapshot.getString("phone"))){

                                   repeatedPhone=phone;

                                }

                            }

                            repeatedFiledCheck(name,department,email,phone,repeatedEmail,repeatedPhone);

                        }else{

                            Log.d("msg","(add_activity) firstore is empty");
                                retrieveDepartment(name,department,email,phone);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                        Log.d("msg","(add_activity) failed to load data from firestore "+e);
                    }
                });
    }

    public void repeatedFiledCheck(String name,String department,String email, String phone,String repeatedEmail,String repeatedPhone){

        if(repeatedEmail.length()>0 && repeatedPhone.length()==0){

            dialog_class.stopLoading();
            Log.d("msg","(add_activity) email "+email+" exist");
            Toast.makeText(this, email+" already exist", Toast.LENGTH_SHORT).show();

        }else if(repeatedEmail.length()==0 && repeatedPhone.length()>0){

            dialog_class.stopLoading();
            Log.d("msg","(add_activity) phone "+phone+" exist");
            Toast.makeText(this, phone+"already exist", Toast.LENGTH_SHORT).show();

        }else if (repeatedEmail.length()>0 && repeatedPhone.length()>0){

            dialog_class.stopLoading();
            Log.d("msg","(add_activity) both email "+email+" and "+phone+" phone is exist");
            Toast.makeText(this,"both "+email+" and "+phone+" already exist" , Toast.LENGTH_SHORT).show();

        }else{

            Log.d("msg","(add_activity) name "+name+" is ready to store in firestore");
            Log.d("msg","(add_activity) department "+department+" is ready to store in firestore");
            Log.d("msg","(add_activity) email "+email+" is ready to store in firestore");
            Log.d("msg","(add_activity) phone "+phone+" is ready to store in firestore");
            retrieveDepartment(name,department,email,phone);


        }


    }

    public void  retrieveDepartment(String name,String department,String email,String phone){

        firestore.collection("departments").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        String getDepartment="";

                        if (!(queryDocumentSnapshots.isEmpty())){

                            List<DocumentSnapshot>list=queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot snapshot:list){

                                if(department.equals(snapshot.getString("department name"))){

                                    getDepartment="isExist";
                                    break;
                                }

                            }

                            isDepartmentExist(name,department,email,phone,getDepartment);


                        }else{

                            Log.d("msg","(add_activity) departments  is empty in firestore");
                            storeDepartmentInFireStore(name,department,email,phone);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                        Log.d("msg","(add_activity) failed to retrieve departments friestore "+e);
                    }
                });

    }

    public void isDepartmentExist(String name,String department,String email,String phone,String isExist){

        if(isExist.equals("isExist")){


            Log.d("msg","(add_activity) department name already exist in firestore");
            storeInFireStore(name,department,email,phone);
        }else{

            Log.d("msg","(add_activity) department name does not  exist in firestore");
            storeDepartmentInFireStore(name,department,email,phone);
        }

    }

    public void storeDepartmentInFireStore(String name, String department, String email, String phone){

        DocumentReference documentReference=firestore.collection("departments").document();
        Map<String,Object>data=new HashMap<>();

        data.put("collection","departments");
        data.put("department name",department);

        documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.d("msg","(add_activity) successfully stored in departments firestore");
                storeInFireStore(name,department,email,phone);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();
                        Log.d("msg","(add_activity) failed to store in departments firestore "+e);
                    }
                });

    }

    public void storeInFireStore(String name,String department,String email,String phone){

       DocumentReference documentReference= firestore.collection("profiles").document("departments").collection(department).document();
        Map<String,Object>data=new HashMap<>();

        data.put("name",name);
        data.put("department",department);
        data.put("email",email);
        data.put("phone",phone);

        documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                dialog_class.stopLoading();

                Log.d("msg","(add_activity) successfully stored in firestore ");
                Toast.makeText(AddActivity.this, "successfully added", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AddActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                        Log.d("msg","(add_activity) failed to  stored in firestore "+e);
                        Toast.makeText(AddActivity.this, "failed to add", Toast.LENGTH_SHORT).show();

                    }
                });
    }



}
