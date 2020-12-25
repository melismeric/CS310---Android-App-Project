package edu.sabanciuniv.zeynepmelismerihomework3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

public class MainActivity extends AppCompatActivity {

    ProgressDialog prgDialog;
    RecyclerView newsRecView;
    List<NewsItem> data;
    List<CategoryItem> dataCat;
    NewsAdapter adp;
    ArrayAdapter<CategoryItem> adpCat;
    Spinner categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = new ArrayList<>();
        dataCat = new ArrayList<>();
        categories = findViewById(R.id.spinner);
        setTitle("News");
        newsRecView = findViewById(R.id.newsrec);
        adp = new NewsAdapter(data, this, new NewsAdapter.NewsItemClickListener() {
            @Override
            public void newItemClicked(NewsItem selectedNewsItem) {
                //Toast.makeText(MainActivity.this, selectedNewsItem.getTitle(), Toast.LENGTH_SHORT).show();
                int id = selectedNewsItem.getId();
                String s = String.valueOf(id);
                //Log.i("DEV",s);
                Intent i = new Intent(MainActivity.this,NewsDetail.class);
                i.putExtra("selectedNewsItemId",selectedNewsItem.getId());
                startActivity(i);
            }

        });

       adpCat = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,dataCat);

       categories.setAdapter(adpCat);

        categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory= categories.getSelectedItem().toString();
                Log.i("DEV",selectedCategory);
                newsRecView.setAdapter(adp);
                int cat_id = ((CategoryItem)categories.getSelectedItem()).getId();

                if(cat_id == 100)
                {
                    NewsTask tsk1 = new NewsTask();
                    tsk1.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");
                }
                else
                {
                    NewsTask tsk1 = new NewsTask();
                    tsk1.execute("http://94.138.207.51:8080/NewsApp/service/news/getbycategoryid/" + cat_id);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newsRecView.setAdapter(adp);
            }
        });


        newsRecView.setLayoutManager(new LinearLayoutManager(this));


        NewsTask tsk = new NewsTask();
        tsk.execute("http://94.138.207.51:8080/NewsApp/service/news/getall");

      SpinnerTask tskspinner = new SpinnerTask();
      tskspinner.execute("http://94.138.207.51:8080/NewsApp/service/news/getallnewscategories");

    }

    class NewsTask extends AsyncTask<String,Void,String >{
        @Override
        protected void onPreExecute() {
            prgDialog = new ProgressDialog(MainActivity.this);
            prgDialog.setTitle("Loading");
            prgDialog.setMessage("Please wait...");
            prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prgDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String urlStr = strings[0];
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

                        long date = current.getLong("date");
                        Date objDate = new Date(date);

                        NewsItem item = new NewsItem(current.getInt("id"),
                          current.getString("title"),
                          current.getString("text"),
                          current.getString("image"),
                                objDate
                                );
                          data.add(item);
                    }
                }
                adp.notifyDataSetChanged();
                prgDialog.dismiss();
            } catch (JSONException e) {
                Log.e("DEV",e.getMessage());
            }
        }
    }



    class SpinnerTask extends AsyncTask<String,Void,String >{

        @Override
        protected String doInBackground(String... strings) {
            String urlStr = strings[0];
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
            dataCat.clear();
            Log.i("DEV",s);
            try {
                JSONObject obj = new JSONObject(s);
                CategoryItem all = new CategoryItem(100,"All");
                dataCat.add(all);

                if(obj.getInt("serviceMessageCode") == 1){

                    JSONArray arr = obj.getJSONArray("items");

                    for(int i = 0; i < arr.length() ; i++){

                        JSONObject current = (JSONObject) arr.get(i);
                        //Log.i("DEV",current.getString("name"));
                        String namecat = current.getString("name");

                        CategoryItem catitem = new CategoryItem(current.getInt("id"),
                                current.getString("name")
                        );
                        dataCat.add(catitem);


                    }

                }else{

                }

                adpCat.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("DEV",e.getMessage());
            }
        }
        }



}


