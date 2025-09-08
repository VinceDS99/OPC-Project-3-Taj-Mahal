package com.openclassrooms.tajmahal.ui.restaurant;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.data.service.RestaurantFakeApi;
import com.openclassrooms.tajmahal.domain.model.Restaurant;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * DetailsViewModel is responsible for preparing and managing the data for the {@link DetailsFragment}.
 * It communicates with the {@link RestaurantRepository} to fetch restaurant details and provides
 * utility methods related to the restaurant UI.
 * <p>
 * Added functionality: calculates review stats (average, total, progress bar percentages) using MVVM.
 */
@HiltViewModel
public class DetailsViewModel extends ViewModel {

    private final RestaurantRepository restaurantRepository;

    // LiveData pour les stats des reviews
    private final MutableLiveData<ReviewStats> reviewStatsLiveData = new MutableLiveData<>();

    @Inject
    public DetailsViewModel(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public LiveData<Restaurant> getTajMahalRestaurant() {
        return restaurantRepository.getRestaurant();
    }

    public LiveData<ReviewStats> getReviewStats() {
        return reviewStatsLiveData;
    }

    /**
     * Calcule les stats des reviews (moyenne, total, pourcentage pour chaque barre)
     * et les expose via LiveData.
     */
    public void loadReviews() {

        RestaurantFakeApi api = new RestaurantFakeApi();
        List<Review> reviews = api.getReviews();

        // Moyenne
        double average = 0.0;
        int totalReviews = reviews.size();
        int[] counts = new int[5];

        if (!reviews.isEmpty()) {
            int total = 0;
            for (Review r : reviews) {
                int rate = r.getRate();

                //On vérifié qu'il n'ya pas de note 0 étoiles
                // S'il y a une note on incrémente le counts - 1 ( si note 1 etoiles on incrémente counts[0] qui contiendra le nombre de notes 1 étoiles)
                if (rate >= 1 && rate <= 5) counts[rate - 1]++;
                total += rate;
            }
            average = (double) total / totalReviews;
        }

        // Progress bars
        int[] percentages = new int[5];
        for (int i = 0; i < 5; i++) {
            percentages[i] = totalReviews == 0 ? 0 : counts[i] * 100 / totalReviews;
        }

        reviewStatsLiveData.setValue(new ReviewStats(average, totalReviews, percentages));
    }

    /**
     * Classe interne représentant les stats des reviews.
     */
    public static class ReviewStats {
        public final double average;
        public final int totalReviews;
        public final int[] percentages;

        public ReviewStats(double average, int totalReviews, int[] percentages) {
            this.average = average;
            this.totalReviews = totalReviews;
            this.percentages = percentages;
        }
    }


    /**
     * Récupère le jour courant en français.
     */
    public String getCurrentDay(Context context) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayString;

        switch (dayOfWeek) {
            case Calendar.MONDAY: dayString = context.getString(R.string.monday); break;
            case Calendar.TUESDAY: dayString = context.getString(R.string.tuesday); break;
            case Calendar.WEDNESDAY: dayString = context.getString(R.string.wednesday); break;
            case Calendar.THURSDAY: dayString = context.getString(R.string.thursday); break;
            case Calendar.FRIDAY: dayString = context.getString(R.string.friday); break;
            case Calendar.SATURDAY: dayString = context.getString(R.string.saturday); break;
            case Calendar.SUNDAY: dayString = context.getString(R.string.sunday); break;
            default: dayString = "";
        }
        return dayString;
    }


}
