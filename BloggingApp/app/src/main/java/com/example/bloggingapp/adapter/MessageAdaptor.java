package com.example.bloggingapp.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bloggingapp.R;
import com.example.bloggingapp.listeners.OnItemCLick;
import com.example.bloggingapp.listeners.OnLongPress;
import com.example.bloggingapp.model.MessageDataModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.Vh> {
    List<MessageDataModel> list;
    FragmentActivity context;
    OnItemCLick onItemCLick;
    OnLongPress onLongPress;
    ArrayList<Integer> selected;
    NavController navController;

    public List<MessageDataModel> getList() {
        return list;
    }

    public void setList(List<MessageDataModel> list) {
        this.list = list;
    }

    public MessageAdaptor(List<MessageDataModel> list, FragmentActivity context, OnItemCLick itemCLick, OnLongPress onLongPress, NavController fragment) {
        this.list = list;
        this.context = context;
        this.onItemCLick = itemCLick;
        this.onLongPress = onLongPress;
        selected = new ArrayList<>();
        this.navController = fragment;
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_messages, parent, false);
        return new Vh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        MessageDataModel message = list.get(position);
        holder.tvTitle.setText(message.getMessageTitle());
        holder.tvDetails.setText(message.getDetail());
        holder.tvTime.setText(new SimpleDateFormat("hh:mm dd/MM/yyyy").format(new Date(Long.parseLong(message.getTime()))));
        if (!message.getImage_uri().equals("")){
            Glide.with(context).load(message.getImage_uri()).into(holder.ivImage);
        }
        if (message.isSelected()) {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_selected));
        } else {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_drawable));
        }

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemCLick.onCLick(position, view, holder.ivImage);
            }
        });

        holder.itemView.setOnClickListener(view -> {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_drawable));
            Bundle bundle = new Bundle();
            bundle.putSerializable("DATA", message);
            navController.navigate(R.id.action_homeFragment_to_viewDetailsFragment, bundle);


        });

        holder.itemView.setOnLongClickListener(view -> {
            onLongPress.longPress(position);
            if (message.isSelected()) {
                message.setSelected(false);
            } else {
                message.setSelected(true);
            }
            notifyItemChanged(position);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetails, tvTime;
        ImageView ivImage;
        ImageButton imageButton;

        public Vh(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDetails = (TextView) itemView.findViewById(R.id.tvDetails);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            imageButton = itemView.findViewById(R.id.menu);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
