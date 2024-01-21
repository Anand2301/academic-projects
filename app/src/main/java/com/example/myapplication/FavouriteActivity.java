package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavouriteActivity extends AppCompatActivity implements Listeners{

    RecyclerView recyclerView;

    RecyclerView.LayoutManager manager = new LinearLayoutManager(FavouriteActivity.this);

    HomeDisplayAdapter adapter;

    final ArrayList<HomeDisplayList> favouriteLists = new ArrayList<>();

    FirebaseFirestore firestore;

    Custom_dialog_Class dialog_class=new Custom_dialog_Class(FavouriteActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        recyclerView = findViewById(R.id.favourite_recyclerView);

        firestore = FirebaseFirestore.getInstance();

        dialog_class.startLoading();

        retrieveFavourite();


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(FavouriteActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void retrieveFavourite() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("loggedUserData", MODE_PRIVATE);
        String userName = sharedPreferences.getString("name", "");

        firestore.collection("allPersonFavourite").document("favourite").collection(userName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!(queryDocumentSnapshots.isEmpty())) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot snapshot : list) {

                                String name = snapshot.getString("name");
                                String department = snapshot.getString("department");
                                String email = snapshot.getString("email");
                                String phone = snapshot.getString("phone");

                                Log.d("msg", "(favourite_activity) fav name is " + name + " fav department is " + department + " fav email is " + email + " fav phone is " + phone);

                                favouriteLists.add(new HomeDisplayList(name, email, department, phone));
                            }


                            recyclerView(favouriteLists,FavouriteActivity.this,manager,recyclerView);
                            dialog_class.stopLoading();


                        } else {

                            dialog_class.stopLoading();

                            Log.d("msg", "(favourite_activity) favourite firestore is empty ");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        dialog_class.stopLoading();

                        Log.d("msg", "(favourite_activity) failed to retrieve in favourite firestore " + e);
                    }
                });
    }

    public void recyclerView(ArrayList<HomeDisplayList>data, Context context, RecyclerView.LayoutManager layoutManager, RecyclerView recyclerView) {

        Log.d("msg","(home_activity) recyclerView is begin...");


        adapter = new HomeDisplayAdapter(data,context, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void favListener(String name, String department, String email, String phone) {

//        TODO:This method is  no useful for this activity(FavouriteActivity),so leave it.
    }

    @Override
    public void longPressListener(HomeDisplayList item) {

        Log.d("msg", "(favourite_activity) long pressed item name is " + item.getName() + " department is " + item.getDepartment() + " email is " + item.getEmail() + " phone is " + item.getPhone());
        Intent intent = new Intent(FavouriteActivity.this, UpdateActivity.class);
        intent.putExtra("name", item.getName());
        intent.putExtra("department", item.getDepartment());
        intent.putExtra("email", item.getEmail());
        intent.putExtra("phone", item.getPhone());
        startActivity(intent);
        finish();

    }

    @Override
    public void favEnable(String phone, View fav, View unFav) {

        favCheck(phone,fav,unFav);
    }

    @Override
    public void favDisable(String item,int position,Context context) {

        favouriteLists.remove(position);
        recyclerView(favouriteLists,FavouriteActivity.this,manager,recyclerView);

        Log.d("msg", "(favourite_activity) successfully removed phone from recyclerview " + item);


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

                                            Log.d("msg", "(favourite_activity) successfully removed from favourite firestore ");
                                            Toast.makeText(context, "successfully removed from favourite", Toast.LENGTH_SHORT).show();



                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.d("msg", "(favourite_activity) failed to remove in favourite firestore " + e);
                                        }
                                    });

                        }else{
                            Log.d("msg","(favourite_activity) favourite firestore in empty");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("msg","(favourite_activity) failed to delete in favourite firestore "+e);
                    }
                });

    }

    @Override
    public void phone(Context context,String phone) {

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED) {

            makeCall(phone);

        }else{

            Log.d("msg","(favourite_Activity) please enable permission to make phone call");
        }
    }

    @Override
    public void email(Context context, String email) {

        Log.d("msg","(favourite_activity) to email address is "+email);

        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{email});
        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent,"choose email client"));

    }

    @Override
    public void share(Context context, String name,String email,String department, String phone) {

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

    public void favCheck(String phone,View fav,View unFav){

        for(HomeDisplayList data:favouriteLists){

            if(data.getPhone().equals(phone)){
                Log.d("msg","(favourite_activity) we find phone in  favourite "+data.getPhone());
                unFav.setVisibility(View.INVISIBLE);
                fav.setVisibility(View.VISIBLE);

            }
        }

    }
}











