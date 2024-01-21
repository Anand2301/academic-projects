package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText name, email, password;
    Button btn;
    TextView signUpToSignIn;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    Custom_dialog_Class dialog_class=new Custom_dialog_Class(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        btn = findViewById(R.id.signup_btn);
        signUpToSignIn=findViewById(R.id.signup_to_signin);


        firestore = FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


        signUpToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputName = name.getText().toString();
                String inputEmail = email.getText().toString();
                String inputPassword = password.getText().toString();

                dialog_class.startLoading();
                fieldsCheck(inputName, inputEmail, inputPassword);
            }
        });


    }


    public void fieldsCheck(String name, String email, String password) {

        if (name(name) && email(email) && password(password)) {

           retrievingNameAndEmail(name,email,password);


        }else{
            dialog_class.stopLoading();
        }
    }


    public Boolean name(String name) {

        if (name.length() != 0) {

            Log.d("msg", "(signUp_activity) name is " + name);
            return true;

        } else {
            Log.d("msg", "(signUp_activity) name field can't be empty");
            Toast.makeText(this, "name field can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public Boolean email(String email) {

        if (email.length() != 0) {

            if(email.endsWith("@gmail.com")) {

                Log.d("msg", "(signUp_activity) email is " + email);
                return true;

            }else{
                Log.d("msg","(signUp_activity) please enter valid email address");
                Toast.makeText(this, "please enter valid email address", Toast.LENGTH_SHORT).show();

                return false;
            }

        } else {
            Log.d("msg", "(signUp_activity) email field can't be empty");
            Toast.makeText(this, "email field can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public Boolean password(String password) {

        if (password.length() != 0) {

            if (password.length() >= 6) {

                Log.d("msg", "(signUp_activity) password is " + password);
                return true;

            } else {

                Log.d("msg", "(signUp_activity) password must be grater than 6");
                Toast.makeText(this, "password must be grater than 6", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            Log.d("msg", "(signUp_activity) password can't be empty");
            Toast.makeText(this, "password can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

    }


    public void retrievingNameAndEmail(String name,String email,String password) {

        firestore.collection("signedPeople").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                String repeatedName="";
                String repeatedEmail="";

                if(!(queryDocumentSnapshots.isEmpty())){

                    List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot snapshot:list){

                        if(name.equals(snapshot.getString("name"))){

                            repeatedName=name;


                        }
                        if(email.equals(snapshot.getString("email"))){

                            repeatedEmail=email;

                        }

                    }

                        repeatedFiledCheck(name, email, repeatedName, repeatedEmail, password);

                }else{

                    Log.d("msg","(signUp_activity) firestore is empty");

                    createAuthAndStoreInFireStore(name,email,password);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog_class.stopLoading();

                Log.d("msg","(signUp_activity) failed to retrieve in firestore "+e);

            }
        });



    }

    public void repeatedFiledCheck(String name, String email, String repeatedName, String repeatedEmail, String password){

        if(repeatedName.length()==0 && repeatedEmail.length()>0){

            dialog_class.stopLoading();
            Log.d("msg","(signUp_activity) email "+email+" exist");
            Toast.makeText(this, email+" already exist", Toast.LENGTH_SHORT).show();

        }else if(repeatedName.length()>0 && repeatedEmail.length()==0){

            dialog_class.stopLoading();
            Log.d("msg","(signUp_activity) name "+name+" exist");
            Toast.makeText(this, name+" already exist", Toast.LENGTH_SHORT).show();

        }else if (repeatedName.length()>0 && repeatedEmail.length()>0){

            dialog_class.stopLoading();
            Log.d("msg","(signUp_activity) both name "+name+" and "+email+" email is exist");
            Toast.makeText(this, "both "+name+" and "+email+" already exist", Toast.LENGTH_SHORT).show();

        }else{

            Log.d("msg","(signUp_activity) name "+name+" is ready to store in firestore");
            Log.d("msg","(signUp_activity) email "+email+" is ready to store in firestore");

            createAuthAndStoreInFireStore(name,email,password);
        }


    }

    public void createAuthAndStoreInFireStore(String name,String email, String password){

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Log.d("msg","(signUp_activity) successfully created account");
                String  id=firebaseAuth.getCurrentUser().getUid();
                storeInFireStore(name,email,password,id);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog_class.stopLoading();
                Log.d("msg","(signUp_activity) failed to create account");
            }
        });


    }

    public void storeInFireStore(String name, String email, String password,String documentId) {

        DocumentReference documentReference = firestore.collection("signedPeople").document(documentId);
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("password", password);

        documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.d("msg","(signUp_activity) successfully stored in firestore");

                FirebaseUser user=firebaseAuth.getCurrentUser();
                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Log.d("msg","(signUp_activity) verification email sended");

                            Toast.makeText(MainActivity.this, "verification email has been sended", Toast.LENGTH_SHORT).show();

                            dialog_class.stopLoading();

                            Intent intent=new Intent(MainActivity.this,SignInActivity.class);
                            startActivity(intent);
                            finish();

                        }else{

                            dialog_class.stopLoading();
                            Log.d("msg","(signUp_activity) failed to send verification email ");

                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog_class.stopLoading();

                Log.d("msg","(signUp_activity) failed to store in firestore "+e);
            }
        });
    }


}