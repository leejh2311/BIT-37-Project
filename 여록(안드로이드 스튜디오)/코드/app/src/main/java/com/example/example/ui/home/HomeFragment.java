package com.example.example.ui.home;

import static android.content.ContentValues.TAG;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.example.MainActivity;
import com.example.example.R;
import com.example.example.databinding.FragmentHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ArrayList<Uri> uriList = new ArrayList<>();
    private RecyclerView recyclerView;

    private Upload_Adapter adapter;
    public Long articleId;
    int REQUEST_CODE_PICK_IMAGE = 200;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        Button selectImageButton = rootView.findViewById(R.id.select_image_button);
        EditText articleEditText = rootView.findViewById(R.id.article_editText);
        Button uploadButton = rootView.findViewById(R.id.upload_button);

        initializeViews();
        setupRecyclerView();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String article = articleEditText.getText().toString();
                new UploadTask().execute(article);
            }
        });

        // 키보드가 열릴 때 EditText 위로 스크롤되도록 설정
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return rootView;
    }

    private void initializeViews() {
        recyclerView = binding.recyclerView1;
    }

    private void setupRecyclerView() {
        adapter = new Upload_Adapter(uriList, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == getActivity().RESULT_OK) {
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                uriList.clear();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    uriList.add(0, imageUri);
                }
                adapter.notifyDataSetChanged();
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                uriList.clear();
                uriList.add(imageUri);
                adapter.notifyDataSetChanged();
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(binding.getRoot().getContext(), "Cancelled.", Toast.LENGTH_SHORT).show();
        }
    }

    private class UploadTask extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("travelId", 20230727);
                jsonObject.put("uploaderIdString", MainActivity.userId);
                jsonObject.put("text", params[0]);

                String serverBaseUrl = getString(R.string.server_base_url);
                String uploadPath = serverBaseUrl + "/upload/article";

                URL url = new URL(uploadPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                os.write(jsonObject.toString().getBytes());
                os.flush();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject resultJson = new JSONObject(response.toString());
                    if (resultJson.has("item")) {
                        JSONObject item = resultJson.getJSONObject("item");
                        if (item.has("articleId")) {
                            long articleId = item.getLong("articleId");
                            Log.d(TAG, "Received articleId: " + articleId); // 로그로 articleId 출력
                            connection.disconnect();
                            return articleId;
                        }
                    }
                }

                connection.disconnect();
                return null;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Long resultArticleId) {
            super.onPostExecute(resultArticleId);
            if (resultArticleId != null) {
                // 전역 변수 articleId에 할당
                articleId = resultArticleId;

                ArrayList<Uri> selectedImageUris = adapter.getSelectedImageUris();
                uploadImagesToServer(selectedImageUris, articleId, new ImageUploadCallback() {
                    @Override
                    public void onSuccess() {
                        // 이미지 업로드 성공한 후에 articleId 서버로 보내기
                        sendArticleIdToServer(articleId, new ArticleIdSendCallback() {
                            @Override
                            public void onSuccess() {
                                // UI 스레드에서 토스트 메시지 출력
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "성공적으로 업로드되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onFailure() {
                                // Toast 등으로 실패 메시지 출력
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        // 이미지 업로드 실패 시 메시지 출력
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), "Article upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    private void uploadImagesToServer(ArrayList<Uri> imageUris, Long articleId, ImageUploadCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 서버 주소 가져오기
                    String serverBaseUrl = getString(R.string.server_base_url);

                    // 경로 추가
                    String uploadPath = serverBaseUrl + "/upload/image";

                    for (Uri imageUri : imageUris) {
                        float[] gpsInfo = getGPSFromExif(imageUri);
                        //위도
                        double latitude = gpsInfo[0];
                        //경도
                        double longitude = gpsInfo[1];

                        URL url = new URL(uploadPath);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        // 설정: POST 요청
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setDoOutput(true);

                        JSONObject json = new JSONObject();
                        String base64Image = getBase64FromUri(imageUri);
                        String imageName = getOriginalImageName(imageUri);

                        json.put("articleId", articleId);  // 전달받은 articleId 사용
                        json.put("uploaderIdString",  MainActivity.userId);
                        json.put("imageName", imageName);
                        json.put("imageBase64", base64Image);
                        json.put("gps_la", latitude); // 위도 추가
                        json.put("gps_lo", longitude); // 경도 추가

                        // JSON 데이터 전송
                        try (OutputStream outputStream = connection.getOutputStream()) {
                            byte[] input = json.toString().getBytes("utf-8");
                            outputStream.write(input, 0, input.length);
                        }

                        // 응답 받기
                        int responseCode = connection.getResponseCode();
                        Log.d(TAG, "Server Response Code: " + responseCode);

                        if (responseCode != HttpURLConnection.HTTP_OK) {
                            callback.onFailure();
                            return; // 이미지 하나라도 업로드 실패 시 중단
                        }

                        // 연결 닫기
                        connection.disconnect();
                    }

                    callback.onSuccess(); // 모든 이미지 업로드 성공
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            }
        }).start();
    }



    private float[] getGPSFromExif(Uri imageUri) {
        float[] gpsInfo = new float[2]; // 0: 위도, 1: 경도

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                ExifInterface exif = new ExifInterface(inputStream);
                float[] latLong = new float[2];
                boolean hasLatLong = exif.getLatLong(latLong);

                if (hasLatLong) {
                    gpsInfo[0] = latLong[0]; // 위도
                    gpsInfo[1] = latLong[1]; // 경도
                }
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gpsInfo;
    }


    private String getOriginalImageName(Uri uri) {
        String originalName = null;
        Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            originalName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            cursor.close();
        }
        return originalName;
    }

    private String getBase64FromUri(Uri uri) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = requireContext().getContentResolver().openInputStream(uri);
            outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP); // 수정된 부분
            return base64Image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private interface ImageUploadCallback {
        void onSuccess();
        void onFailure();
    }


    //articleId 다시 send하는거
    private void sendArticleIdToServer(long articleId, ArticleIdSendCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String serverBaseUrl = getString(R.string.server_base_url);
                    String articleUploadPath = serverBaseUrl + "/upload/article";

                    URL url = new URL(articleUploadPath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    JSONObject json = new JSONObject();
                    json.put("articleId", articleId);

                    try (OutputStream outputStream = connection.getOutputStream()) {
                        byte[] input = json.toString().getBytes("utf-8");
                        outputStream.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    Log.d(TAG, "Server Response Code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure();
                    }

                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            }
        }).start();
    }

    private interface ArticleIdSendCallback {
        void onSuccess();
        void onFailure();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
