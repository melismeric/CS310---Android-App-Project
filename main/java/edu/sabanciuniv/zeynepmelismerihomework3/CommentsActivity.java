package edu.sabanciuniv.zeynepmelismerihomework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    RecyclerView commentsRecView;
    int selectedNewsItemId;
    CommentsAdapter adp;
    List<CommentItem> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        setTitle("Comments");
        data = new ArrayList<>();
        ActionBar currentBar = getSupportActionBar();
        currentBar.setHomeButtonEnabled(true);
        currentBar.setDisplayHomeAsUpEnabled(true);
        currentBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_white_18dp);

        commentsRecView = findViewById(R.id.commentsrec);
        selectedNewsItemId= (int) getIntent().getSerializableExtra("selectedNewsItemId");

        adp = new CommentsAdapter(data,this);

        commentsRecView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecView.setAdapter(adp);

        CommentsTask tsk = new CommentsTask();
        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getcommentsbynewsid",String.valueOf(selectedNewsItemId));

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if(item.getItemId() == R.id.mn_postcomment){
            Toast.makeText(this,"post Comment Clicked",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this,PostCommentActivity.class);
            i.putExtra("selectedNewsItemId",selectedNewsItemId);
            startActivity(i);
        }
        return true;
    }

    class CommentsTask extends AsyncTask<String,Void,String > {

        @Override
        protected String doInBackground(String... strings) {
            String id = strings[1];
            String urlStr = strings[0] + "/" + id;
            StringBuilder buffer = new StringBuilder();

            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = "";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            data.clear();
            Log.i("DEV",s);
            try {
                JSONObject obj = new JSONObject(s);

                if(obj.getInt("serviceMessageCode") == 1){

                    JSONArray arr = obj.getJSONArray("items");

                    for(int i = 0; i < arr.length() ; i++){
                        JSONObject current = (JSONObject) arr.get(i);
                        CommentItem item = new CommentItem(current.getInt("id"),
                                current.getString("text"),
                                current.getString("name")
                        );
                        data.add(item);
                    }
                }
                adp.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("DEV",e.getMessage());
            }
        }
    }

    @Override
    protected void onRestart() {
        this.recreate();
        super.onRestart();
    }
}
