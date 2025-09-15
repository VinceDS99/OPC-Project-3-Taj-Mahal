package com.openclassrooms.tajmahal.ui.restaurant;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.data.service.RestaurantFakeApi;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddReviewFragment extends Fragment {

    // Vues de saisie
    private EditText etReview;
    private RatingBar ratingBarInput;
    private Chip btnSubmit;
    private ShapeableImageView profileImage;
    private TextView tvUserName;

    // RecyclerView et son Adapter
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    // Utilisateur courant
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

        // --- Initialisation des vues ---
        etReview = view.findViewById(R.id.etReview);
        ratingBarInput = view.findViewById(R.id.ratingBar);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        profileImage = view.findViewById(R.id.profileImage);
        tvUserName = view.findViewById(R.id.tvUserName);
        recyclerViewReviews = view.findViewById(R.id.recyclerViewReviews);

        // Charger l'image du profil de l'utilisateur
        Glide.with(requireContext())
                .load(R.drawable.profile_picture)
                .circleCrop()
                .into(profileImage);
        tvUserName.setText(currentUserName);

        // Bouton retour
        view.findViewById(R.id.buttonBack).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // --- Configuration de la RatingBar de saisie ---
        ratingBarInput.setNumStars(5);
        ratingBarInput.setStepSize(1f);
        ratingBarInput.setRating(0);
        ratingBarInput.setIsIndicator(false);

        // Couleurs personnalisées des étoiles
        LayerDrawable starsInput = (LayerDrawable) ratingBarInput.getProgressDrawable().mutate();
        starsInput.getDrawable(0).setColorFilter(Color.parseColor("#DADADA"), PorterDuff.Mode.SRC_IN);
        starsInput.getDrawable(1).setColorFilter(Color.parseColor("#F3BB44"), PorterDuff.Mode.SRC_IN);
        starsInput.getDrawable(2).setColorFilter(Color.parseColor("#F3BB44"), PorterDuff.Mode.SRC_IN);
        ratingBarInput.setProgressDrawable(starsInput);

        // --- Configuration du RecyclerView ---
        reviewList.addAll(new RestaurantFakeApi().getReviews()); // récupérer les reviews existantes
        reviewAdapter = new ReviewAdapter(reviewList, requireContext());
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewReviews.setAdapter(reviewAdapter);


        // --- Gestion du bouton de validation ---
        btnSubmit.setOnClickListener(v -> {
            String comment = etReview.getText().toString().trim();
            int rate = (int) ratingBarInput.getRating();

            // Vérifications simples
            if (comment.isEmpty()) {
                showAlert("Veuillez saisir un commentaire.");
                return;
            }
            if (rate < 1 || rate > 5) {
                showAlert("Veuillez choisir une note entre 1 et 5.");
                return;
            }

            // Création d'une nouvelle review
            Review newReview = new Review(currentUserName, currentUserPicture, comment, rate);

            // Ajout au début de la liste et mise à jour du RecyclerView
            reviewList.add(0, newReview);
            reviewAdapter.notifyItemInserted(0);
            recyclerViewReviews.scrollToPosition(0);

            // Réinitialisation des champs de saisie
            etReview.setText("");
            ratingBarInput.setRating(0);
        });

        return view;
    }

    // Affiche une alerte en bas de l'écran
    private void showAlert(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#CF2F2F"))
                .setTextColor(Color.WHITE)
                .show();
    }
}
