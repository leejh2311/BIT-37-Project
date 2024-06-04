package com.example.example.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.example.R;
import com.example.example.databinding.FragmentListBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {

    private FragmentListBinding binding;

    private List_Adapter searchAdapter;

    public class PackageItem {
        private String sectionText;
        private String imageName;

        public PackageItem(String sectionText, String imageName) {
            this.sectionText = sectionText;
            this.imageName = imageName;
        }

        public String getSectionText() {
            return sectionText;
        }

        public String getImageName() {
            return imageName;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        android.widget.Button travel_Record = binding.travelRecord;
        android.widget.Button time_Line = binding.timeLine;

        //여행 기록 버튼 이벤트 생성
        travel_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
                bottomNavigationView.setSelectedItemId(R.id.navigation_search);
            }
        });

        //타임라인 버튼 이벤트 생성
        time_Line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "현재 페이지 입니다", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView outerRecyclerView = rootView.findViewById(R.id.outerRecyclerView);
        LinearLayoutManager outerLayoutManager = new LinearLayoutManager(getContext());
        outerRecyclerView.setLayoutManager(outerLayoutManager);

        searchAdapter = new List_Adapter(new ArrayList<>(), getContext()); // Context도 전달
        outerRecyclerView.setAdapter(searchAdapter);



        getPackageDetailFromServer(new ListFragment.PackageCallback() {
            @Override
            public void onPackageLoaded(List<ListFragment.PackageItem> items) {
                getActivity().runOnUiThread(() -> {
                    searchAdapter.setItems(items);
                    searchAdapter.notifyDataSetChanged();
                });
            }
        });

        return binding.getRoot();
    }

    private void getPackageDetailFromServer(ListFragment.PackageCallback callback) {
        new Thread(() -> {
            try {
                String serverBaseUrl = getString(R.string.server_base_url);
                String fetchPath = serverBaseUrl + "/get/package/detail";
                URL url = new URL(fetchPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject requestData = new JSONObject();
                requestData.put("travelId", "20230727");

                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.write(requestData.toString().getBytes());
                }

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String jsonResponse = readResponse(connection);
                    JSONObject responseJson = new JSONObject(jsonResponse);
                    JSONArray items = responseJson.getJSONArray("item");

                    List<ListFragment.PackageItem> packageItems = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        String sectionText = item.getString("sectionText");
                        String imageName = item.getString("imageName");
                        packageItems.add(new ListFragment.PackageItem(sectionText, imageName));
                    }

                    callback.onPackageLoaded(packageItems);
                } else {
                    // 실패 처리
                }

                connection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                // 실패 처리
            }
        }).start();
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private interface PackageCallback {
        void onPackageLoaded(List<ListFragment.PackageItem> items);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}