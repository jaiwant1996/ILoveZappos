package edu.msu.bhushanj.ilovezappos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Boolean isRes=false;
    private EditText searchQuery;
    private TextView itemBrand;
    private String itemBrandString;

    private TextView itemPrice;
    private String itemPriceString;

    private TextView itemName;
    private String itemNameString;

    private String apiKey;
    private String jsonText;

    private ImageView iv;
    private Bitmap bitmap;

    private String searchQ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageDrawable(null);
        itemBrand = (TextView) findViewById(R.id.itemBrand);
        itemBrand.setText(null);

        itemPrice = (TextView) findViewById(R.id.itemPrice);
        itemPrice.setText(null);

        itemName = (TextView) findViewById(R.id.itemName);
        itemName.setText(null);

        apiKey = "b743e26728e16b81da139182bb2094357c31d331";

        Button btn = (Button)findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchQuery = (EditText) findViewById(R.id.editText);
                searchQ = searchQuery.getText().toString();

                new JsonParseUrl().execute("","","");

            }
        });
    }



    private class JsonParseUrl extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!isRes){
                Toast.makeText(MainActivity.this, "No results found!", Toast.LENGTH_SHORT).show();
            }
            else{
                iv.setImageBitmap(bitmap);
                itemPrice.setText(itemPriceString);
                itemBrand.setText(itemBrandString);
                itemName.setText(itemNameString);
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            searchQuery = (EditText) findViewById(R.id.editText);

            String searchUrl = "https://api.zappos.com/Search?term=" + searchQ + "&key=" + apiKey;


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(searchUrl);
                connection = (HttpURLConnection) url.openConnection();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                jsonText = buffer.toString();
                Log.d("myTag", jsonText);

                JSONObject parentObj = new JSONObject(jsonText);

                JSONArray parentArr = parentObj.getJSONArray("results");
                if(parentArr.length() == 0){
                    isRes=false;
                }
                else{
                    isRes=true;
                }

                JSONObject firstObj = parentArr.getJSONObject(0);

                String imgageUrl = firstObj.getString("thumbnailImageUrl");
                itemNameString = firstObj.getString("productName");
                itemPriceString = firstObj.getString("price");
                itemBrandString = firstObj.getString("brandName");

                Log.d("Url: ", imgageUrl);

                try {
                    URL imgUrl = new URL(imgageUrl);
                    Bitmap image = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
                    bitmap = image;
                } catch(IOException e) {
                    System.out.println(e);
                }



            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }


    }


}
