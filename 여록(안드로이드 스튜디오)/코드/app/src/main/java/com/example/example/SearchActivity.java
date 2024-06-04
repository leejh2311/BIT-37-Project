package com.example.example;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.example.databinding.FragmentSearchBinding;

public class SearchActivity extends AppCompatActivity{
    private @NonNull FragmentSearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        binding = FragmentSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}



