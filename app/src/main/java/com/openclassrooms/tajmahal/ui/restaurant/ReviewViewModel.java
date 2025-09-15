package com.openclassrooms.tajmahal.ui.restaurant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.tajmahal.data.service.RestaurantFakeApi;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReviewViewModel extends ViewModel {

    // LiveData qui contient la liste des avis affichés dans l’interface
    private final MutableLiveData<List<Review>> reviewsLiveData = new MutableLiveData<>();

    // Notre fake API qui fournit les avis
    private final RestaurantFakeApi fakeApi;

    // Constructeur : on initialise l’API et on charge les avis existants
    @Inject
    public ReviewViewModel() {
        this.fakeApi = new RestaurantFakeApi();
        loadReviews();
    }

    /**
     * Permet à l’UI (fragment/activité) d’accéder à la liste des avis.
     * L’UI va observer ce LiveData et sera automatiquement mise à jour
     * quand son contenu change.
     */
    public LiveData<List<Review>> getReviews() {
        return reviewsLiveData;
    }

    /**
     * Charge les avis depuis la Fake API et met à jour le LiveData.
     * → Ici on simule un appel réseau, mais on récupère simplement
     *   une liste prédéfinie dans RestaurantFakeApi.
     */
    public void loadReviews() {
        List<Review> reviews = fakeApi.getReviews();
        reviewsLiveData.setValue(reviews);
    }

    /**
     * Ajoute un nouvel avis à la liste et notifie les observateurs.
     * → On insère l’avis au début de la liste (index 0)
     *   pour qu’il apparaisse en premier dans le RecyclerView.
     */
    public void addReview(Review review) {
        List<Review> currentReviews = reviewsLiveData.getValue();

        // Si la liste est vide, on crée une nouvelle liste
        if (currentReviews == null) {
            currentReviews = new ArrayList<>();
        }

        // Ajout en première position
        currentReviews.add(0, review);

        // Mise à jour du LiveData (ce qui déclenche la mise à jour de l’UI)
        reviewsLiveData.setValue(currentReviews);
    }
}
