/** Represents an assessment (exam, assignment, or quiz) associated with a review. */
public class Assessment {
    public enum AssessmentType { EXAM, ASSIGNMENT, QUIZ }

    private String name;
    private AssessmentType type;
    private int difficulty; // 1–5
    private String comment;

    /**
     * Creates an Assessment with the given details.
     * @param name       name of the assessment (e.g. "Final Exam")
     * @param type       EXAM, ASSIGNMENT, or QUIZ
     * @param difficulty difficulty rating from 1 (easiest) to 5 (hardest)
     * @param comment    assessment-specific feedback (e.g. fairness, clarity)
     * @throws IllegalArgumentException if difficulty is outside the range 1–5
     */
    public Assessment(String name, AssessmentType type, int difficulty, String comment) {
        if (difficulty < 1 || difficulty > 5) {
            throw new IllegalArgumentException("Difficulty must be between 1 and 5, got: " + difficulty);
        }
        this.name = name;
        this.type = type;
        this.difficulty = difficulty;
        this.comment = comment;
    }

    public String getName() { return name; }
    public AssessmentType getType() { return type; }
    public int getDifficulty() { return difficulty; }
    public String getComment() { return comment; }

    @Override
    public String toString() {
        return name + " [" + type + "] Difficulty: " + difficulty + "/5 — " + comment;
    }
}
