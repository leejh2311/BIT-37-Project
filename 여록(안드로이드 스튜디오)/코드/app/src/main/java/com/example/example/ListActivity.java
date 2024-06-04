package com.example.example;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.example.databinding.FragmentHomeBinding;
import com.example.example.databinding.FragmentListBinding;
public class ListActivity extends AppCompatActivity {

    private @NonNull FragmentListBinding binding;
    private @NonNull FragmentHomeBinding binding2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        binding = FragmentListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding2 = FragmentHomeBinding.inflate(getLayoutInflater());
        setContentView(binding2.getRoot());

    }
}
