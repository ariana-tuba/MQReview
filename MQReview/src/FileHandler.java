import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

/**
 * Saves and loads all review data to/from a plain-text file using PrintWriter and Scanner.
 *
 * File format per unit:
 *   UNIT_START
 *   <unitCode>
 *   <unitName>
 *     REVIEW_START
 *     <type: UNIT|STAFF|ASSESSMENT>
 *     <rating>
 *     <comment>
 *     <day> <month> <year>
 *     [if STAFF:       name / role / department]
 *     [if ASSESSMENT:  name / type / difficulty / comment]
 *     <replyCount>
 *     [<reply> …]
 *     REVIEW_END
 *   UNIT_END
 *
 * Limitation: comment and reply text must not contain the sentinel strings
 * UNIT_START, UNIT_END, REVIEW_START, or REVIEW_END.
 */
public class FileHandler {
    private static final String UNIT_START   = "UNIT_START";
    private static final String UNIT_END     = "UNIT_END";
    private static final String REVIEW_START = "REVIEW_START";
    private static final String REVIEW_END   = "REVIEW_END";

    /**
     * Saves all review data in the board to a plain-text file.
     * @param board    the ReviewBoard whose data should be saved
     * @param filename path to the output file (created or overwritten)
     * @throws IOException if the file cannot be written
     */
    public static void save(ReviewBoard board, String filename) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
        for (UnitReview unit : board.getAllUnits()) {
            writer.println(UNIT_START);
            writer.println(unit.getUnitCode());
            writer.println(unit.getUnitName());

            ArrayList<Review> reviews = unit.getReviews();
            for (int i = 0; i < reviews.size(); i++) {
                Review r = reviews.get(i);
                writer.println(REVIEW_START);
                writer.println(r.getType().name());
                writer.println(r.getRating());
                writer.println(r.getComment());
                Date d = r.getDate();
                writer.println(d.getDay() + " " + d.getMonth() + " " + d.getYear());

                if (r.getType() == Review.ReviewType.STAFF && r.getPerson() != null) {
                    Person p = r.getPerson();
                    writer.println(p.getName());
                    writer.println(p.getRole().name());
                    writer.println(p.getDepartment());
                } else if (r.getType() == Review.ReviewType.ASSESSMENT && r.getAssessment() != null) {
                    Assessment a = r.getAssessment();
                    writer.println(a.getName());
                    writer.println(a.getType().name());
                    writer.println(a.getDifficulty());
                    writer.println(a.getComment());
                }

                CommentNode chain = unit.getReplyChain(i);
                int replyCount = (chain == null) ? 0 : chain.count();
                writer.println(replyCount);
                CommentNode current = chain;
                while (current != null) {
                    writer.println(current.getReply());
                    current = current.getNext();
                }
                writer.println(REVIEW_END);
            }
            writer.println(UNIT_END);
        }
        writer.close();
        System.out.println("Data saved to " + filename + ".");
    }

    /**
     * Loads review data from a plain-text file into a new ReviewBoard.
     * @param filename path to the file to read
     * @return a ReviewBoard populated with all units and reviews from the file;
     *         returns an empty ReviewBoard if the file does not exist
     * @throws IOException if the file exists but cannot be read
     */
    public static ReviewBoard load(String filename) throws IOException {
        ReviewBoard board = new ReviewBoard();
        File file = new File(filename);
        if (!file.exists()) return board;

        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.equals(UNIT_START)) continue;

            String unitCode = scanner.nextLine().trim();
            String unitName = scanner.nextLine().trim();
            board.addUnit(unitCode, unitName);
            UnitReview unit = board.findUnit(unitCode);

            while (scanner.hasNextLine()) {
                line = scanner.nextLine().trim();
                if (line.equals(UNIT_END)) break;
                if (!line.equals(REVIEW_START)) continue;

                String typeStr     = scanner.nextLine().trim();
                int rating         = Integer.parseInt(scanner.nextLine().trim());
                String comment     = scanner.nextLine().trim();
                String[] dateParts = scanner.nextLine().trim().split(" ");
                Date date = new Date(
                    Integer.parseInt(dateParts[0]),
                    Integer.parseInt(dateParts[1]),
                    Integer.parseInt(dateParts[2])
                );

                Review.ReviewType type = Review.ReviewType.valueOf(typeStr);
                Review review;
                if (type == Review.ReviewType.STAFF) {
                    String personName = scanner.nextLine().trim();
                    Person.Role role  = Person.Role.valueOf(scanner.nextLine().trim());
                    String dept       = scanner.nextLine().trim();
                    review = new Review(rating, comment, date, new Person(personName, role, dept));
                } else if (type == Review.ReviewType.ASSESSMENT) {
                    String aName               = scanner.nextLine().trim();
                    Assessment.AssessmentType aType =
                        Assessment.AssessmentType.valueOf(scanner.nextLine().trim());
                    int difficulty  = Integer.parseInt(scanner.nextLine().trim());
                    String aComment = scanner.nextLine().trim();
                    review = new Review(rating, comment, date,
                        new Assessment(aName, aType, difficulty, aComment));
                } else {
                    review = new Review(rating, comment, date);
                }

                unit.addReview(review);
                int reviewIndex = unit.getReviews().size() - 1;
                int replyCount  = Integer.parseInt(scanner.nextLine().trim());
                for (int i = 0; i < replyCount; i++) {
                    unit.addReply(reviewIndex, scanner.nextLine().trim());
                }
                scanner.nextLine(); // consume REVIEW_END
            }
        }
        scanner.close();
        return board;
    }
}
