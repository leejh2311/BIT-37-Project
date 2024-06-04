package com.example.example.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.example.R;
import com.example.example.databinding.FragmentSearchBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private static final String TAG = "SearchFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        android.widget.Button travel_Record = binding.travelRecord;
        android.widget.Button time_Line = binding.timeLine;


        //여행 기록 버튼 이벤트 생성
        travel_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "현재 페이지 입니다", Toast.LENGTH_SHORT).show();
            }
        });

        //타임라인 버튼 이벤트 생성
        time_Line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
                bottomNavigationView.setSelectedItemId(R.id.navigation_list);
            }
        });

        // 서버에서 이미지 가져와서 카드뷰에 표시
        fetchImageUriFromServer("20230727");

        return rootView;
    }

    // 이미지 URI를 서버에서 가져오는 메서드
    private void fetchImageUriFromServer(String travelId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> imageUrls = new ArrayList<>();
                try {
                    String serverBaseUrl = getString(R.string.server_base_url);

                    // 경로 추가
                    String getPath = serverBaseUrl + "/get/image";

                    URL url = new URL(getPath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // 설정: POST 요청
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // 요청 데이터 작성
                    JSONObject postData = new JSONObject();
                    postData.put("travelId", travelId);
                    OutputStream os = connection.getOutputStream();
                    os.write(postData.toString().getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    // 응답 받기
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String response = readResponse(connection);
                        JSONArray itemArray = new JSONObject(response).getJSONArray("Item");
                        for (int i = 0; i < itemArray.length(); i++) {
                            JSONObject imageInfo = itemArray.getJSONObject(i);
                            String imageName = imageInfo.getString("imageName");
                            String imageUrl = serverBaseUrl + "/image/" + imageName; // 올바른 URL 조합
                            imageUrls.add(imageUrl);
                        }
                    }

                    // 연결 닫기
                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                // UI 업데이트를 메인 스레드에서 처리하기 위해 Handler 사용
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        updateImagesOnMainThread(imageUrls);
                    }
                });
            }
        }).start();
    }

    private void updateImagesOnMainThread(List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            ImageView imageView = requireView().findViewById(getResources().getIdentifier("imageView" + (i + 1), "id", requireContext().getPackageName()));
            Glide.with(requireContext())
                    .load(imageUrls.get(i))
                    .placeholder(R.drawable.image1)
                    .error(R.drawable.lens)
                    .into(imageView);
        }

        // 이미지가 없는 ImageView에 대해 기본 이미지 설정
        for (int i = imageUrls.size(); i < 27; i++) {
            ImageView imageView = requireView().findViewById(getResources().getIdentifier("imageView" + (i + 1), "id", requireContext().getPackageName()));
            imageView.setImageResource(R.drawable.no_image);
        }
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
