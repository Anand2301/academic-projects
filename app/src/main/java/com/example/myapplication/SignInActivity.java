package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SignInActivity extends AppCompatActivity {
    EditText email, password;
    Button signBtn;
    TextView signInToSignUp;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    Custom_dialog_Class dialog_class=new Custom_dialog_Class(SignInActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        email = findViewById(R.id.signin_email);
        password = findViewById(R.id.signin_password);
        signBtn = findViewById(R.id.signin_btn);
        signInToSignUp = findViewById(R.id.signin_to_signup);

        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();



        isLoggedOrNot();


        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputEmail=email.getText().toString();
                String inputPassword=password.getText().toString();

                dialog_class.startLoading();

                fieldsCheck(inputEmail,inputPassword);

            }
        });

        signInToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    public void fieldsCheck(String email, String password) {

        if (email(email) && password(password)) {

           checkEmailAndPassword(email,password);


        }else{

            dialog_class.stopLoading();
        }
    }


    public Boolean email(String email) {

        if (email.length() != 0) {

            if(email.endsWith("@gmail.com")) {

                Log.d("msg", "(signIn_Activity) email is " + email);
                return true;
            }else{
                Log.d("msg","(signIn_activity) please enter valid email address");
                Toast.makeText(this, "please enter valid email address", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            Log.d("msg", "(signIn_Activity) email field can't be empty");
            Toast.makeText(this, "email field can't be empty", Toast.LENGTH_SHORT).show();

            return false;
        }

    }

    public Boolean password(String password) {

        if (password.length() != 0) {

            if (password.length() >= 6) {

                Log.d("msg", "(signIn_Activity) password is " + password);
                return true;

            } else {

                Log.d("msg", "(signIn_Activity) password must be grater than 6");
                Toast.makeText(this, "password must be grater than 6", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            Log.d("msg", "(signIn_Activity) password can't be empty");
            Toast.makeText(this, "password can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void  checkEmailAndPassword(String email,String password){

        firestore.collection("signedPeople").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        String isEmailExists="";
                        String isPasswordExists="";

                        List<DocumentSnapshot>list=queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot snapshot:list){

                            String fEmail=snapshot.getString("email");
                            String fPassword=snapshot.getString("password");

                            Log.d("msg","(signIn_Activity) fEmail is "+fEmail);
                            Log.d("msg","(signIn_Activity) fPassword is "+fPassword);

                            if (fEmail.equals(email) && !(fPassword.equals(password))){

                                isEmailExists="yes";
                                Log.d("msg","(signIn_Activity) email is correct ");

                                break;

                            }

                            if (fEmail.equals(email) && fPassword.equals(password)){

                                isEmailExists="yes";
                                isPasswordExists="yes";
                                Log.d("msg","(signIn_Activity) both email and password is correct ");

                                break;

                            }



                        }

                        Log.d("msg","(signIn_Activity) isEmailExists is "+isEmailExists);
                        Log.d("msg","(signIn_Activity) isPasswordExists  is  "+isPasswordExists);

                        if (isEmailExists.length()==0){

                            dialog_class.stopLoading();

                            Log.d("msg","(signIn_Activity) Invalid email address ");
                            Toast.makeText(SignInActivity.this, " Invalid email address ", Toast.LENGTH_SHORT).show();

                        } else if(isPasswordExists.length()==0){

                            dialog_class.stopLoading();

                            Log.d("msg","(signIn_Activity) Invalid password ");
                            Toast.makeText(SignInActivity.this, " Invalid password ", Toast.LENGTH_SHORT).show();

                        }else if (isEmailExists.length()>0 && isPasswordExists.length()>0){

                            checkEmailVerificationAndSignIn(email,password);


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                        Log.d("msg", "(signIn_Activity) failed to retrieve data from signedPeople firestore  "+e );

                    }
                });

    }


    public void checkEmailVerificationAndSignIn(String email,String password){

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                FirebaseUser user=firebaseAuth.getCurrentUser();

                if(user.isEmailVerified()) {

                    loggedUserDetails();

                }else{
                    dialog_class.stopLoading();

                    Log.d("msg","(signIn_Activity) please verify the email first ");
                    Toast.makeText(SignInActivity.this, " please verify the email first ", Toast.LENGTH_SHORT).show();
                }

            }

        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                       Log.d("msg","(signIn_Activity) failed to signIn "+e);

                    }
                });


    }


    public void  loggedUserDetails(){



        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("loggedUserData",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        firestore.collection("signedPeople").document(firebaseAuth.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String name=documentSnapshot.getString("name");
                        String email=documentSnapshot.getString("email");

                        editor.putString("name",name);
                        editor.putString("email",email);
                        editor.putString("state","logged");
                        editor.putString("userId",firebaseAuth.getUid());
                        editor.commit();

                        Log.d("msg", "(signIn_Activity) successfully signed");
                        Toast.makeText(SignInActivity.this, "successfully signed", Toast.LENGTH_SHORT).show();

                        dialog_class.stopLoading();

                        Intent intent=new Intent(SignInActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                        Log.d("msg","(signIn_activity) failed to get logged user data from firestore "+e);
                    }
                });

    }

    public void isLoggedOrNot(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("loggedUserData",MODE_PRIVATE);
       String loggedOrNot= sharedPreferences.getString("state","");

       if (loggedOrNot.equals("logged")){

           Intent intent=new Intent(this,HomeActivity.class);
           startActivity(intent);
           finish();
       }

    }
}