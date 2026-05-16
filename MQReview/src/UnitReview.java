import java.util.ArrayList;

public class UnitReview {
    private String unitCode;
    private String unitName;
    private ArrayList<Review> reviews;
    private ArrayList<CommentNode> replyChains; // index matches reviews; null means no replies yet

    public UnitReview(String unitCode, String unitName) {
        this.unitCode = unitCode;
        this.unitName = unitName;
        this.reviews = new ArrayList<>();
        this.replyChains = new ArrayList<>();
    }

    public String getUnitCode() { return unitCode; }
    public String getUnitName() { return unitName; }
    public ArrayList<Review> getReviews() { return reviews; }

    public void addReview(Review review) {
        reviews.add(review);
        replyChains.add(null);
    }

    /**
     * Appends a reply to the CommentNode chain for the review at the given index.
     * @param reviewIndex zero-based index of the review to reply to
     * @param reply       the reply text to append
     * @throws IndexOutOfBoundsException if reviewIndex is out of range
     */
    public void addReply(int reviewIndex, String reply) {
        if (reviewIndex < 0 || reviewIndex >= reviews.size()) {
            throw new IndexOutOfBoundsException("No review at index " + reviewIndex);
        }
        if (replyChains.get(reviewIndex) == null) {
            replyChains.set(reviewIndex, new CommentNode(reply));
        } else {
            replyChains.get(reviewIndex).append(reply);
        }
    }

    /**
     * Removes the review (and its reply chain) at the given index.
     * @param index zero-based position of the review to remove
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public void removeReview(int index) {
        if (index < 0 || index >= reviews.size()) {
            throw new IndexOutOfBoundsException("No review at index " + index);
        }
        reviews.remove(index);
        replyChains.remove(index);
    }

    /**
     * Returns the head of the reply chain for a review, or null if none exist.
     * @param reviewIndex zero-based index of the review
     * @return the first CommentNode in the chain, or null if no replies have been added
     */
    public CommentNode getReplyChain(int reviewIndex) {
        if (reviewIndex < 0 || reviewIndex >= replyChains.size()) return null;
        return replyChains.get(reviewIndex);
    }

    /**
     * Returns all reviews of the given type.
     * @param type the review type to filter by (UNIT, STAFF, or ASSESSMENT)
     * @return list of reviews matching the given type; empty list if none exist
     */
    public ArrayList<Review> getReviewsByType(Review.ReviewType type) {
        ArrayList<Review> filtered = new ArrayList<>();
        for (Review r : reviews) {
            if (r.getType() == type) filtered.add(r);
        }
        return filtered;
    }

    /**
     * Average rating across all reviews.
     * @return mean rating from 1.0 to 5.0, or 0.0 if no reviews exist
     */
    public double getAverageRating() {
        if (reviews.isEmpty()) return 0.0;
        int total = 0;
        for (Review r : reviews) total += r.getRating();
        return (double) total / reviews.size();
    }

    /**
     * Average rating for a specific review type.
     * @param type the review type to average (UNIT, STAFF, or ASSESSMENT)
     * @return mean rating from 1.0 to 5.0, or 0.0 if no reviews of that type exist
     */
    public double getAverageRatingByType(Review.ReviewType type) {
        ArrayList<Review> typed = getReviewsByType(type);
        if (typed.isEmpty()) return 0.0;
        int total = 0;
        for (Review r : typed) total += r.getRating();
        return (double) total / typed.size();
    }

    public void printSummary() {
        System.out.printf("%-10s %-35s Avg: %.2f/5  (%d reviews)%n",
            unitCode, unitName, getAverageRating(), reviews.size());
    }

    public void printAllReviews() {
        System.out.println("\n========================================");
        System.out.println("  " + unitCode + " — " + unitName);
        System.out.printf("  Overall average: %.2f/5  (%d reviews)%n",
            getAverageRating(), reviews.size());
        System.out.println("========================================");

        printCategory(Review.ReviewType.UNIT, "UNIT REVIEWS");
        printCategory(Review.ReviewType.STAFF, "STAFF REVIEWS");
        printCategory(Review.ReviewType.ASSESSMENT, "ASSESSMENT REVIEWS");
    }

    private void printCategory(Review.ReviewType type, String header) {
        ArrayList<Review> typed = getReviewsByType(type);
        if (typed.isEmpty()) return;
        System.out.println("\n--- " + header + " ---");
        for (int i = 0; i < reviews.size(); i++) {
            Review r = reviews.get(i);
            if (r.getType() != type) continue;
            System.out.println("[Review #" + (i + 1) + "]");
            System.out.println(r);
            CommentNode chain = replyChains.get(i);
            if (chain != null) {
                System.out.println("  Replies:");
                System.out.println(chain);
            }
            System.out.println();
        }
    }
}
