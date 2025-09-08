package com.openclassrooms.tajmahal.ui.restaurant;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.data.service.RestaurantFakeApi;
import com.openclassrooms.tajmahal.databinding.FragmentDetailsBinding;
import com.openclassrooms.tajmahal.domain.model.Restaurant;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * DetailsFragment is the entry point of the application and serves as the primary UI.
 * It displays details about a restaurant and provides functionality to open its location
 * in a map, call its phone number, or view its website.
 * <p>
 * This class uses {@link FragmentDetailsBinding} for data binding to its layout and
 * {@link DetailsViewModel} to interact with data sources and manage UI-related data.
 */
@AndroidEntryPoint
public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;

    private DetailsViewModel detailsViewModel;

    /**
     * This method is called when the fragment is first created.
     * It's used to perform one-time initialization.
     *
     * @param savedInstanceState A bundle containing previously saved instance state.
     * If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method is called immediately after `onCreateView()`.
     * Use this method to perform final initialization once the fragment views have been inflated.
     *
     * @param view The View returned by `onCreateView()`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupViewModel();


        detailsViewModel.getTajMahalRestaurant().observe(getViewLifecycleOwner(), this::updateUIWithRestaurant);


        detailsViewModel.getReviewStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats == null) return;

            // Mettre à jour UI
            binding.RatingNumber.setText(String.format(Locale.getDefault(), "%.1f", stats.average));
            binding.ratingBar.setRating((float) stats.average);
            binding.ReviewCount.setText("(" + stats.totalReviews + ")");
            binding.progressBar1.setProgress(stats.percentages[0]);
            binding.progressBar2.setProgress(stats.percentages[1]);
            binding.progressBar3.setProgress(stats.percentages[2]);
            binding.progressBar4.setProgress(stats.percentages[3]);
            binding.progressBar5.setProgress(stats.percentages[4]);
        });

        // Appel du chargement des reviews dans le ViewModel
        detailsViewModel.loadReviews();


        //Changement de page lors du click sur "Laisser un avis"
        binding.addReview.setOnClickListener(v -> {
            //Remplacement du fragment actuel par le AddReviewFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, AddReviewFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        });

    }


    //Première méthode changé pour non respect du MVVM
//    private void loadReviews() {
//        //Récupération des reviews
//        RestaurantFakeApi api = new RestaurantFakeApi();
//        List<Review> reviews = api.getReviews();
//
//        //Initialisation variable 'average' en décimales
//        double average = 0.0;
//
//        if (!reviews.isEmpty()) {
//            int total = 0;
//            for (Review review : reviews) {
//                //On ajoute chaque note au total
//                total += review.getRate();
//            }
//            // Moyenne = total / nombre de reviews
//            average = (double) total / reviews.size();
//        }
//
//        binding.RatingNumber.setText(String.format(Locale.getDefault(), "%.1f", average));
//        binding.ratingBar.setRating((float) average);
//        binding.ReviewCount.setText("(" + reviews.size() + ")");
//
//
//        int[] counts = new int[5];
//        for (Review review : reviews) {
//            int rate = review.getRate();
//            //On vérifié qu'il n'ya pas de note 0 étoiles
//            // S'il y a une note on incrémente le counts - 1 ( si note 1 etoiles on incrémente counts[0] qui contiendra le nombre de notes 1 étoiles)
//            if (rate >= 1 && rate <= 5) counts[rate - 1]++;
//        }
//
//        int totalReviews = reviews.size();
//
//        //Calcul des pourcentages de présence de chaque note (si 2 reviews sur 5 ont 1 étoile → 2*100/5 = 40 → progress bar 1 affiche 40%)
//        binding.progressBar1.setProgress(counts[0] * 100 / totalReviews);
//        binding.progressBar2.setProgress(counts[1] * 100 / totalReviews);
//        binding.progressBar3.setProgress(counts[2] * 100 / totalReviews);
//        binding.progressBar4.setProgress(counts[3] * 100 / totalReviews);
//        binding.progressBar5.setProgress(counts[4] * 100 / totalReviews);
//    }









    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * The fragment should not add the view itself but return it.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Returns the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false); // Binds the layout using view binding.
        return binding.getRoot(); // Returns the root view.
    }


    /**
     * Sets up the UI-specific properties, such as system UI flags and status bar color.
     */
    private void setupUI() {
        Window window = requireActivity().getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * Initializes the ViewModel for this activity.
     */
    private void setupViewModel() {
        detailsViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
    }

    /**
     * Updates the UI components with the provided restaurant data.
     *
     * @param restaurant The restaurant object containing details to be displayed.
     */
    private void updateUIWithRestaurant(Restaurant restaurant) {
        if (restaurant == null) return;

        binding.tvRestaurantName.setText(restaurant.getName());
        binding.tvRestaurantDay.setText(detailsViewModel.getCurrentDay(requireContext()));
        binding.tvRestaurantType.setText(String.format("%s %s", getString(R.string.restaurant), restaurant.getType()));
        binding.tvRestaurantHours.setText(restaurant.getHours());
        binding.tvRestaurantAddress.setText(restaurant.getAddress());
        binding.tvRestaurantWebsite.setText(restaurant.getWebsite());
        binding.tvRestaurantPhoneNumber.setText(restaurant.getPhoneNumber());
        binding.chipOnPremise.setVisibility(restaurant.isDineIn() ? View.VISIBLE : View.GONE);
        binding.chipTakeAway.setVisibility(restaurant.isTakeAway() ? View.VISIBLE : View.GONE);

        binding.buttonAdress.setOnClickListener(v -> openMap(restaurant.getAddress()));
        binding.buttonPhone.setOnClickListener(v -> dialPhoneNumber(restaurant.getPhoneNumber()));
        binding.buttonWebsite.setOnClickListener(v -> openBrowser(restaurant.getWebsite()));
    }

    /**
     * Opens the provided address in Google Maps or shows an error if Google Maps
     * is not installed.
     *
     * @param address The address to be shown in Google Maps.
     */
    private void openMap(String address) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(requireActivity(), R.string.maps_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Dials the provided phone number or shows an error if there's no dialing application
     * installed.
     *
     * @param phoneNumber The phone number to be dialed.
     */
    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(requireActivity(), R.string.phone_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens the provided website URL in a browser or shows an error if there's no
     * browser installed.
     *
     * @param websiteUrl The URL of the website to be opened.
     */
    private void openBrowser(String websiteUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(requireActivity(), R.string.no_browser_found, Toast.LENGTH_SHORT).show();
        }
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

}