import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles all write operations on the ReviewBoard: adding and removing units,
 * reviews, and replies.
 */
public class ReviewEditor {
    private Scanner input;
    private ReviewBoard board;

    /**
     * Creates a ReviewEditor backed by the given Scanner and ReviewBoard.
     * @param input shared Scanner for reading user input
     * @param board the ReviewBoard to modify
     */
    public ReviewEditor(Scanner input, ReviewBoard board) {
        this.input = input;
        this.board = board;
    }

    /**
     * Prompts the user for a unit code and name, then adds the unit to the board.
     */
    public void addUnit() {
        System.out.print("Unit code (e.g. COMP1010): ");
        String code = input.nextLine().trim().toUpperCase();
        System.out.print("Unit name: ");
        String name = input.nextLine().trim();
        if (board.addUnit(code, name)) {
            System.out.println("Unit " + code + " added successfully.");
        } else {
            System.out.println("Unit " + code + " already exists.");
        }
    }

    /**
     * Prompts the user for all review details, builds the Review, and adds it.
     */
    public void addReview() {
        System.out.print("Unit code: ");
        String code = input.nextLine().trim().toUpperCase();
        System.out.print("Unit name (used if unit is new): ");
        String unitName = input.nextLine().trim();

        System.out.println("Review type:");
        System.out.println("  1 = Overall unit experience");
        System.out.println("  2 = Lecturer or tutor");
        System.out.println("  3 = Specific assessment");
        System.out.print("Choice: ");
        String typeChoice = input.nextLine().trim();

        int rating = promptRating();
        System.out.print("Comment: ");
        String comment = input.nextLine().trim();
        Date date = promptDate();

        try {
            Review review = buildReview(typeChoice, rating, comment, date);
            if (review == null) return;
            board.addReview(code, unitName, review);
            System.out.println("Review added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to select a review from a unit, then removes it.
     */
    public void removeReview() {
        System.out.print("Unit code: ");
        String code = input.nextLine().trim().toUpperCase();
        UnitReview unit = board.findUnit(code);
        if (unit == null) {
            System.out.println("Unit " + code + " not found.");
            return;
        }
        if (unit.getReviews().isEmpty()) {
            System.out.println("No reviews for " + code + " yet.");
            return;
        }
        for (int i = 0; i < unit.getReviews().size(); i++) {
            System.out.println((i + 1) + ". " + unit.getReviews().get(i));
            System.out.println();
        }
        System.out.print("Select review number to remove: ");
        try {
            int index = Integer.parseInt(input.nextLine().trim()) - 1;
            board.removeReview(code, index);
            System.out.println("Review removed.");
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid selection.");
        }
    }

    /**
     * Prompts the user to select a review from a unit, then adds a reply to it.
     */
    public void replyToReview() {
        System.out.print("Unit code: ");
        String code = input.nextLine().trim().toUpperCase();
        UnitReview unit = board.findUnit(code);
        if (unit == null) {
            System.out.println("Unit not found.");
            return;
        }
        ArrayList<Review> reviews = unit.getReviews();
        if (reviews.isEmpty()) {
            System.out.println("No reviews for " + code + " yet.");
            return;
        }
        for (int i = 0; i < reviews.size(); i++) {
            System.out.println((i + 1) + ". " + reviews.get(i));
        }
        System.out.print("Select review number to reply to: ");
        try {
            int index = Integer.parseInt(input.nextLine().trim()) - 1;
            System.out.print("Your reply: ");
            String reply = input.nextLine().trim();
            unit.addReply(index, reply);
            System.out.println("Reply added.");
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid selection.");
        }
    }

    /**
     * Prompts for type-specific data and constructs the Review object.
     * @param typeChoice "1" for unit, "2" for staff, "3" for assessment
     * @param rating     pre-validated rating (1–5)
     * @param comment    pre-entered comment text
     * @param date       pre-entered review date
     * @return the constructed Review, or null if typeChoice was invalid
     */
    private Review buildReview(String typeChoice, int rating, String comment, Date date) {
        switch (typeChoice) {
            case "1":
                return new Review(rating, comment, date);

            case "2": {
                System.out.print("Staff name: ");
                String staffName = input.nextLine().trim();
                System.out.print("Role — enter TUTOR or press Enter for LECTURER: ");
                String roleStr = input.nextLine().trim().toUpperCase();
                Person.Role role = roleStr.equals("TUTOR") ? Person.Role.TUTOR : Person.Role.LECTURER;
                System.out.print("Department: ");
                String dept = input.nextLine().trim();
                return new Review(rating, comment, date, new Person(staffName, role, dept));
            }

            case "3": {
                System.out.print("Assessment name (e.g. Final Exam, Assignment 1): ");
                String aName = input.nextLine().trim();
                System.out.print("Type — EXAM, ASSIGNMENT, or QUIZ (default EXAM): ");
                String aTypeStr = input.nextLine().trim().toUpperCase();
                Assessment.AssessmentType aType;
                switch (aTypeStr) {
                    case "ASSIGNMENT": aType = Assessment.AssessmentType.ASSIGNMENT; break;
                    case "QUIZ":       aType = Assessment.AssessmentType.QUIZ;       break;
                    default:           aType = Assessment.AssessmentType.EXAM;
                }
                int difficulty = promptDifficulty();
                System.out.print("Assessment-specific comment (e.g. fairness, clarity): ");
                String aComment = input.nextLine().trim();
                return new Review(rating, comment, date,
                    new Assessment(aName, aType, difficulty, aComment));
            }

            default:
                System.out.println("Invalid type. Review not added.");
                return null;
        }
    }

    private int promptRating() {
        while (true) {
            System.out.print("Rating (1–5): ");
            try {
                int r = Integer.parseInt(input.nextLine().trim());
                if (r >= 1 && r <= 5) return r;
                System.out.println("  Must be between 1 and 5. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a number.");
            }
        }
    }

    private int promptDifficulty() {
        while (true) {
            System.out.print("Difficulty (1–5): ");
            try {
                int d = Integer.parseInt(input.nextLine().trim());
                if (d >= 1 && d <= 5) return d;
                System.out.println("  Must be between 1 and 5. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a number.");
            }
        }
    }

    private Date promptDate() {
        System.out.println("Enter review date:");
        int day   = readIntWithDefault("  Day   (1–31): ", 1);
        int month = readIntWithDefault("  Month (1–12): ", 1);
        int year  = readIntWithDefault("  Year  (e.g. 2026): ", 2026);
        return new Date(day, month, year);
    }

    private int readIntWithDefault(String prompt, int defaultValue) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(input.nextLine().trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
