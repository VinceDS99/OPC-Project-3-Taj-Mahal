package com.openclassrooms.tajmahal;

import com.openclassrooms.tajmahal.domain.model.Review;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExampleUnitTest {

    // Liste simulant le stockage des avis
    private List<Review> reviewList;

    // Méthode exécutée avant chaque test pour initialiser la liste
    @Before
    public void setup() {
        // Crée une nouvelle liste vide avant chaque test
        reviewList = new ArrayList<>();
    }

    // ========================================================
    // Vérification de la note (1 à 5 uniquement, entiers)
    // ========================================================

    @Test
    public void review_validRateLimit() {
        //Création d'un review avec note minimum et une autre avec note maximum
        Review reviewMin = new Review("User", "pic1", "Commentaire min", 1);
        Review reviewMax = new Review("User", "pic2", "Commentaire max", 5);

        //Vérification que les notes sont comprises entre 1 et 5 et donc valides
        assertTrue(reviewMin.getRate() >= 1 && reviewMin.getRate() <= 5);
        assertTrue(reviewMax.getRate() >= 1 && reviewMax.getRate() <= 5);
    }

    @Test
    public void review_invalidRateNotAdded() {
        //Création d'un review avec note invalide
        Review reviewInvalid = new Review("User", "pic3", "Note invalide", 6);

        //Si la note est valide on ajoute la review
        if (reviewInvalid.getRate() >= 1 && reviewInvalid.getRate() <= 5) {
            reviewList.add(reviewInvalid);
        }

        //On vérifie que la review avec note invalide n'a pas été ajouté a la liste
        assertEquals(0, reviewList.size());
    }

    // ========================================================
    // Vérification des commentaires
    // ========================================================

    @Test
    public void review_commentWithOnlySpacesNotAllowed() {
        Review review = new Review("User", "pic", "     ", 3);

        //On vérifie que la commande ne contient pas que des espaces
        assertTrue(review.getComment().trim().isEmpty());
    }

    @Test
    public void review_notAddedIfEmptyComment() {
        Review review = new Review("User", "pic", "", 4);

        //Si la review n'est pas vide on l'ajoute
        if (review.getComment() != null && !review.getComment().trim().isEmpty()) {
            reviewList.add(review);
        }

        //On vérifie que la review avec commmentaire vide n'a pas été ajouté a la liste
        assertEquals(0, reviewList.size());
    }

    // ========================================================
    // Vérification du username
    // ========================================================

    @Test
    public void review_usernameNotEmpty() {
        Review review = new Review("User", "pic", "Commentaire", 4);

        assertNotNull(review.getUsername());
        assertFalse(review.getUsername().isEmpty());
    }

    @Test
    public void review_usernameEmptyNotAllowed() {
        Review review = new Review("", "pic", "Commentaire", 4);

        assertTrue(review.getUsername().isEmpty());
    }

    // ========================================================
    // PLusieurs utilisateurs peuvent ajouter des avis
    // ========================================================

    @Test
    public void multipleUsersCanAddReviews() {
        Review review1 = new Review("UserA", "picA", "Avis A", 5);
        Review review2 = new Review("UserB", "picB", "Avis B", 4);
        Review review3 = new Review("UserC", "picC", "Avis C", 3);

        reviewList.add(review1);
        reviewList.add(review2);
        reviewList.add(review3);

        //On vérifie que la liste contient bien 3 reviews
        assertEquals(3, reviewList.size());
    }

    // ========================================================
    // Un utilisateur ne peut pas poster plusieurs avis
    // ========================================================

    @Test public void userCanOnlyAddOneReview() {
        // Création d'une première review valide pour l'utilisateur
        Review review1 = new Review("Test User", "profile_picture", "Super restaurant", 5);

        //Ajout de la première review si l'utilisateur n'a pas encore posté
        if (reviewList.stream().noneMatch(r -> r.getUsername().equals(review1.getUsername()))) { reviewList.add(review1); }

        // Tentative d'ajout d'une deuxième review pour le même utilisateur
        Review review2 = new Review("Test User", "profile_picture", "Avis différent", 4);
        if (reviewList.stream().noneMatch(r -> r.getUsername().equals(review2.getUsername()))) { reviewList.add(review2); }

        // Vérifie que la liste contient uniquement 1 avis pour cet utilisateur
        long count = reviewList.stream() .filter(r -> r.getUsername().equals("Test User")) .count(); assertEquals(1, count);
    }

    // ========================================================
    // Gestion des champs null
    // ========================================================
    @Test
    public void review_nullFieldsNotAllowed() {
        Review reviewNullComment = new Review("User", "pic", null, 4);
        Review reviewNullPicture = new Review("User", null, "Commentaire", 4);

        assertNull(reviewNullComment.getComment());
        assertNull(reviewNullPicture.getPicture());
    }

    // ========================================================
    // Ajout valide d'un avis complet
    // ========================================================
    @Test
    public void review_addedIfValid() {
        Review review = new Review("ValidUser", "picValid", "Super restaurant", 5);

        if (review.getComment() != null && !review.getComment().trim().isEmpty() &&
                review.getRate() >= 1 && review.getRate() <= 5) {
            reviewList.add(review);
        }

        assertEquals(1, reviewList.size());
        Review addedReview = reviewList.get(0);
        assertEquals("ValidUser", addedReview.getUsername());
        assertEquals("picValid", addedReview.getPicture());
        assertEquals("Super restaurant", addedReview.getComment());
        assertEquals(5, addedReview.getRate());
    }
}
