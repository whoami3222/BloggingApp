package com.example.bloggingapp.fragments;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloggingapp.R;
import com.example.bloggingapp.adapter.MessageAdaptor;
import com.example.bloggingapp.db.DatabaseHelper;
import com.example.bloggingapp.listeners.OnItemCLick;
import com.example.bloggingapp.listeners.OnLongPress;
import com.example.bloggingapp.model.MessageDataModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    RecyclerView listView;
    DatabaseHelper helper;
    MessageAdaptor adaptor;
    OnItemCLick onItemCLick;
    OnLongPress onLongPress;
    ImageButton btnDelete;
    EditText etSearch;
    List<MessageDataModel> list = new ArrayList<>();
    List<MessageDataModel> selectedPosition = new ArrayList<>();

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        helper = new DatabaseHelper(getContext());
        etSearch = view.findViewById(R.id.etSearch);
        listView = view.findViewById(R.id.lt_messages);
        btnDelete = view.findViewById(R.id.btnDel);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // btnAdd.setOnClickListener(view -> startActivity(new Intent(requireContext(), AddMessageActivity.class)));
        list = helper.getALlMessages();
        if (list == null) {
            list = new ArrayList<>();
        }
        onItemCLick = (position, view1, imageView) -> showMenu(requireContext(), view1, position, imageView);

        if (selectedPosition.isEmpty()) {
            btnDelete.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.VISIBLE);
        }


        onLongPress = position -> {
            btnDelete.setVisibility(View.VISIBLE);
            if (!selectedPosition.contains(position)) {
                selectedPosition.add(list.get(position));
            } else {
                selectedPosition.remove(list.get(position));
            }

            if (selectedPosition.isEmpty()) {
                btnDelete.setVisibility(View.GONE);
            }
        };

        listView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adaptor = new MessageAdaptor(list, requireActivity(), onItemCLick, onLongPress, findNavController(HomeFragment.this));
        listView.setAdapter(adaptor);


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String search = editable.toString();
                if (!search.isEmpty()) {
                    List<MessageDataModel> newList = new ArrayList<>();
                    for (MessageDataModel message : list) {
                        if (message.getMessageTitle().toLowerCase().contains(search.toLowerCase())) {
                            newList.add(message);
                        }
                    }
                    adaptor.setList(newList);
                    adaptor.notifyDataSetChanged();
                } else {
                    adaptor.setList(list);
                    adaptor.notifyDataSetChanged();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedPosition.isEmpty()) {
                    for (int i = 0; i < selectedPosition.size(); i++) {
                        helper.deleteMessage((selectedPosition.get(i)));
                        list.remove(selectedPosition.get(i));
                        adaptor.notifyDataSetChanged();
                    }
                }
                btnDelete.setVisibility(View.GONE);

            }
        });


    }

    private void showMenu(Context context, View view, int adapPosition, ImageView imageView) {
        PowerMenu menu = new PowerMenu.Builder(context).build();
        menu.addItem(new PowerMenuItem("Edit"));
        menu.addItem(new PowerMenuItem("Delete"));
        menu.addItem(new PowerMenuItem("Share"));
        menu.addItem(new PowerMenuItem("Facebook Timeline"));
        menu.showAsAnchorLeftBottom(view);
        menu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
            @Override
            public void onItemClick(int position, PowerMenuItem item) {
                if (position == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DATA", list.get(adapPosition));
                    findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_addMessageFragment, bundle);
                    menu.dismiss();
                } else if (position == 1) {
                    helper.deleteMessage(list.get(adapPosition));
                    list.remove(adapPosition);
                    adaptor.notifyItemRemoved(adapPosition);
                    menu.dismiss();
                } else if (position == 2) {
                    Drawable drawable = imageView.getDrawable();

                    Bitmap bitmap = null;
                    if (drawable instanceof BitmapDrawable) {
                        bitmap = ((BitmapDrawable) drawable).getBitmap();
                    }

                    if (bitmap != null) {
                        // Save the bitmap to a temporary file (optional)
                        String path = saveBitmapToTempFile(bitmap);

                        // Create a content URI for the file
                        Uri contentUri = FileProvider.getUriForFile(requireContext(), requireActivity().getPackageName() + ".fileprovider", new File(path));

                        // Share the Bitmap via Intent
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        // Add text to share
                        String textToShare = list.get(adapPosition).getMessageTitle() + "\n" + list.get(adapPosition).getDetail();
                        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                        startActivity(Intent.createChooser(shareIntent, "Share Post"));
                    }

                    // shareImageAndText(list.get(adapPosition));
                } else if (position == 3) {
                    shareOnFacebook(list.get(adapPosition), imageView);
                }
            }
        });
        //Method to call Fragment
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

    private void shareOnFacebook(MessageDataModel messageDataModel, ImageView imageView) {


        Drawable drawable = imageView.getDrawable();

        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }

        if (bitmap != null) {
            // Save the bitmap to a temporary file (optional)
            String path = saveBitmapToTempFile(bitmap);

            // Create a content URI for the file
            Uri contentUri = FileProvider.getUriForFile(requireContext(), requireActivity().getPackageName() + ".fileprovider", new File(path));


            // Create an intent with ACTION_SEND
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            // Set the type of data you want to share
            shareIntent.setType("image/*");

            // Set the image URI
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

            // Add text to share
            String textToShare = messageDataModel.getMessageTitle() + "\n" + messageDataModel.getDetail();
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);

            // Explicitly set the package to ensure it opens in the Facebook app
            shareIntent.setPackage("com.facebook.katana");

            // Check if the Facebook app is installed
            if (shareIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                // Start the activity
                startActivity(shareIntent);
            } else {
                Toast.makeText(requireContext(), "Facebook is Not installed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareImageAndText(MessageDataModel messageDataModel) {
        // Create an intent with ACTION_SEND
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        // Set the type of data you want to share
        shareIntent.setType("image/*");

        // Set the image URI
        Uri imageUri = Uri.parse("content://" + messageDataModel.getImage_uri());
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        // Add text to share
        String textToShare = messageDataModel.getMessageTitle() + "\n" + messageDataModel.getDetail();
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);

        // Start the activity
        startActivity(Intent.createChooser(shareIntent, "Share using"));
    }

    @Override
    public void onResume() {
        super.onResume();
        btnDelete.setVisibility(View.GONE);
    }
}