import java.util.Scanner;

/**
 * Main menu controller for MQReview. Owns the ReviewBoard and Scanner,
 * and delegates user actions to ReviewEditor and ReviewViewer.
 */
public class ReviewBoardUI {
    private static final String SAVE_FILE = "reviews.txt";

    private ReviewBoard board;
    private Scanner input;
    private ReviewEditor editor;
    private ReviewViewer viewer;

    /**
     * Initialises the controller: loads existing data and creates the handler objects.
     */
    public ReviewBoardUI() {
        input  = new Scanner(System.in);
        board  = tryLoad();
        editor = new ReviewEditor(input, board);
        viewer = new ReviewViewer(input, board);
    }

    /**
     * Starts the main menu loop, running until the user chooses to exit.
     */
    public void run() {
        System.out.println("============================================");
        System.out.println("    MQReview — Macquarie University Reviews");
        System.out.println("============================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = input.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1":  editor.addUnit();            break;
                case "2":  editor.addReview();          break;
                case "3":  viewer.viewUnitReviews();    break;
                case "4":  editor.replyToReview();      break;
                case "5":  viewer.filterByRating();     break;
                case "6":  viewer.searchByStaff();      break;
                case "7":  viewer.searchByAssessment(); break;
                case "8":  viewer.viewAverages();       break;
                case "9":  saveData();                  break;
                case "10": editor.removeReview();       break;
                case "0":
                    saveData();
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        input.close();
    }

    private void printMenu() {
        System.out.println("\n--- MQReview Menu ---");
        System.out.println(" 1. Add a unit");
        System.out.println(" 2. Add a review (unit / staff / assessment)");
        System.out.println(" 3. View all reviews for a unit");
        System.out.println(" 4. Reply to a review");
        System.out.println(" 5. Filter reviews by minimum rating");
        System.out.println(" 6. Search reviews by staff name");
        System.out.println(" 7. Search reviews by assessment");
        System.out.println(" 8. View average ratings");
        System.out.println(" 9. Save data");
        System.out.println("10. Remove a review");
        System.out.println(" 0. Exit");
        System.out.print("Enter choice: ");
    }

    private ReviewBoard tryLoad() {
        try {
            ReviewBoard loaded = FileHandler.load(SAVE_FILE);
            if (!loaded.getAllUnits().isEmpty()) {
                System.out.println("Loaded existing data from " + SAVE_FILE);
            }
            return loaded;
        } catch (Exception e) {
            System.out.println("Starting fresh — no existing save file found.");
            return new ReviewBoard();
        }
    }

    private void saveData() {
        try {
            FileHandler.save(board, SAVE_FILE);
        } catch (Exception e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }
}
