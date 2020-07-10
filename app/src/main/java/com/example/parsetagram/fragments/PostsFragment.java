package com.example.parsetagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.parsetagram.EndlessRecyclerViewScrollListener;
import com.example.parsetagram.Post;
import com.example.parsetagram.PostsAdapter;
import com.example.parsetagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.

 */
public class PostsFragment extends Fragment {

    public static final String TAG = "PostsFragment";
    protected RecyclerView rvPosts;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected PostsAdapter postsAdapter;
    protected List<Post> allPosts;
    protected Button btnLogout;
    protected Button btnEditProfilePic;
    // Store a member variable for the listener
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected Date oldestPost;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfilePic = view.findViewById(R.id.btnEditProfilePic);
        //hiding profile buttons
        btnLogout.setVisibility(View.GONE);
        btnEditProfilePic.setVisibility(View.GONE);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"User getting new data");
                queryPosts();
            }
        });

        allPosts = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(),allPosts);

        rvPosts.setAdapter(postsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(linearLayoutManager);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.i(TAG,"load more posts");
                queryMorePosts();
            }
        };
        rvPosts.addOnScrollListener(scrollListener);

        queryPosts();
    }

    protected void queryMorePosts() {
        //query posts after oldest post
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(5);
        query.whereLessThan(Post.KEY_CREATED_AT,oldestPost);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null || objects.size() == 0){
                    Log.e(TAG,"Issue with loading more posts or no more posts",e);
                    return;
                }
                allPosts.addAll(objects);
                //updating new oldest post
                oldestPost = objects.get(objects.size()-1).getCreatedAt();
                //add to profile view next
                postsAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(2);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        postsAdapter.clear();
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null || objects.size() == 0){
                    Log.e(TAG,"Issue w getting posts",e);
                    return;
                }
//                for (Post post: objects){
//                    Log.i(TAG, "Post: " + post.getDescription() + "\nUsername: " + post.getUser().getUsername());
//                }
                allPosts.addAll(objects);
                //updating last query item
                oldestPost = objects.get(objects.size()-1).getCreatedAt();
                swipeRefreshLayout.setRefreshing(false);
                postsAdapter.notifyDataSetChanged();
            }
        });
    }
}