import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles all read and display operations on the ReviewBoard: viewing reviews,
 * filtering by rating, searching by staff or assessment, and showing averages.
 */
public class ReviewViewer {
    private Scanner input;
    private ReviewBoard board;

    /**
     * Creates a ReviewViewer backed by the given Scanner and ReviewBoard.
     * @param input shared Scanner for reading user input
     * @param board the ReviewBoard to query
     */
    public ReviewViewer(Scanner input, ReviewBoard board) {
        this.input = input;
        this.board = board;
    }

    /**
     * Prompts for a unit code and prints all its reviews.
     */
    public void viewUnitReviews() {
        System.out.print("Unit code: ");
        String code = input.nextLine().trim().toUpperCase();
        UnitReview unit = board.findUnit(code);
        if (unit == null) {
            System.out.println("Unit " + code + " not found.");
        } else {
            unit.printAllReviews();
        }
    }

    /**
     * Prompts for a unit code and minimum rating, then prints all matching reviews.
     */
    public void filterByRating() {
        System.out.print("Unit code: ");
        String code = input.nextLine().trim().toUpperCase();
        System.out.print("Minimum rating (1–5): ");
        try {
            int min = Integer.parseInt(input.nextLine().trim());
            ArrayList<Review> results = board.filterByMinRating(code, min);
            if (results.isEmpty()) {
                System.out.println("No reviews with rating >= " + min + ".");
            } else {
                System.out.println("Reviews for " + code + " rated " + min + " or above:");
                for (Review r : results) {
                    System.out.println(r);
                    System.out.println();
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered.");
        }
    }

    /**
     * Prompts for a staff name and prints all reviews that mention that person.
     */
    public void searchByStaff() {
        System.out.print("Staff name to search: ");
        String name = input.nextLine().trim();
        ArrayList<Review> results = board.searchByStaffName(name);
        if (results.isEmpty()) {
            System.out.println("No reviews found for staff: " + name);
        } else {
            System.out.println("Reviews for " + name + ":");
            for (Review r : results) {
                System.out.println(r);
                System.out.println();
            }
        }
    }

    /**
     * Prompts for an assessment name or type and prints all matching reviews.
     */
    public void searchByAssessment() {
        System.out.println("Search by: 1 = Name   2 = Type");
        System.out.print("Choice: ");
        String choice = input.nextLine().trim();

        if (choice.equals("1")) {
            System.out.print("Assessment name: ");
            String name = input.nextLine().trim();
            ArrayList<Review> results = board.searchByAssessmentName(name);
            if (results.isEmpty()) {
                System.out.println("No assessment reviews found for: " + name);
            } else {
                for (Review r : results) { System.out.println(r); System.out.println(); }
            }
        } else {
            System.out.print("Type — EXAM, ASSIGNMENT, or QUIZ: ");
            String typeStr = input.nextLine().trim().toUpperCase();
            Assessment.AssessmentType type;
            switch (typeStr) {
                case "ASSIGNMENT": type = Assessment.AssessmentType.ASSIGNMENT; break;
                case "QUIZ":       type = Assessment.AssessmentType.QUIZ;       break;
                default:           type = Assessment.AssessmentType.EXAM;
            }
            ArrayList<Review> results = board.searchByAssessmentType(type);
            if (results.isEmpty()) {
                System.out.println("No " + type + " reviews found.");
            } else {
                for (Review r : results) { System.out.println(r); System.out.println(); }
            }
        }
    }

    /**
     * Prints average ratings for a specific unit, or all units if no code is entered.
     */
    public void viewAverages() {
        System.out.print("Unit code (or press Enter for all units): ");
        String code = input.nextLine().trim().toUpperCase();
        if (code.isEmpty()) {
            board.printAllUnits();
        } else {
            UnitReview unit = board.findUnit(code);
            if (unit == null) {
                System.out.println("Unit not found.");
            } else {
                System.out.printf("%-10s %s%n", unit.getUnitCode(), unit.getUnitName());
                System.out.printf("  Overall            : %.2f/5%n", unit.getAverageRating());
                System.out.printf("  Unit reviews       : %.2f/5%n",
                    unit.getAverageRatingByType(Review.ReviewType.UNIT));
                System.out.printf("  Staff reviews      : %.2f/5%n",
                    unit.getAverageRatingByType(Review.ReviewType.STAFF));
                System.out.printf("  Assessment reviews : %.2f/5%n",
                    unit.getAverageRatingByType(Review.ReviewType.ASSESSMENT));
            }
        }
    }
}
