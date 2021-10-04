package com.example.memeshare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private String memeUrl;

    private void loadMeme() {

        ProgressBar lodder = findViewById(R.id.progressBar);
        lodder.setVisibility(View.VISIBLE);

        String url = "https://meme-api.herokuapp.com/gimme";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            memeUrl = response.getString("url");
                            ImageView meme = findViewById(R.id.memeImage);
                            Glide.with(meme.getContext()).load(memeUrl).listener(
                                    new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            lodder.setVisibility(View.GONE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            lodder.setVisibility(View.GONE);
                                            return false;
                                        }
                                    }
                            ).into(meme);
                        } catch (JSONException e) {
                            Log.e("error", "No url field in JSON object");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "Error in network calls.");
                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMeme();
    }

    public void shareMeme(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Check out this cool meme!! \n" + memeUrl);
        sendIntent.setType("text/plain");
        Intent chooser = Intent.createChooser(sendIntent, "Send this meme using...");

        try {
            startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void nextMeme(View view) {
        loadMeme();
    }
}