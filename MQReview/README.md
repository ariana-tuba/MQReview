# MQReview — Rate My Professor but for Macquarie University

## What problem does MQReview solve?

Macquarie students currently have no centralised, structured way to share honest feedback about
units, lecturers, tutors, or individual assessments. MQReview fills that gap with a text-based
system that lets students submit and remove reviews, reply to each other's comments, search and
filter by staff name or assessment, and see average ratings — all persisted across sessions via a
plain text file.

---

## Program Structure

| Class | Responsibility |
|---|---|
| `Date` | Stores a calendar date as three integers (day, month, year). Avoids using String for structured data. |
| `Person` | Stores a staff member's name, role (`LECTURER` / `TUTOR`), and department. |
| `Assessment` | Stores an assessment's name, type (`EXAM` / `ASSIGNMENT` / `QUIZ`), difficulty (1–5), and a comment. |
| `CommentNode` | A node in a singly-linked list. Holds one reply string and a pointer to the next node. `append()` and `count()` are recursive, satisfying the recursive data structure requirement. |
| `Review` | Holds a rating (1–5), comment, `Date` object, and either a `Person` or an `Assessment` depending on the review type. Satisfies the class-containing-another-user-defined-class requirement. |
| `UnitReview` | Aggregates all `Review` objects for one unit, plus a parallel `ArrayList<CommentNode>` for per-review reply chains. Calculates averages per category. |
| `ReviewBoard` | The central data manager. Holds all `UnitReview` objects. Provides add, remove, search, filter, and average methods across all units. |
| `FileHandler` | Saves and loads the entire data set to/from `reviews.txt` using `PrintWriter` and `Scanner`. No external libraries. |
| `Driver` | Application entry point. Creates a `ReviewBoardUI` and starts the menu loop. |
| `ReviewBoardUI` | Main menu controller. Owns the `Scanner` and `ReviewBoard`, loads and saves data, and delegates user actions to `ReviewEditor` and `ReviewViewer`. |
| `ReviewEditor` | Client class for all write operations: adding and removing units and reviews, and posting replies. |
| `ReviewViewer` | Client class for all read operations: displaying reviews, filtering by rating, and searching by staff or assessment. |
| `ReviewTest` | JUnit test suite with 24 tests covering boundary values, null inputs, empty lists, duplicates, the linked-list reply chain, and review removal. |

---

## How to run the application

### Prerequisites

- Java 11 or later (check with `java -version`)
- The JUnit JAR is already included in the `lib/` folder (`junit-platform-console-standalone-1.7.2.jar`)

### Compile (run from the `MQReview/` directory)

**Windows:**

- Compile source files: `javac -d out src\*.java`
- Compile tests: `javac -cp "lib\junit-platform-console-standalone-1.7.2.jar;out" -d out test\ReviewTest.java`

**macOS / Linux:**

- Compile source files: `javac -d out src/*.java`
- Compile tests: `javac -cp "lib/junit-platform-console-standalone-1.7.2.jar:out" -d out test/ReviewTest.java`

### Run the application

- Windows: `java -cp out Driver`
- macOS / Linux: `java -cp out Driver`
- Data is saved to `reviews.txt` automatically on exit and reloaded on the next run

### Run the JUnit tests

- Windows: `java -cp "lib\junit-platform-console-standalone-1.7.2.jar;out" org.junit.runner.JUnitCore ReviewTest`
- macOS / Linux: `java -cp "lib/junit-platform-console-standalone-1.7.2.jar:out" org.junit.runner.JUnitCore ReviewTest`

---

## Notes

- Review comments and replies must not contain the exact strings `UNIT_START`, `UNIT_END`,
  `REVIEW_START`, or `REVIEW_END`, as these are used as file format sentinels.
- Ratings and assessment difficulty are validated at construction time; invalid values throw
  `IllegalArgumentException`.
