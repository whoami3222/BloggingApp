package com.example.bloggingapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.bloggingapp.R;
import com.example.bloggingapp.model.MessageDataModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ViewDetailsFragment extends Fragment {
    TextView tvTitle, tvDetails;
    ImageView imgMessage;
    ImageButton imgShare, imgBack;
    MessageDataModel message;

    public ViewDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_details, container, false);
        tvTitle = view.findViewById(R.id.etTitle);
        tvDetails = view.findViewById(R.id.etDetails);
        imgMessage = view.findViewById(R.id.ivImage);
        imgShare = view.findViewById(R.id.btnShare);
        imgBack = view.findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        message = (MessageDataModel) getArguments().getSerializable("DATA");
        tvTitle.setText("Title: "+message.getMessageTitle());
        tvDetails.setText("Description: "+message.getDetail());
        if (!message.getImage_uri().equals("")){
            Glide.with(requireContext()).load(message.getImage_uri()).into(imgMessage);
        }

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = imgMessage.getDrawable();
                Bitmap bitmap = null;
                if (drawable instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                }
                if (bitmap != null) {
                    // Save the bitmap to a temporary file (optional)
                    String path = saveBitmapToTempFile(bitmap);

                    // Create a content URI for the file
                    Uri contentUri = FileProvider.getUriForFile(requireContext(), requireActivity().getPackageName()+".fileprovider", new File(path));

                    // Share the Bitmap via Intent
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    String titleAndDetails = "Title: " + message.getMessageTitle() + "\nDescription: " + message.getDetail();
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, titleAndDetails);

                    startActivity(Intent.createChooser(shareIntent, "Share Image"));
                }else{
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    String titleAndDetails = "Title: " + message.getMessageTitle() + "\nDescription: " + message.getDetail();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, titleAndDetails);
                    startActivity(Intent.createChooser(shareIntent, "Share Image"));
                }
            }
        });

        return view;
    }

    private String saveBitmapToTempFile(Bitmap bitmap) {
        try {
            File tempFile = File.createTempFile("temp_image", ".png", requireActivity().getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}