package com.openclassrooms.tajmahal.ui.restaurant;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.data.service.RestaurantFakeApi;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddReviewFragment extends Fragment {

    private LinearLayout reviewContainer;
    private EditText etReview;
    private RatingBar ratingBarInput;
    private Chip btnSubmit;
    private ShapeableImageView profileImage;
    private TextView tvUserName;

    private final String currentUserName = "Manon Garcia";
    private final String currentUserPicture = "profile_picture";

    public static AddReviewFragment newInstance() {
        return new AddReviewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_review, container, false);

        // Initialisation vues
        reviewContainer = view.findViewById(R.id.reviewContainer);
        etReview = view.findViewById(R.id.etReview);
        ratingBarInput = view.findViewById(R.id.ratingBar);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        profileImage = view.findViewById(R.id.profileImage);
        tvUserName = view.findViewById(R.id.tvUserName);

        // Charger image profil
        Glide.with(requireContext())
                .load(R.drawable.profile_picture)
                .circleCrop()
                .into(profileImage);

        tvUserName.setText(currentUserName);

        // Bouton retour
        view.findViewById(R.id.buttonBack).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // --- Initialisation RatingBar de saisie ---
        ratingBarInput.setNumStars(5);
        ratingBarInput.setStepSize(1f);
        ratingBarInput.setRating(0);
        ratingBarInput.setIsIndicator(false);

        // Couleurs personnalisées étoiles saisie
        LayerDrawable starsInput = (LayerDrawable) ratingBarInput.getProgressDrawable().mutate();
        starsInput.getDrawable(0).setColorFilter(Color.parseColor("#DADADA"), PorterDuff.Mode.SRC_IN);
        starsInput.getDrawable(1).setColorFilter(Color.parseColor("#F3BB44"), PorterDuff.Mode.SRC_IN);
        starsInput.getDrawable(2).setColorFilter(Color.parseColor("#F3BB44"), PorterDuff.Mode.SRC_IN);
        ratingBarInput.setProgressDrawable(starsInput);

        // Afficher reviews existantes
        List<Review> reviews = new RestaurantFakeApi().getReviews();
        for (Review review : reviews) {
            LinearLayout reviewBlock = createReviewBlock(review, requireContext());
            reviewContainer.addView(reviewBlock);
        }

        // Gestion bouton submit
        btnSubmit.setOnClickListener(v -> {
            String comment = etReview.getText().toString().trim();
            int rate = (int) ratingBarInput.getRating();

            if (comment.isEmpty()) {
                showAlert("Veuillez saisir un commentaire.");
                return;
            }

            if (rate < 1 || rate > 5) {
                showAlert("Veuillez choisir une note entre 1 et 5.");
                return;
            }

            // Création de la review
            Review newReview = new Review(currentUserName, currentUserPicture, comment, rate);

            // Ajouter au début
            LinearLayout newReviewBlock = createReviewBlock(newReview, requireContext());
            reviewContainer.addView(newReviewBlock, 0);

            // Réinitialiser champs
            etReview.setText("");
            ratingBarInput.setRating(0);
        });

        return view;
    }

    private void showAlert(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#CF2F2F"))
                .setTextColor(Color.WHITE)
                .show();
    }

    private LinearLayout createReviewBlock(Review review, Context context) {
        LinearLayout reviewLayout = new LinearLayout(context);
        reviewLayout.setOrientation(LinearLayout.HORIZONTAL);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        reviewLayout.setPadding(padding, padding, padding, padding);

        // Image profil
        ShapeableImageView profileImage = new ShapeableImageView(context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())
        );
        profileImage.setLayoutParams(imageParams);
        profileImage.setScaleType(ShapeableImageView.ScaleType.CENTER_CROP);

        Glide.with(context)
                .load(review.getPicture().equals("profile_picture") ? R.drawable.profile_picture : review.getPicture())
                .placeholder(R.drawable.profile_picture)
                .error(R.drawable.profile_picture)
                .circleCrop()
                .into(profileImage);

        reviewLayout.addView(profileImage);

        // Infos utilisateur
        LinearLayout infoLayout = new LinearLayout(context);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        int marginStart = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        infoParams.setMargins(marginStart, 0, 0, 0);
        infoLayout.setLayoutParams(infoParams);

        // Nom utilisateur
        TextView userName = new TextView(context);
        userName.setText(review.getUsername());
        userName.setTextColor(Color.parseColor("#666666"));
        userName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        userName.setTypeface(ResourcesCompat.getFont(context, R.font.jakarta_semibold));
        infoLayout.addView(userName);

        // RatingBar review
        RatingBar ratingBar = new RatingBar(context, null, android.R.attr.ratingBarStyleSmall);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1f);
        ratingBar.setRating(review.getRate());
        ratingBar.setIsIndicator(true);

        LinearLayout.LayoutParams ratingParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        ratingParams.setMargins(0, topMargin, 0, 0);
        ratingBar.setLayoutParams(ratingParams);

        // Couleurs étoiles review
        LayerDrawable starsReview = (LayerDrawable) ratingBar.getProgressDrawable().mutate();
        starsReview.getDrawable(0).setColorFilter(Color.parseColor("#DADADA"), PorterDuff.Mode.SRC_IN);
        starsReview.getDrawable(1).setColorFilter(Color.parseColor("#F3BB44"), PorterDuff.Mode.SRC_IN);
        starsReview.getDrawable(2).setColorFilter(Color.parseColor("#F3BB44"), PorterDuff.Mode.SRC_IN);
        ratingBar.setProgressDrawable(starsReview);

        infoLayout.addView(ratingBar);

        // Commentaire
        TextView commentText = new TextView(context);
        commentText.setText(review.getComment());
        commentText.setTextColor(Color.parseColor("#666666"));
        commentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        commentText.setTypeface(ResourcesCompat.getFont(context, R.font.jakarta_regular));
        infoLayout.addView(commentText);

        reviewLayout.addView(infoLayout);

        return reviewLayout;
    }
}
