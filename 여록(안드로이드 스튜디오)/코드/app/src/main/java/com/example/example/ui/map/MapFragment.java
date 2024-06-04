package com.example.example.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.example.R;
import com.example.example.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private static List<Marker> markerList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;


    private FragmentMapBinding binding;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);


        // 서버에서 받아온 좌표값을 기반으로 마커를 추가합니다.
        addMarkersFromServer();

        return rootView;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // 위치 권한 체크
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없으면 권한 요청
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            // 권한이 있으면 현재 위치 가져오기 시작
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            float zoomLevel;
                            //대한민국 중심으로 지도의 초기 위치 설정
                            LatLng koreaCenter = new LatLng(36.5, 127.7); // 대한민국 중심 좌표
                            zoomLevel = 7;
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(koreaCenter, zoomLevel));

                        }
                    });
        }
    }

    // 이미지 URI를 서버에서 가져오는 메서드
    private void addMarkersFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<LatLng> coordinates = new ArrayList<>(); // 좌표 정보를 저장하는 리스트

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
                    postData.put("travelId", 20230727);
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
                            double gps_la = imageInfo.getDouble("gps_la");
                            double gps_lo = imageInfo.getDouble("gps_lo");
                            coordinates.add(new LatLng(gps_la, gps_lo)); // 좌표 정보를 리스트에 추가
                        }
                    }

                    // 연결 닫기
                    connection.disconnect();

                    // 가져온 좌표 정보를 기반으로 마커 추가
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (LatLng coordinate : coordinates) {
                                addCustomMarker(coordinate); // 좌표로 마커 추가
                            }
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // addCustomMarker 메서드를 수정하여 좌표로 마커를 추가합니다.
    private void addCustomMarker(LatLng latLng) {
        // 마커 생성에 사용할 아이콘 설정 (기존 custom_marker1를 사용합니다.)
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.custom_marker1);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(icon);

        Marker marker = googleMap.addMarker(markerOptions);
        markerList.add(marker);

        // 새 마커를 추가한 후 마커 정보를 저장합니다.
        saveMarkers();
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
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }



    // 마커 정보를 저장하는 메소드
    private void saveMarkers() {
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("marker_count", markerList.size());
        for (int i = 0; i < markerList.size(); i++) {
            Marker marker = markerList.get(i);
            String key = "marker_" + i;
            editor.putFloat(key + "_lat", (float) marker.getPosition().latitude);
            editor.putFloat(key + "_lng", (float) marker.getPosition().longitude);

            // marker.getTag()를 가져온 후 ArrayList<Uri> 타입으로 형변환하여 저장
            Object tagObject = marker.getTag();
            if (tagObject instanceof ArrayList<?>) {
                ArrayList<Uri> imageUris = (ArrayList<Uri>) tagObject;
                Set<String> uriSet = new HashSet<>();
                for (Uri uri : imageUris) {
                    uriSet.add(uri.toString());
                }
                editor.putStringSet(key + "_imageUris", uriSet);
            }
        }
        editor.apply();
    }

}