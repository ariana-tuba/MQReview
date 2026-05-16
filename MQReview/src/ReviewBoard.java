import java.util.ArrayList;

public class ReviewBoard {
    private ArrayList<UnitReview> units;

    public ReviewBoard() {
        this.units = new ArrayList<>();
    }

    /**
     * Adds a new unit. Returns false without adding if the unit code already exists,
     * preventing duplicates from being recorded.
     * @param unitCode unique code for the unit (e.g. "COMP1010")
     * @param unitName full name of the unit
     * @return true if the unit was added, false if the code already exists
     */
    public boolean addUnit(String unitCode, String unitName) {
        if (findUnit(unitCode) != null) return false;
        units.add(new UnitReview(unitCode, unitName));
        return true;
    }

    /**
     * Adds a review to the specified unit. If the unit doesn't exist yet,
     * it is created automatically using the provided unitName.
     * @param unitCode unique code for the unit
     * @param unitName unit name used only if the unit needs to be created
     * @param review   the review to add
     * @return true always (kept for consistency with addUnit's return type)
     */
    public boolean addReview(String unitCode, String unitName, Review review) {
        UnitReview unit = findUnit(unitCode);
        if (unit == null) {
            addUnit(unitCode, unitName);
            unit = findUnit(unitCode);
        }
        unit.addReview(review);
        return true;
    }

    /**
     * Returns the UnitReview for the given code (case-insensitive), or null if not found.
     * @param unitCode the unit code to look up
     * @return the matching UnitReview, or null if no unit with that code exists
     */
    public UnitReview findUnit(String unitCode) {
        if (unitCode == null) return null;
        for (UnitReview u : units) {
            if (u.getUnitCode().equalsIgnoreCase(unitCode)) return u;
        }
        return null;
    }

    /**
     * Returns reviews for a unit with rating >= minRating; empty list if unit not found.
     * @param unitCode  the unit code to filter reviews for
     * @param minRating minimum rating threshold (inclusive)
     * @return list of matching reviews, or an empty list if the unit doesn't exist
     */
    public ArrayList<Review> filterByMinRating(String unitCode, int minRating) {
        ArrayList<Review> results = new ArrayList<>();
        UnitReview unit = findUnit(unitCode);
        if (unit == null) return results;
        for (Review r : unit.getReviews()) {
            if (r.getRating() >= minRating) results.add(r);
        }
        return results;
    }

    /**
     * Searches all units for STAFF reviews where the person's name matches (case-insensitive).
     * @param name the staff member's name to search for
     * @return list of matching staff reviews across all units; empty list if none found
     */
    public ArrayList<Review> searchByStaffName(String name) {
        ArrayList<Review> results = new ArrayList<>();
        if (name == null || name.isBlank()) return results;
        for (UnitReview u : units) {
            for (Review r : u.getReviewsByType(Review.ReviewType.STAFF)) {
                if (r.getPerson() != null &&
                    r.getPerson().getName().equalsIgnoreCase(name)) {
                    results.add(r);
                }
            }
        }
        return results;
    }

    /**
     * Searches all units for ASSESSMENT reviews by assessment name (case-insensitive).
     * @param name the assessment name to search for
     * @return list of matching assessment reviews across all units; empty list if none found
     */
    public ArrayList<Review> searchByAssessmentName(String name) {
        ArrayList<Review> results = new ArrayList<>();
        if (name == null || name.isBlank()) return results;
        for (UnitReview u : units) {
            for (Review r : u.getReviewsByType(Review.ReviewType.ASSESSMENT)) {
                if (r.getAssessment() != null &&
                    r.getAssessment().getName().equalsIgnoreCase(name)) {
                    results.add(r);
                }
            }
        }
        return results;
    }

    /**
     * Searches all units for ASSESSMENT reviews matching the given assessment type.
     * @param type the assessment type to filter by (EXAM, ASSIGNMENT, or QUIZ)
     * @return list of matching assessment reviews across all units; empty list if none found
     */
    public ArrayList<Review> searchByAssessmentType(Assessment.AssessmentType type) {
        ArrayList<Review> results = new ArrayList<>();
        for (UnitReview u : units) {
            for (Review r : u.getReviewsByType(Review.ReviewType.ASSESSMENT)) {
                if (r.getAssessment() != null && r.getAssessment().getType() == type) {
                    results.add(r);
                }
            }
        }
        return results;
    }

    /**
     * Removes the review at the given index from the specified unit.
     * @param unitCode the unit to remove the review from
     * @param index    zero-based position of the review to remove
     * @return true if removed, false if the unit was not found
     * @throws IndexOutOfBoundsException if index is out of range for that unit
     */
    public boolean removeReview(String unitCode, int index) {
        UnitReview unit = findUnit(unitCode);
        if (unit == null) return false;
        unit.removeReview(index);
        return true;
    }

    public ArrayList<UnitReview> getAllUnits() { return units; }

    public void printAllUnits() {
        if (units.isEmpty()) {
            System.out.println("No units in the system.");
            return;
        }
        System.out.println("\n--- All Units ---");
        for (UnitReview u : units) {
            u.printSummary();
        }
    }
}
