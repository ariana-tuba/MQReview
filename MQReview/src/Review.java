/**
 * Represents a single review. Satisfies the "class containing another user-defined class"
 * requirement: every Review holds a Date, and optionally a Person or Assessment.
 */
public class Review {
    public enum ReviewType { UNIT, STAFF, ASSESSMENT }

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private int rating;
    private String comment;
    private Date date;
    private ReviewType type;
    private Person person;         // non-null only for STAFF reviews
    private Assessment assessment; // non-null only for ASSESSMENT reviews

    /**
     * Creates a unit-level review.
     * @param rating  overall rating from 1 (worst) to 5 (best)
     * @param comment free-text feedback about the unit
     * @param date    date the review was submitted
     * @throws IllegalArgumentException if rating is outside 1–5
     */
    public Review(int rating, String comment, Date date) {
        this(rating, comment, date, ReviewType.UNIT, null, null);
    }

    /**
     * Creates a staff review.
     * @param rating  overall rating from 1 (worst) to 5 (best)
     * @param comment free-text feedback about the staff member
     * @param date    date the review was submitted
     * @param person  the staff member being reviewed
     * @throws IllegalArgumentException if rating is outside 1–5
     */
    public Review(int rating, String comment, Date date, Person person) {
        this(rating, comment, date, ReviewType.STAFF, person, null);
    }

    /**
     * Creates an assessment review.
     * @param rating     overall rating from 1 (worst) to 5 (best)
     * @param comment    free-text feedback about the assessment
     * @param date       date the review was submitted
     * @param assessment the assessment being reviewed
     * @throws IllegalArgumentException if rating is outside 1–5
     */
    public Review(int rating, String comment, Date date, Assessment assessment) {
        this(rating, comment, date, ReviewType.ASSESSMENT, null, assessment);
    }

    private Review(int rating, String comment, Date date, ReviewType type,
                   Person person, Assessment assessment) {
        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new IllegalArgumentException(
                "Rating must be between " + MIN_RATING + " and " + MAX_RATING + ", got: " + rating);
        }
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.type = type;
        this.person = person;
        this.assessment = assessment;
    }

    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Date getDate() { return date; }
    public ReviewType getType() { return type; }
    public Person getPerson() { return person; }
    public Assessment getAssessment() { return assessment; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(type).append("] Rating: ").append(rating).append("/5");
        sb.append(" | Date: ").append(date);
        if (type == ReviewType.STAFF && person != null) {
            sb.append("\n  Staff: ").append(person);
        } else if (type == ReviewType.ASSESSMENT && assessment != null) {
            sb.append("\n  Assessment: ").append(assessment);
        }
        sb.append("\n  Comment: ").append(comment);
        return sb.toString();
    }
}
