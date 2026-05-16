import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class ReviewTest {

    private ReviewBoard board;
    private Date sampleDate;

    @Before
    public void setUp() {
        board      = new ReviewBoard();
        sampleDate = new Date(15, 4, 2026);
    }

    // -----------------------------------------------------------------------
    //  Rating boundary tests
    // -----------------------------------------------------------------------

    @Test
    public void testValidRatingLowerBound() {
        Review r = new Review(1, "Absolute worst", sampleDate);
        assertEquals("Rating 1 should be stored exactly", 1, r.getRating());
    }

    @Test
    public void testValidRatingUpperBound() {
        Review r = new Review(5, "Absolute best", sampleDate);
        assertEquals("Rating 5 should be stored exactly", 5, r.getRating());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRatingZero_throwsIllegalArgument() {
        // 0 is below the valid range of 1–5
        new Review(0, "Zero is not a valid rating", sampleDate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRatingSix_throwsIllegalArgument() {
        // 6 is above the valid range of 1–5
        new Review(6, "Six is not a valid rating", sampleDate);
    }

    // -----------------------------------------------------------------------
    //  Null input tests
    // -----------------------------------------------------------------------

    @Test
    public void testFindUnit_nullCode_returnsNull() {
        board.addUnit("COMP1010", "Intro to Programming");
        assertNull("findUnit(null) must return null, not throw", board.findUnit(null));
    }

    @Test
    public void testSearchByStaffName_null_returnsEmptyList() {
        ArrayList<Review> results = board.searchByStaffName(null);
        assertNotNull(results);
        assertTrue("Null staff name search should return an empty list", results.isEmpty());
    }

    @Test
    public void testSearchByStaffName_blank_returnsEmptyList() {
        ArrayList<Review> results = board.searchByStaffName("   ");
        assertTrue("Blank staff name search should return an empty list", results.isEmpty());
    }

    @Test
    public void testSearchByAssessmentName_null_returnsEmptyList() {
        ArrayList<Review> results = board.searchByAssessmentName(null);
        assertNotNull(results);
        assertTrue("Null assessment name search should return an empty list", results.isEmpty());
    }

    // -----------------------------------------------------------------------
    //  Empty-list / non-existent unit edge cases
    // -----------------------------------------------------------------------

    @Test
    public void testFindUnit_nonExistent_returnsNull() {
        assertNull("Unknown unit code should return null", board.findUnit("FAKE9999"));
    }

    @Test
    public void testAverageRating_emptyUnit_returnsZero() {
        board.addUnit("COMP2010", "Empty Unit");
        UnitReview unit = board.findUnit("COMP2010");
        assertEquals("Average for a unit with no reviews should be 0.0",
            0.0, unit.getAverageRating(), 0.001);
    }

    @Test
    public void testAverageRatingByType_noReviewsOfType_returnsZero() {
        board.addUnit("COMP3010", "Mixed Unit");
        board.addReview("COMP3010", "Mixed Unit", new Review(4, "Good overall", sampleDate));
        UnitReview unit = board.findUnit("COMP3010");
        assertEquals("Average for STAFF type when none exist should be 0.0",
            0.0, unit.getAverageRatingByType(Review.ReviewType.STAFF), 0.001);
    }

    @Test
    public void testFilterByMinRating_noMatchingReviews_returnsEmpty() {
        board.addUnit("COMP4010", "Low-rated Unit");
        board.addReview("COMP4010", "Low-rated Unit", new Review(2, "Below average", sampleDate));
        ArrayList<Review> results = board.filterByMinRating("COMP4010", 4);
        assertTrue("No reviews should match a min rating of 4 when only 2-star exists",
            results.isEmpty());
    }

    @Test
    public void testFilterByMinRating_nonExistentUnit_returnsEmpty() {
        ArrayList<Review> results = board.filterByMinRating("FAKE0000", 3);
        assertNotNull(results);
        assertTrue("Filtering on an unknown unit should return an empty list", results.isEmpty());
    }

    // -----------------------------------------------------------------------
    //  Duplicate unit guard
    // -----------------------------------------------------------------------

    @Test
    public void testAddDuplicateUnit_notAdded() {
        boolean firstAdd  = board.addUnit("COMP5010", "Original Name");
        boolean secondAdd = board.addUnit("COMP5010", "Duplicate Attempt");
        assertTrue("First add of COMP5010 should succeed", firstAdd);
        assertFalse("Second add of COMP5010 with same code should be rejected", secondAdd);
        int count = 0;
        for (UnitReview u : board.getAllUnits()) {
            if (u.getUnitCode().equalsIgnoreCase("COMP5010")) count++;
        }
        assertEquals("Exactly one COMP5010 entry should exist in the board", 1, count);
    }

    // -----------------------------------------------------------------------
    //  Average rating calculation
    // -----------------------------------------------------------------------

    @Test
    public void testAverageRating_calculatesCorrectly() {
        board.addUnit("COMP6010", "Calculation Test");
        board.addReview("COMP6010", "Calculation Test", new Review(4, "Pretty good", sampleDate));
        board.addReview("COMP6010", "Calculation Test", new Review(2, "Below average", sampleDate));
        UnitReview unit = board.findUnit("COMP6010");
        assertEquals("Average of 4 and 2 should be 3.0", 3.0, unit.getAverageRating(), 0.001);
    }

    // -----------------------------------------------------------------------
    //  CommentNode recursive linked list
    // -----------------------------------------------------------------------

    @Test
    public void testCommentNode_chainBuildsCorrectly() {
        board.addUnit("COMP7010", "Reply Test Unit");
        board.addReview("COMP7010", "Reply Test Unit", new Review(3, "Decent", sampleDate));
        UnitReview unit = board.findUnit("COMP7010");

        unit.addReply(0, "First reply");
        unit.addReply(0, "Second reply");
        unit.addReply(0, "Third reply");

        CommentNode chain = unit.getReplyChain(0);
        assertNotNull("Chain should not be null after adding replies", chain);
        assertEquals("Chain should contain 3 nodes", 3, chain.count());
        assertEquals("First node should hold first reply", "First reply", chain.getReply());
        assertEquals("Second node should hold second reply",
            "Second reply", chain.getNext().getReply());
        assertEquals("Third node should hold third reply",
            "Third reply", chain.getNext().getNext().getReply());
        assertNull("Tail node's next pointer should be null",
            chain.getNext().getNext().getNext());
    }

    @Test
    public void testCommentNode_noReplies_chainIsNull() {
        board.addUnit("COMP8010", "No Reply Unit");
        board.addReview("COMP8010", "No Reply Unit", new Review(5, "Great!", sampleDate));
        UnitReview unit = board.findUnit("COMP8010");
        assertNull("Reply chain should be null when no replies have been added",
            unit.getReplyChain(0));
    }

    // -----------------------------------------------------------------------
    //  Assessment difficulty boundary
    // -----------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void testAssessmentDifficulty_zero_throwsIllegalArgument() {
        new Assessment("Final Exam", Assessment.AssessmentType.EXAM, 0, "Too easy");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssessmentDifficulty_six_throwsIllegalArgument() {
        new Assessment("Final Exam", Assessment.AssessmentType.EXAM, 6, "Impossible");
    }

    // -----------------------------------------------------------------------
    //  Cross-unit staff search
    // -----------------------------------------------------------------------

    @Test
    public void testSearchByStaffName_findsReviewsAcrossUnits() {
        Person drSmith = new Person("Dr. Smith", Person.Role.LECTURER, "Computing");
        board.addUnit("COMP1010", "Intro");
        board.addUnit("COMP2010", "Data Structures");
        board.addReview("COMP1010", "Intro",
            new Review(5, "Brilliant teacher", sampleDate, drSmith));
        board.addReview("COMP2010", "Data Structures",
            new Review(4, "Very helpful", sampleDate, drSmith));

        ArrayList<Review> results = board.searchByStaffName("Dr. Smith");
        assertEquals("Should find 2 reviews for Dr. Smith across two units", 2, results.size());
    }

    // -----------------------------------------------------------------------
    //  Remove review
    // -----------------------------------------------------------------------

    @Test
    public void testRemoveReview_removesCorrectly() {
        board.addUnit("COMP9010", "Remove Test");
        board.addReview("COMP9010", "Remove Test", new Review(3, "First",  sampleDate));
        board.addReview("COMP9010", "Remove Test", new Review(4, "Second", sampleDate));
        board.removeReview("COMP9010", 0);
        UnitReview unit = board.findUnit("COMP9010");
        assertEquals("One review should remain after removing the first", 1, unit.getReviews().size());
        assertEquals("Remaining review should be the second one", 4, unit.getReviews().get(0).getRating());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveReview_invalidIndex_throwsException() {
        board.addUnit("COMP9020", "Remove Bounds Test");
        board.addReview("COMP9020", "Remove Bounds Test", new Review(2, "Only review", sampleDate));
        board.removeReview("COMP9020", 5);
    }

    @Test
    public void testRemoveReview_unknownUnit_returnsFalse() {
        assertFalse("Removing from a non-existent unit should return false",
            board.removeReview("FAKE1234", 0));
    }

    @Test
    public void testSearchByStaffName_notFound_returnsEmpty() {
        board.addUnit("COMP1010", "Intro");
        Person p = new Person("Dr. Jones", Person.Role.TUTOR, "Computing");
        board.addReview("COMP1010", "Intro",
            new Review(3, "Average tutor", sampleDate, p));

        ArrayList<Review> results = board.searchByStaffName("Dr. Smith");
        assertTrue("Searching for an unknown staff member should return an empty list",
            results.isEmpty());
    }
}
