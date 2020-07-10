package com.example.parsetagram;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import static android.view.View.GONE;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView post_user;
        private ImageView post_image;
        private TextView post_desc;
        private ImageView user_pic;
        private TextView post_desc_user;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            post_user = itemView.findViewById(R.id.post_user);
            post_image = itemView.findViewById(R.id.post_pic);
            post_desc = itemView.findViewById(R.id.post_desc);
            user_pic = itemView.findViewById(R.id.profilePic);
            post_desc_user = itemView.findViewById(R.id.post_desc_user);
            post_image.setOnClickListener(moreDetails);
            post_desc.setOnClickListener(moreDetails);

            user_pic.setOnClickListener(profile);
            post_desc_user.setOnClickListener(profile);
            post_user.setOnClickListener(profile);
        }

        public void bind(Post post) {
            //Bind data coming from posts to view elems
            post_desc_user.setText(post.getUser().getUsername());
            post_desc.setText(post.getDescription());
            post_user.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            if(image != null) {
                post_image.setVisibility(View.VISIBLE);
                Glide.with(context).load(post.getImage().getUrl()).into(post_image);
            }else{
                post_image.setVisibility(View.GONE);
            }

            ParseFile pfp = post.getUser().getParseFile("profilePic");

            Glide.with(context)
                    .load(pfp.getUrl())
                    .circleCrop()
                    .into(user_pic);
        }


        View.OnClickListener moreDetails = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();

                //grabbing curr post
                Post post = posts.get(position);
                Intent intent = new Intent(context,PostDetails.class);
                intent.putExtra("post", Parcels.wrap(post));
                context.startActivity(intent);
            }
        };

        View.OnClickListener profile = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();

                Post post = posts.get(position);
                ParseUser parseUser = post.getUser();
                Intent intent = new Intent(context,UserProfile.class);
                intent.putExtra("user",Parcels.wrap(parseUser));
                context.startActivity(intent);
            }
        };

    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }
}
