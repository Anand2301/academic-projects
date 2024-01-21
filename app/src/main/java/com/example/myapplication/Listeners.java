package com.example.myapplication;

import android.content.Context;
import android.view.View;

public interface Listeners {

    void favListener(String name,String department,String email,String phone);
    void longPressListener(HomeDisplayList item);

    void favEnable(String phone,View fav,View unFav);

    void favDisable(String item,int position,Context context);

    void phone(Context context,String phone);
    void email(Context context,String email);
    void share(Context context,String name,String email,String department,String phone);


}



