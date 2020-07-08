package com.example.parsetagram;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;


public class PostDetails extends AppCompatActivity {

    public static final String TAG = "PostDetails";
    TextView username;
    ImageView picture;
    TextView description;
    TextView timestamp;
    ImageView profilePic;

    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        username = findViewById(R.id.details_username);
        picture = findViewById(R.id.details_pic);
        description = findViewById(R.id.details_desc);
        timestamp = findViewById(R.id.details_timestamp);
        profilePic = findViewById(R.id.details_profilePic);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));
        Log.i(TAG,"Showing details for " + description);

        username.setText(post.getUser().getUsername());
        description.setText(post.getDescription());
        Date date = post.getCreatedAt();
        timestamp.setText((String) DateUtils.getRelativeTimeSpanString(date.getTime()));

        ParseFile image = post.getImage();
        if(image != null) {
            picture.setVisibility(View.VISIBLE);
            Glide.with(this).load(post.getImage().getUrl()).into(picture);
        }else{
            picture.setVisibility(View.GONE);
        }

        Glide.with(this)
                .load(getProfileUrl(post.getUser().getObjectId()))
                .circleCrop()
                .into(profilePic);
    }

    private static String getProfileUrl(final String userId) {
        String hex = "";
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] hash = digest.digest(userId.getBytes());
            final BigInteger bigInt = new BigInteger(hash);
            hex = bigInt.abs().toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "https://www.gravatar.com/avatar/" + hex + "?d=identicon";
    }
}