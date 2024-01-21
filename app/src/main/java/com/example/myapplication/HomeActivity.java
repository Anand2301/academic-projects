package com.example.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements Listeners {
    FloatingActionButton up, add, fav,profile,down;
    ImageView searchBtn, closeBtn;
    EditText search;
    TextView home;
    RecyclerView recyclerView;

    HomeDisplayAdapter adapter;
    final ArrayList<HomeDisplayList> homeDisplayList = new ArrayList<>();
    final  ArrayList<String>favList=new ArrayList<>();

    final ArrayList<String>departmentsList=new ArrayList<>();
  final  int PERMISSION_REQ_CODE=1;

  Custom_dialog_Class dialog_class=new Custom_dialog_Class(HomeActivity.this);


    FirebaseFirestore firestore;
    Boolean doubleTab = false;

    int retrieveAllData_loopCount =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        up = findViewById(R.id.Floating_up);
        add = findViewById(R.id.Floating_add);
        fav = findViewById(R.id.Floating_fav);
        profile=findViewById(R.id.Floating_profile);
        down = findViewById(R.id.Floating_down);
        searchBtn = findViewById(R.id.Home_searchBtn);
        closeBtn = findViewById(R.id.Home_closeBtn);
        search = findViewById(R.id.Home_search);
        home = findViewById(R.id.Home_home);
        recyclerView = findViewById(R.id.Home_recyclerView);

        firestore = FirebaseFirestore.getInstance();

        sharedPreferencesCheck();

        recyclerView.setHasFixedSize(true);

        permissions();

        favAndDepartmentsRetrieve();

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                down.setVisibility(View.VISIBLE);
                profile.setVisibility(View.VISIBLE);
                fav.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                up.setVisibility(View.INVISIBLE);


            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                add.setVisibility(View.INVISIBLE);
                fav.setVisibility(View.INVISIBLE);
                profile.setVisibility(View.INVISIBLE);
                down.setVisibility(View.INVISIBLE);
                up.setVisibility(View.VISIBLE);

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, AddActivity.class);
                startActivity(intent);
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                home.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.INVISIBLE);
                closeBtn.setVisibility(View.VISIBLE);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search.setText("");
                search.setVisibility(View.INVISIBLE);
                home.setVisibility(View.VISIBLE);
                closeBtn.setVisibility(View.INVISIBLE);
                searchBtn.setVisibility(View.VISIBLE);


            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                filter(editable.toString());

            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Intent intent=new Intent(HomeActivity.this,FavouriteActivity.class);
               startActivity(intent);
               finish();


            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (doubleTab) {
            super.onBackPressed();
        } else {
            Log.d("msg", "(home_activity) double tap to exit....");
            Toast.makeText(this, "double tap to exit....", Toast.LENGTH_SHORT).show();
            doubleTab = true;
        }
    }



    public void permissions(){

        if (ContextCompat.checkSelfPermission(HomeActivity.this,Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED){

            Log.d("msg","(home_activity) you already granted permission  for phone ");
        }else{

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)){

                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Permission Required ");
                builder.setMessage("Permission is required for phone");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQ_CODE);

                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();


            }else {

                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQ_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISSION_REQ_CODE){

            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                Log.d("msg","(home_Activity) permission granted");
            }else{

                Log.d("msg","(home_Activity) permission denied");
            }
        }
    }


    public void favAndDepartmentsRetrieve(){

        dialog_class.startLoading();

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("loggedUserData",MODE_PRIVATE);
       String userName=sharedPreferences.getString("name","");

        Task task1 =firestore.collection("allPersonFavourite").document("favourite").collection(userName).get();
        Task task2=firestore.collection("departments").get();

        Task<List<QuerySnapshot>> task= Tasks.whenAllSuccess(task1,task2);

        task.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {

                for (QuerySnapshot queryDocumentSnapshots:querySnapshots){

                    for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                        if(documentSnapshot.getString("collection").equals("fav")) {

                            Log.d("msg", "(home_activity) fav phone is " + documentSnapshot.getString("phone"));
                            favList.add(documentSnapshot.getString("phone"));

                        }
                        if(documentSnapshot.getString("collection").equals("departments")) {

                            Log.d("msg", "(home_activity) department is " + documentSnapshot.getString("department name"));
                            departmentsList.add(documentSnapshot.getString("department name"));
                        }


                    }
                }
               checkFav_Department_HomeDisplay_List();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("msg","(home_activity) failed to retrieve from favourite and departments firestore "+e);
                    }
                });

        }



    public void checkFav_Department_HomeDisplay_List() {

        for(String data1:favList){
            Log.d("msg","(home_activity) favList phone is "+data1);

        }

        for(String data2:departmentsList){
            Log.d("msg","(home_activity) departmentsList department is "+data2);

            retrieveAllData(data2);


        }
    }




        public void retrieveAllData(String department) {

        firestore.collection("profiles").document("departments").collection(department).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!(queryDocumentSnapshots.isEmpty())) {

                            retrieveAllData_loopCount +=1;

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();


                                for (DocumentSnapshot snapshot : list) {

                                    String name = snapshot.getString("name");
                                    String department = snapshot.getString("department");
                                    String email = snapshot.getString("email");
                                    String phone = snapshot.getString("phone");

                                    Log.d("msg", "(home_activity) name is " + name + " " + "department is " + department + " " + "email is " + email + " phone is " + phone);
                                    homeDisplayList.add(new HomeDisplayList(name, email, department, phone));


                                }
                            Log.d("msg","(home_activity) departmentList size is  "+departmentsList.size());
                            Log.d("msg","(home_activity) retrieveAllData_loopCount is  "+ retrieveAllData_loopCount);

                            if (retrieveAllData_loopCount ==departmentsList.size()){

                                Log.d("msg","(home_activity) loop finished ");


                                for(HomeDisplayList data3:homeDisplayList){
                                    Log.d("msg","(home_activity) homeDisplayList name is "+data3.getName()+" homeDisplayList department is "+data3.getDepartment()+" homeDisplayList email is "+data3.getEmail()+" homeDisplayList phone is "+data3.getPhone());

                                }
                                RecyclerView.LayoutManager manager = new LinearLayoutManager(HomeActivity.this);
                                recyclerView(homeDisplayList,HomeActivity.this,manager,recyclerView);

                                dialog_class.stopLoading();


                            }


                        } else {
                            dialog_class.stopLoading();
                            Log.d("msg", "(home_activity) firestore is empty");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                        Log.d("msg", "(home_activity) failed to retrieve in firestore " + e);
                    }
                });

    }





    public void recyclerView(ArrayList<HomeDisplayList>data, Context context, RecyclerView.LayoutManager layoutManager,RecyclerView recyclerView) {

        Log.d("msg","(home_activity) recyclerView is begin...");


        adapter = new HomeDisplayAdapter(data,context, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    public void filter(String searchData) {


        ArrayList<HomeDisplayList> filerList = new ArrayList<>();

        for (HomeDisplayList data : homeDisplayList) {

            if (data.getName().toLowerCase().contains(searchData.toLowerCase()) || data.getPhone().toLowerCase().contains(searchData.toLowerCase()) || data.getDepartment().toLowerCase().contains(searchData.toLowerCase())) {

                filerList.add(data);
            }

        }

        adapter.adapterFilter(filerList);
    }

    public void sharedPreferencesCheck(){

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("loggedUserData",MODE_PRIVATE);

        Log.d("msg","(home_activity) sharedPreference name is "+sharedPreferences.getString("name",""));
        Log.d("msg","(home_activity) sharedPreference email is "+sharedPreferences.getString("email",""));
        Log.d("msg","(home_activity) sharedPreference logged userId is "+sharedPreferences.getString("userId",""));
    }



    @Override
    public void favListener(String name, String department, String email, String phone) {

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("loggedUserData",MODE_PRIVATE);
        String userName=sharedPreferences.getString("name","");


        Log.d("msg", "(home_activity) fav clicked name is " + name);
        Log.d("msg", "(home_activity) fav clicked department is " + department);
        Log.d("msg", "(home_activity) fav clicked email is " + email);
        Log.d("msg", "(home_activity) fav clicked phone is " + phone);

        DocumentReference documentReference=firestore.collection("allPersonFavourite").document("favourite").collection(userName).document();
        Map<String,Object>data=new HashMap<>();

        data.put("collection","fav");
        data.put("name",name);
        data.put("department",department);
        data.put("email",email);
        data.put("phone",phone);

        documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.d("msg","(home_activity) data added in favourite firestore");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("msg","(home_activity) failed to store in favourite firestore "+e);
                    }
                });

    }


    @Override
    public void longPressListener(HomeDisplayList item) {

        dialog_class.startLoading();

            firestore.collection("profiles").document("departments").collection(item.getDepartment()).whereEqualTo("phone",item.getPhone()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            String id;

                            if (!(queryDocumentSnapshots.isEmpty())){

                                id=queryDocumentSnapshots.getDocuments().get(0).getId();


                                Log.d("msg", "(home_activity) long pressed item name is " + item.getName() + " department is " + item.getDepartment() + " email is " + item.getEmail() + " phone is " + item.getPhone()+" firestore id is "+id);

                                dialog_class.stopLoading();

                                Intent intent = new Intent(HomeActivity.this, UpdateActivity.class);
                                intent.putExtra("name", item.getName());
                                intent.putExtra("department", item.getDepartment());
                                intent.putExtra("email", item.getEmail());
                                intent.putExtra("phone", item.getPhone());
                                intent.putExtra("id",id);
                                startActivity(intent);
                                finish();




                            }else{
                                dialog_class.stopLoading();
                                Log.d("msg","(home_activity) firestore is empty");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d("msg","(home_activity) failed to update in firestore "+e);
                        }
                    });






    }

    @Override
    public void favEnable(String phone, View fav, View unFav) {

        favCheck(phone,fav,unFav);
    }

    @Override
    public void favDisable(String item,int position,Context context) {

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("loggedUserData",MODE_PRIVATE);
        String userName=sharedPreferences.getString("name","");

        firestore.collection("allPersonFavourite").document("favourite").collection(userName).whereEqualTo("phone",item).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!(queryDocumentSnapshots.isEmpty())) {

                            DocumentSnapshot data = queryDocumentSnapshots.getDocuments().get(0);
                            String id = data.getId();

                            firestore.collection("allPersonFavourite").document("favourite").collection(userName).document(id).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Log.d("msg", "(home_activity) successfully removed phone  " + item);
                                            favList.remove(item);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.d("msg", "(home_activity) failed to remove in favourite firestore " + e);
                                        }
                                    });

                        }else{
                            Log.d("msg","(home_activity) favourite firestore in empty");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("msg","(home_activity) failed to delete in favourite firestore "+e);
                    }
                });

    }

    @Override
    public void phone(Context context,String phone) {

        if(ContextCompat.checkSelfPermission(context,Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED) {

            makeCall(phone);

        }else{

            Log.d("msg","(home_Activity) please enable permission to make phone call");
        }

    }

    @Override
    public void email(Context context, String email) {

        Log.d("msg","(home_activity) to email address is "+email);

        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{email});
        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent,"choose email client"));

    }

    @Override
    public void share(Context context, String name, String email,String department, String phone) {

    String information="name: "+name+"\n"+"email: "+email+"\n"+"department: "+department+"\n"+"phone: "+phone;


        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,information);
        intent.setType("text/plain");

        startActivity(Intent.createChooser(intent,"choose share via"));
    }

    public void makeCall(String phone){


            String dail="tel:"+phone;
            Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse(dail));
            startActivity(intent);

    }


    public void favCheck(String phone, View fav, View unFav){

        for(String data:favList){

            if(data.equals(phone)){
                Log.d("msg","(home_activity) we find phone in  favourite "+data);
                unFav.setVisibility(View.INVISIBLE);
                fav.setVisibility(View.VISIBLE);

            }
        }

    }











}