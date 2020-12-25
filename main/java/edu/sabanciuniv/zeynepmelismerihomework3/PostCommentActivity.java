package edu.sabanciuniv.zeynepmelismerihomework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostCommentActivity extends AppCompatActivity {

    ProgressDialog prgDialog;
    EditText postCommenterName;
    EditText postComment;
    int selectedNewsItemId;
    String commenterName;
    String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);
        setTitle("Post Comment");
        ActionBar currentBar = getSupportActionBar();
        currentBar.setHomeButtonEnabled(true);
        currentBar.setDisplayHomeAsUpEnabled(true);
        currentBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_white_18dp);

        postCommenterName = findViewById(R.id.txtpostcommentername);
        postComment = findViewById(R.id.txtpostcomment);
        selectedNewsItemId=  (int)getIntent().getSerializableExtra("selectedNewsItemId");
        Log.i("DEV", String.valueOf(selectedNewsItemId));
    }

    public void toResultClicked(View v){

        PostCommentTask tsk = new PostCommentTask();
        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/savecomment");
        commenterName = postCommenterName.getText().toString();
        comment = postComment.getText().toString();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    class PostCommentTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(PostCommentActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder strBuilder = new StringBuilder();
            String urlStr = strings[0];


            JSONObject obj = new JSONObject();
            try {
                obj.put("name",commenterName);
                obj.put("text",comment);
                obj.put("news_id",selectedNewsItemId);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.connect();

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(obj.toString());


                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line ="";

                    while((line = reader.readLine())!=null){
                        strBuilder.append(line);
                    }

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return strBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("DEV",s);
            JSONObject obj = null;
            try {
                obj = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if(obj.getInt("serviceMessageCode") == 1){
                    prgDialog.dismiss();
                    Intent i = new Intent(PostCommentActivity.this, CommentsActivity.class);

                    startActivity(i);
                    finish();
                }else{
                    Log.e("DEV",s);
                    String alertMessage = obj.getString("serviceMessageText");
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PostCommentActivity.this);
                    androidx.appcompat.app.AlertDialog dialog = builder.setTitle("Error").setMessage(alertMessage).create();
                    dialog.show();
                    prgDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
