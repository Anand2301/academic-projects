package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import java.lang.reflect.Constructor;

public class Custom_dialog_Class {


    Context context;

    AlertDialog.Builder builder;
    Dialog dialog;

    public Custom_dialog_Class(Context context) {
        this.context = context;
    }


    public  void startLoading(){

        View view=LayoutInflater.from(context).inflate(R.layout.custom_dialog,null);
        builder=new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);

      dialog=builder.create();
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
      dialog.show();




    }

    public void stopLoading(){

       dialog.dismiss();

    }
}
