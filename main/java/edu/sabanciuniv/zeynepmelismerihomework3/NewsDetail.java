package edu.sabanciuniv.zeynepmelismerihomework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsDetail extends AppCompatActivity {

    NewsItem selectedNewsItem;
    int selectedNewsItemId;
    ImageView imgDetail;
    TextView txtDetail;
    TextView txtDetailTitle;
    TextView txtDetailDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        setTitle("News Details");
        ActionBar currentBar = getSupportActionBar();
        currentBar.setHomeButtonEnabled(true);
        currentBar.setDisplayHomeAsUpEnabled(true);
        currentBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_white_18dp);

        imgDetail = findViewById(R.id.imgdetail);
        txtDetail = findViewById(R.id.txtdetail);
        txtDetailDate = findViewById(R.id.txtdetaildate);
        txtDetailTitle = findViewById(R.id.txtdetailtitle);

        selectedNewsItemId= (int)getIntent().getSerializableExtra("selectedNewsItemId");

         NewsDetailTask tsk = new NewsDetailTask();
        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getnewsbyid" ,String.valueOf(selectedNewsItemId));

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if(item.getItemId() == R.id.mn_comment){
            //Toast.makeText(this,"Comment Clicked",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this,CommentsActivity.class);
            i.putExtra("selectedNewsItemId",selectedNewsItemId);
            startActivity(i);
        }


        return true;
    }

    class NewsDetailTask extends AsyncTask<String ,Void,String> {
        @Override
        protected String doInBackground(String... strings) {

            String id = strings[1];
            String urlStr = strings[0] + "/" + id;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url1 = new URL(urlStr);

                HttpURLConnection conn = (HttpURLConnection)url1 .openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = "";
                while ( (line= reader.readLine()) != null){
                    stringBuilder.append(line);

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("DEV",s);
            try {
                JSONObject obj = new JSONObject(s);

                if(obj.getInt("serviceMessageCode") == 1){

                    JSONArray arr = obj.getJSONArray("items");

                    for(int i = 0; i < arr.length() ; i++){

                        JSONObject current = (JSONObject) arr.get(i);

                        long date = current.getLong("date");
                        Date objDate = new Date(date);

                        NewsItem item = new NewsItem(current.getInt("id"),
                                current.getString("title"),
                                current.getString("text"),
                                current.getString("image"),
                                objDate
                        );


                        if(item.getBitmap() == null){
                            new ImageDownloadTask(imgDetail).execute(item);
                        }else{
                            imgDetail.setImageBitmap(item.getBitmap());
                        }

                        txtDetail.setText(item.getText());
                        txtDetailTitle.setText(item.getTitle());
                        txtDetailDate.setText(new SimpleDateFormat("dd/MM/yyy").format(item.getNewsDate()));
                    }
                }
            } catch (JSONException e) {
                Log.e("DEV",e.getMessage());
            }
        }
    }
}
