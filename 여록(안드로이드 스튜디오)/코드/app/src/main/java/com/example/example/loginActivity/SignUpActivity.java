package com.example.example.loginActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.example.MainActivity;
import com.example.example.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextId;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;
    private TextView passwordMismatchText;
    private Spinner spinnerYear;
    private Spinner spinnerMonth;
    private Spinner spinnerDay;
    private Spinner spinnerEmailSuffix;
    private Button signUpButton;
    private CheckBox checkBoxMale;
    private CheckBox checkBoxFemale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextName = findViewById(R.id.editTextName);
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPassword2);
        passwordMismatchText = findViewById(R.id.passwordMismatch);
        spinnerYear = findViewById(R.id.spinner_year);
        spinnerMonth = findViewById(R.id.spinner_month);
        spinnerDay = findViewById(R.id.spinner_day);
        spinnerEmailSuffix = findViewById(R.id.spinner_email_suffix);
        signUpButton = findViewById(R.id.signupButton);
        checkBoxMale = findViewById(R.id.checkBoxMale);
        checkBoxFemale = findViewById(R.id.checkBoxFemale);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = editTextId.getText().toString();
                String userPw = editTextPassword.getText().toString();
                String confirmPassword = editTextPasswordConfirm.getText().toString();

                if (userPw.equals(confirmPassword)) {
                    passwordMismatchText.setVisibility(View.GONE);
                    JSONObject json = new JSONObject();
                    try {
                        json.put("userId", userId);
                        json.put("userPw", userPw);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SignUpTask().execute(json.toString());
                } else {
                    passwordMismatchText.setVisibility(View.VISIBLE);
                    passwordMismatchText.setText("비밀번호가 일치하지 않습니다.");
                }
            }
        });

        // Populate the spinner_year
        String[] years = new String[2008 - 1970];
        for (int i = 1970; i <= 2007; i++) {
            years[i - 1970] = String.valueOf(i);
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Populate the spinner_month
        String[] months = new String[12];
        for (int i = 1; i <= 12; i++) {
            months[i - 1] = String.valueOf(i);
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Populate the spinner_day
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.valueOf(i);
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Populate the spinner_email_suffix
        String[] emailSuffixes = {"@naver.com", "@daum.net", "@gmail.com"};
        ArrayAdapter<String> emailSuffixAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emailSuffixes);
        emailSuffixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEmailSuffix.setAdapter(emailSuffixAdapter);

        // Set the OnCheckedChangeListener for checkBoxMale
        checkBoxMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxFemale.setChecked(false);
                checkBoxFemale.setEnabled(false);
            } else {
                checkBoxFemale.setEnabled(true);
            }
        });

        // Set the OnCheckedChangeListener for checkBoxFemale
        checkBoxFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxMale.setChecked(false);
                checkBoxMale.setEnabled(false);
            } else {
                checkBoxMale.setEnabled(true);
            }
        });
    }


    private class SignUpTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                // 서버 주소 가져오기
                String serverBaseUrl = getString(R.string.server_base_url);

                // 경로 추가
                String uploadPath = serverBaseUrl + "/register";

                URL url = new URL(uploadPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();


                // POST 요청 설정
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // JSON 데이터 전송
                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    outputStream.write(params[0].getBytes());
                }

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return new JSONObject(response.toString());
                }

                connection.disconnect();
                return null;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonResponse) {
            super.onPostExecute(jsonResponse);

            if (jsonResponse != null) {
                Log.d("SignUpActivity", "JSON Response: " + jsonResponse.toString());

                String message = jsonResponse.optString("msg", "");

                if ("OK".equals(message)) {
                    Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d("SignUpActivity", "Activity finished");
                } else if ("ID already exists".equals(message)) {
                    Toast.makeText(SignUpActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("SignUpActivity", "JSON Response is null or invalid.");
                Toast.makeText(SignUpActivity.this, "서버 응답 없음 또는 오류 발생", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
