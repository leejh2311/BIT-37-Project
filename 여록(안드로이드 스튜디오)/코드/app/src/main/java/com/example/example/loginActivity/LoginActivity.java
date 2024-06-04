package com.example.example.loginActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.example.MainActivity;
import com.example.example.R;
import com.microsoft.sqlserver.jdbc.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //컨트롤 바인딩
        usernameEditText = findViewById(R.id.edit_id);
        passwordEditText = findViewById(R.id.edit_pw);
        loginButton = findViewById(R.id.btn_login);
        signupButton = findViewById(R.id.btn_signup);

        //로그인 버튼 클릭 시
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // 정상 코드
                if (StringUtils.isEmpty(username)) {
                    Toast.makeText(LoginActivity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (StringUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }


                /*
                // 테스트 코드
                if (StringUtils.isEmpty(username) | StringUtils.isEmpty(password)) {
                    username = "testID";
                    password = "testPW";
                }
                */

                // JSON 데이터 생성
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("userId", username);
                    jsonData.put("userPw", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // AsyncTask 실행 (네트워크 통신)
                new LoginTask().execute(jsonData.toString());
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LoginActivity", "Activity resumed"); // 추가된 로그
    }

    private class LoginTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject(params[0]);

                // 서버 주소 가져오기
                Resources resources = getResources();
                String serverBaseUrl = resources.getString(R.string.server_base_url);

                // 경로 추가
                String uploadPath = serverBaseUrl + "/login";

                URL url = new URL(uploadPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // POST 요청 설정
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // POST 요청
                OutputStream os = connection.getOutputStream();
                os.write(params[0].getBytes());
                os.flush();

                // 반환 값 확인
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 성공 시 응답 데이터를 받아옴
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject resultJson = new JSONObject(response.toString());
                    Log.d("Login Activity", resultJson.toString());

                    return resultJson;
                }

                connection.disconnect();
                return null;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.d("Login Activity", e.toString());
                return null; // 에러 발생 시 실패로 처리
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonResponse) {
            super.onPostExecute(jsonResponse);

            if (jsonResponse != null && jsonResponse.optInt("code", -1) == 1) {
                // 로그인 성공 처리
                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    MainActivity.userId = data.getString("userId");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // 로그인 성공 시 다음 화면으로 이동 (예시)
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            } else {
                // 로그인 실패 처리
                Toast.makeText(LoginActivity.this, "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}