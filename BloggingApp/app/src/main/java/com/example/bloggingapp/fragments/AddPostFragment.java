package com.example.bloggingapp.fragments;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.bloggingapp.R;
import com.example.bloggingapp.db.DatabaseHelper;
import com.example.bloggingapp.model.MessageDataModel;
import com.github.dhaval2404.imagepicker.ImagePicker;

public class AddPostFragment extends Fragment {
    EditText etTitle, etDetails;
    AppCompatButton btnAdd;
    String imageUri = "";
    ImageView ivImage, ivPhoto;
    CardView clImage;
    int PICK_IMAGE_GALLERY = 124;
    MessageDataModel message;
    DatabaseHelper helper;
    TextView textView;
    LinearLayout llUpload;

    public AddPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        etTitle = view.findViewById(R.id.etTitle);
        etDetails = view.findViewById(R.id.etDetails);
        ivImage = view.findViewById(R.id.ivImage);
        ivPhoto = view.findViewById(R.id.ivPhoto);
        btnAdd = view.findViewById(R.id.btnAdd);
        clImage = view.findViewById(R.id.cvPhoto);
        textView = view.findViewById(R.id.label_titel);
        llUpload = view.findViewById(R.id.llUpload);


        if (getArguments() != null) {
            textView.setText("Edit Post");
            message = (MessageDataModel) getArguments().getSerializable("DATA");
            etTitle.setText(message.getMessageTitle());
            etDetails.setText(message.getDetail());
            ivPhoto.setVisibility(View.VISIBLE);
            llUpload.setVisibility(View.GONE);
            Glide.with(requireActivity()).load(message.getImage_uri()).into(ivPhoto);
            imageUri = message.getImage_uri();
            btnAdd.setText("Edit");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        helper = new DatabaseHelper(requireContext());

        clImage.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_EXTERNAL_STORAGE}, 20);
            ImagePicker.with(this)
                    .start();
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString();
                String details = etDetails.getText().toString();
                Long time = System.currentTimeMillis();

                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter title", Toast.LENGTH_SHORT).show();
                } else if (details.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter details", Toast.LENGTH_SHORT).show();
                } else {
                    if (message == null) {
                        helper.AddMessage(new MessageDataModel(title, details, time.toString(), imageUri));

                    } else {
                        helper.updateMessage(new MessageDataModel(message.getId(), title, details, time.toString(), imageUri));
                    }
                    findNavController(AddPostFragment.this).navigateUp();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Getting Gallery Image uri
            Uri uriImage = data.getData();
            Log.e("TAG", "onActivityResult: " + uriImage);

            try {
                ivPhoto.setImageURI(uriImage);
                imageUri = uriImage.toString();
                llUpload.setVisibility(View.GONE);
                ivPhoto.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}