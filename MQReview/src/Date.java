/** Stores a calendar date as three integers (day, month, year). */
public class Date {
    private int day;
    private int month;
    private int year;

    /**
     * Creates a Date with the given day, month, and year.
     * @param day   day of the month (1–31)
     * @param month month of the year (1–12)
     * @param year  four-digit year
     */
    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() { return day; }
    public int getMonth() { return month; }
    public int getYear() { return year; }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", day, month, year);
    }
}
