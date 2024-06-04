package com.bit37.team6.Upload.Article;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class articlePythonThread extends Thread {
    private long articleId;

    @Override
    public void run() {
        try {
            String articleIdString = Long.toString(articleId);
            System.out.println(articleIdString);
            String pythonPath = "C:/Users/lullu/Desktop/dataManageModule/DataManage.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonPath, articleIdString);
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "cp949"));

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }
}
