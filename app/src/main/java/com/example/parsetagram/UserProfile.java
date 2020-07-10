package com.example.parsetagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {

    public static final String TAG = "UserProfile";
    RecyclerView rvUserPosts;
    ParseUser parseUser;
    ImageView userProfilePic;
    TextView username;
    List<Post> userPosts;
    ProfileAdapter profileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        rvUserPosts = findViewById(R.id.profile_rvPosts);
        userProfilePic = findViewById(R.id.profile_user_pic);
        username = findViewById(R.id.profile_username);

        parseUser = (ParseUser) Parcels.unwrap(getIntent().getParcelableExtra("user"));

        ParseFile pfp = parseUser.getParseFile("profilePic");

        Glide.with(this)
                .load(pfp.getUrl())
                .circleCrop()
                .into(userProfilePic);

        //TODO: initialize user posts
        userPosts = new ArrayList<>();

        profileAdapter = new ProfileAdapter(userPosts,this);
        rvUserPosts.setAdapter(profileAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        rvUserPosts.setLayoutManager(gridLayoutManager);

        username.setText(parseUser.getUsername());

        queryUserProfilePosts();
    }

    private void queryUserProfilePosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_USER, parseUser);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        //clearing before background call prevents recycle view and scroll refresh bug
        profileAdapter.clear();

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null || (objects.size() == 0)){
                    Log.e(TAG,"Issue w getting posts",e);
                    return;
                }

                userPosts.addAll(objects);
                profileAdapter.notifyDataSetChanged();
            }
        });
    }

}