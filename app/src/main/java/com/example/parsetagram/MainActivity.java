package com.example.parsetagram;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    ImageView ivPostImage;
    Button btnSubmit;
    EditText etDesc;
    Button btnCaptureImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDesc = findViewById(R.id.etDesc);
        ivPostImage = findViewById(R.id.ivPostImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCaptureImage = findViewById(R.id.btnCaptureImage);

//        queryPosts();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = etDesc.getText().toString();
                if (desc.isEmpty()){
                    Toast.makeText(MainActivity.this,"Description can't be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser =  ParseUser.getCurrentUser();
                savePost(desc,currentUser);
            }
        });
    }

    private void savePost(String desc, ParseUser currentUser) {
        Post post = new Post();
        post.setDescription(desc);
//        post.setImage();
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG,"error while saving",e);
                    Toast.makeText(MainActivity.this,"error while saving",Toast.LENGTH_LONG);
                }
                Log.i(TAG,"Post save was successful!");
                etDesc.setText("");
            }
        });
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG,"Issue w getting posts",e);
                    return;
                }
                for (Post post: objects){
                    Log.i(TAG, "Post: " + post.getDescription() + "\nUsername: " + post.getUser().getUsername());
                }
            }
        });
    }
}