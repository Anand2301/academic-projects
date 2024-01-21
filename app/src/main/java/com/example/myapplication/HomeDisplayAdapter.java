package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public  class HomeDisplayAdapter extends RecyclerView.Adapter<HomeDisplayAdapter.HomeDisplayViewHolder> {

    private  ArrayList<HomeDisplayList> homeDisplayLists=new ArrayList<>();
    Context context;

    Listeners myListener;


    public static class HomeDisplayViewHolder extends RecyclerView.ViewHolder{

        TextView name,email,department,phone;
        ImageView emailBtn,phoneBtn, unFavouriteBtn,favouriteBtn,shareBtn;
        CardView myCard;

       RelativeLayout relativeLayout;
        public HomeDisplayViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.homeDisplay_name);
            email=itemView.findViewById(R.id.homeDisplay_email);
            department=itemView.findViewById(R.id.homeDisplay_department);
            phone=itemView.findViewById(R.id.homeDisplay_phone);
            myCard=itemView.findViewById(R.id.my_card);
            emailBtn=itemView.findViewById(R.id.homeDisplay_emailBtn);
            phoneBtn=itemView.findViewById(R.id.homeDisplay_phoneBtn);
            unFavouriteBtn =itemView.findViewById(R.id.homeDisplay_unFavouriteBtn);
            favouriteBtn=itemView.findViewById(R.id.homeDisplay_favouriteBtn);
            shareBtn=itemView.findViewById(R.id.home_display_shareBtn);
            relativeLayout=itemView.findViewById(R.id.homeDisplay_intent_layout);



        }


    }

    public HomeDisplayAdapter(ArrayList<HomeDisplayList>homeDisplayLists, Context context, Listeners myListener){

        this.homeDisplayLists=homeDisplayLists;
        this.context=context;
        this.myListener=myListener;



    }


    @NonNull
    @Override
    public HomeDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_homedisplay,parent,false);

        HomeDisplayViewHolder homeDisplayViewHolder=new HomeDisplayViewHolder(view);

        return homeDisplayViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeDisplayViewHolder holder, int position) {

        int index=position;

        HomeDisplayList item=homeDisplayLists.get(index);
        holder.name.setText(item.getName());
        holder.email.setText(item.getEmail());
        holder.department.setText(item.getDepartment());
        holder.phone.setText(item.getPhone());


        myListener.favEnable(item.getPhone(),holder.favouriteBtn,holder.unFavouriteBtn);



        holder.myCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                myListener.longPressListener(item);

                return false;
            }
        });

        holder.myCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.relativeLayout.getVisibility()==View.GONE){

                    holder.relativeLayout.setVisibility(View.VISIBLE);

                }else if(holder.relativeLayout.getVisibility()==View.VISIBLE){

                    holder.relativeLayout.setVisibility(View.GONE);

                }

            }
        });

        holder.favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.favouriteBtn.setVisibility(View.INVISIBLE);
                holder.unFavouriteBtn.setVisibility(View.VISIBLE);

                myListener.favDisable(item.getPhone(),index,context);

            }
        });

        holder.unFavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.unFavouriteBtn.setVisibility(View.INVISIBLE);
                holder.favouriteBtn.setVisibility(View.VISIBLE);
                myListener.favListener(item.getName(),item.getDepartment(),item.getEmail(),item.getPhone());

            }
        });

        holder.phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myListener.phone(context,item.getPhone());

            }
        });

        holder.emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myListener.email(context,item.getEmail());
            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myListener.share(context,item.getName(),item.getEmail(),item.getDepartment(),item.getPhone());

            }
        });
    }

    @Override
    public int getItemCount() {
        return homeDisplayLists.size();
    }

    public void adapterFilter(ArrayList<HomeDisplayList> list){

        homeDisplayLists=list;
        notifyDataSetChanged();

    }




}
