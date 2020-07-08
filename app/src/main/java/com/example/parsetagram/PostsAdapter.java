package com.example.parsetagram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView post_user;
        private ImageView post_image;
        private TextView post_desc;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            post_user = itemView.findViewById(R.id.post_user);
            post_image = itemView.findViewById(R.id.post_pic);
            post_desc = itemView.findViewById(R.id.post_desc);
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            //Bind data coming from posts to view elems
            post_desc.setText(post.getDescription());
            post_user.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            if(image != null) {
                post_image.setVisibility(View.VISIBLE);
                Glide.with(context).load(post.getImage().getUrl()).into(post_image);
            }else{
                post_image.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            //grabbing curr post
            Post post = posts.get(position);
            Intent intent = new Intent(context,PostDetails.class);
            intent.putExtra("post", Parcels.wrap(post));
            context.startActivity(intent);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
