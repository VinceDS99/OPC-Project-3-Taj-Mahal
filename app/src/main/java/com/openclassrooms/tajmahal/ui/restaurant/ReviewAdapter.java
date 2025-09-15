package com.openclassrooms.tajmahal.ui.restaurant;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<Review> reviews;
    private final Context context;

    public ReviewAdapter(List<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.userName.setText(review.getUsername());
        holder.comment.setText(review.getComment());
        holder.ratingBar.setRating(review.getRate());

        // Couleur étoiles
        LayerDrawable starsReview = (LayerDrawable) holder.ratingBar.getProgressDrawable().mutate();
        starsReview.getDrawable(0).setColorFilter(Color.parseColor("#DADADA"), PorterDuff.Mode.SRC_IN);
        starsReview.getDrawable(1).setColorFilter(Color.parseColor("#F3BB44"), PorterDuff.Mode.SRC_IN);
        starsReview.getDrawable(2).setColorFilter(Color.parseColor("#F3BB44"), PorterDuff.Mode.SRC_IN);
        holder.ratingBar.setProgressDrawable(starsReview);

        // Image profil
        Glide.with(context)
                .load(review.getPicture().equals("profile_picture") ? R.drawable.profile_picture : review.getPicture())
                .circleCrop()
                .into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView profileImage;
        TextView userName;
        TextView comment;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.reviewProfileImage);
            userName = itemView.findViewById(R.id.reviewUserName);
            comment = itemView.findViewById(R.id.reviewComment);
            ratingBar = itemView.findViewById(R.id.reviewRatingBar);
        }
    }
}
