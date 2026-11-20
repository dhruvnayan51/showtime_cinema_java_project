// ShowTime - single-file version (no packages). Save as Main.java and run.
// The packaged version (com.showtime.*) is in the ShowTime folder.

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

// ===== SeatClassNotFoundException.java (exceptions package in the packaged version) =====
/**
 * UNIT V - Custom CHECKED exception.
 * Thrown when the clerk enters a seat class id that is not in the seat chart.
 * It extends Exception (not RuntimeException), so the compiler forces every
 * caller to either catch it or declare it with "throws".
 */
class SeatClassNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int seatClassId;

    public SeatClassNotFoundException(int seatClassId) {
        super("Seat class id " + seatClassId + " does not exist. Please choose an id from the seat chart.");
        this.seatClassId = seatClassId;
    }

    public int getSeatClassId() {
        return seatClassId;
    }
}

// ===== SeatsNotAvailableException.java (exceptions package in the packaged version) =====
/**
 * UNIT V - Custom CHECKED exception.
 * Thrown when a booking asks for more seats than are still available.
 */
class SeatsNotAvailableException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int requested;
    private final int available;

    public SeatsNotAvailableException(String seatClassName, int requested, int available) {
        super("Not enough seats in " + seatClassName + ": requested " + requested
                + ", available " + available + ".");
        this.requested = requested;
        this.available = available;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}

// ===== Billable.java (interfaces package in the packaged version) =====
/**
 * UNIT IV - Interface.
 * Anything that can be billed must be able to report these four amounts.
 * An interface only says WHAT must be provided, never HOW (no fields, no logic).
 * Booking implements this interface.
 */
interface Billable {

    /** Sum of all seat fares before discount. */
    double getSubtotal();

    /** Money taken off because of the viewer's discount rate. */
    double getDiscountAmount();

    /** 18% GST charged on the amount after discount. */
    double getGstAmount();

    /** Amount after discount plus GST - what the viewer actually pays. */
    double getFinalTotal();
}

// ===== Person.java (model package in the packaged version) =====
/**
 * UNIT IV - Top of the inheritance chain:  Person -> Viewer -> Adult / SeniorCitizen.
 * A Person only knows a name. Everything else is added by the subclasses.
 */
class Person {

    private final String name;

    public Person(String name) {
        this.name = name; // "this" separates the field from the parameter with the same name
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Person: " + name;
    }
}

// ===== Viewer.java (model package in the packaged version) =====
/**
 * UNIT IV - Abstract class.
 * A Viewer is a Person who buys tickets. We never create a plain Viewer,
 * only an Adult or a SeniorCitizen, so the class is abstract.
 *
 * - getDiscountRate() and getViewerType() are abstract: each child MUST override them.
 * - calculateDiscount() is FINAL: no child may change how a discount is computed
 *   (it always = subtotal x discount rate).
 */
abstract class Viewer extends Person {

    protected Viewer(String name) {
        super(name); // super(...) runs the Person constructor first
    }

    /** Discount as a fraction, e.g. 0.20 means 20%. Each child decides its own value. */
    public abstract double getDiscountRate();

    /** Text shown on the bill, e.g. "Adult". */
    public abstract String getViewerType();

    /** Final method - cannot be overridden. */
    public final double calculateDiscount(double subtotal) {
        return subtotal * getDiscountRate();
    }

    @Override
    public String toString() {
        return getName() + " (" + getViewerType() + ")";
    }
}

// ===== Adult.java (model package in the packaged version) =====
/** UNIT IV - Concrete child of Viewer. Adults get 0% discount. */
class Adult extends Viewer {

    public Adult(String name) {
        super(name);
    }

    @Override
    public double getDiscountRate() {
        return 0.0;
    }

    @Override
    public String getViewerType() {
        return "Adult";
    }
}

// ===== SeniorCitizen.java (model package in the packaged version) =====
/** UNIT IV - Concrete child of Viewer. Senior Citizens get 20% discount. */
class SeniorCitizen extends Viewer {

    public SeniorCitizen(String name) {
        super(name);
    }

    @Override
    public double getDiscountRate() {
        return 0.20;
    }

    @Override
    public String getViewerType() {
        return "Senior Citizen";
    }
}

// ===== SeatClass.java (model package in the packaged version) =====
/**
 * UNIT I / III - One row of the seat chart (e.g. 101 Front Stall).
 * The chart itself is a SeatClass[] array created in Main.
 */
class SeatClass {

    private final int id;
    private final String name;
    private final String category;
    private final double fare;
    private int seatsAvailable;

    public SeatClass(int id, String name, String category, double fare, int seatsAvailable) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.fare = fare;
        this.seatsAvailable = seatsAvailable;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getFare() {
        return fare;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    /** UNIT II - ternary operator: LOW when fewer than 10 seats remain, otherwise OK. */
    public String getStatus() {
        return seatsAvailable < 10 ? "LOW" : "OK";
    }

    /**
     * UNIT V - throw / throws.
     * Only CHECKS the request, it does not change anything.
     * totalRequested = seats of this class the booking wants in total.
     */
    public void checkAvailable(int totalRequested) throws SeatsNotAvailableException {
        if (totalRequested > seatsAvailable) {
            throw new SeatsNotAvailableException(name, totalRequested, seatsAvailable);
        }
    }

    /** Removes seats. Called only after the whole booking has been validated. */
    public void deduct(int quantity) {
        seatsAvailable = seatsAvailable - quantity;
    }
}

// ===== LineItem.java (model package in the packaged version) =====
/**
 * UNIT III - One line of a booking: a seat class and how many seats of it.
 * Objects of this class are what we push onto the ItemStack.
 * It is immutable (fields are final), so a stored bill line can never change.
 */
class LineItem {

    private final SeatClass seatClass;
    private final int quantity;

    public LineItem(SeatClass seatClass, int quantity) {
        this.seatClass = seatClass;
        this.quantity = quantity;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public int getQuantity() {
        return quantity;
    }

    /** Fare x quantity. */
    public double getAmount() {
        return seatClass.getFare() * quantity;
    }

    @Override
    public String toString() {
        return quantity + " x " + seatClass.getName();
    }
}

// ===== ItemStack.java (stack package in the packaged version) =====
/**
 * UNIT III - Our OWN stack (no java.util.Stack, no ArrayList).
 * LIFO = Last In, First Out: the last seat class added is the first one removed,
 * which is exactly what "undo" (-1) needs.
 *
 * It is built on a plain array plus an int "top" that counts how many items are stored.
 *   push  -> put item at data[top], then top++
 *   pop   -> top--, return data[top]
 */
class ItemStack {

    private final LineItem[] data;
    private int top;

    public ItemStack(int capacity) {
        this.data = new LineItem[capacity];
        this.top = 0;
    }

    public boolean isEmpty() {
        return top == 0;
    }

    public boolean isFull() {
        return top == data.length;
    }

    public int size() {
        return top;
    }

    /** Returns false (and does nothing) if the stack is full. */
    public boolean push(LineItem item) {
        if (isFull()) {
            return false;
        }
        data[top] = item;
        top++;
        return true;
    }

    /** Removes and returns the most recent item, or null if the stack is empty. */
    public LineItem pop() {
        if (isEmpty()) {
            return null;
        }
        top--;
        LineItem removed = data[top];
        data[top] = null;
        return removed;
    }

    /** Looks at the most recent item without removing it. */
    public LineItem peek() {
        return isEmpty() ? null : data[top - 1];
    }

    /** Total quantity already selected for one seat class id (used for validation). */
    public int quantityFor(int seatClassId) {
        int total = 0;
        for (int i = 0; i < top; i++) {
            if (data[i].getSeatClass().getId() == seatClassId) {
                total = total + data[i].getQuantity();
            }
        }
        return total;
    }

    /** Copy of the stored items, oldest first, in an array of exactly the right size. */
    public LineItem[] toArray() {
        LineItem[] copy = new LineItem[top];
        for (int i = 0; i < top; i++) {
            copy[i] = data[i];
        }
        return copy;
    }
}

// ===== BillingCalculator.java (billing package in the packaged version) =====
/**
 * UNIT I / III - All the money maths lives here.
 *  - GST is a constant (final).
 *  - subtotal(...) is OVERLOADED: one version takes just the array,
 *    the other also takes how many items to add up.
 *  - The second version is RECURSIVE (it calls itself).
 */
class BillingCalculator {

    /** GST rate - 18%. Declared final so it can never be changed. */
    public static final double GST = 0.18;

    /** Overload 1: adds up every item in the array. */
    public static double subtotal(LineItem[] items) {
        return subtotal(items, items.length);
    }

    /**
     * Overload 2 - RECURSIVE.
     * subtotal of first n items = amount of the n-th item + subtotal of the first (n-1) items.
     * Base case: n == 0 -> nothing to add -> 0.
     */
    public static double subtotal(LineItem[] items, int n) {
        if (n == 0) {
            return 0.0;
        }
        return items[n - 1].getAmount() + subtotal(items, n - 1);
    }

    /** Discount = subtotal x viewer's rate (Adult 0%, Senior Citizen 20%). */
    public static double discount(double subtotal, Viewer viewer) {
        return round2(viewer.calculateDiscount(subtotal));
    }

    /** GST is charged on the amount AFTER discount. */
    public static double gst(double amountAfterDiscount) {
        return round2(amountAfterDiscount * GST);
    }

    /** Rounds to 2 decimal places so printed bills always add up exactly. */
    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

// ===== Booking.java (billing package in the packaged version) =====
/**
 * UNIT III / IV - One confirmed booking (one bill).
 * - static counter  -> gives BKG1, BKG2, BKG3 ... (shared by ALL Booking objects)
 * - this(...)       -> constructor chaining
 * - implements Billable -> promises the four money getters
 * - token & 1       -> odd/even gate rule
 *
 * A Booking object is created ONLY after a booking has passed every check,
 * so the counter only counts successful bookings.
 */
class Booking implements Billable {

    /** Static = one copy for the whole class, not one per object. */
    private static int counter = 0;

    private final String bookingId;
    private final Viewer viewer;
    private final LineItem[] items;
    private final int token;
    private final double subtotal;
    private final double discountAmount;
    private final double gstAmount;
    private final double finalTotal;

    /** Main constructor. */
    public Booking(Viewer viewer, LineItem[] items, int itemCount, int token) {
        counter++;
        this.bookingId = "BKG" + counter;
        this.viewer = viewer;
        this.token = token;

        // keep only the first itemCount items in a correctly sized private array
        this.items = new LineItem[itemCount];
        for (int i = 0; i < itemCount; i++) {
            this.items[i] = items[i];
        }

        // Subtotal -> minus discount -> plus GST on the discounted amount
        this.subtotal = BillingCalculator.round2(BillingCalculator.subtotal(this.items));
        this.discountAmount = BillingCalculator.discount(this.subtotal, viewer);
        double afterDiscount = this.subtotal - this.discountAmount;
        this.gstAmount = BillingCalculator.gst(afterDiscount);
        this.finalTotal = BillingCalculator.round2(afterDiscount + this.gstAmount);
    }

    /** Shorter constructor: uses every item in the array. Chains to the main one with this(...). */
    public Booking(Viewer viewer, LineItem[] items, int token) {
        this(viewer, items, items.length, token);
    }

    public static int getCounter() {
        return counter;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Viewer getViewer() {
        return viewer;
    }

    public int getToken() {
        return token;
    }

    /** UNIT II - bitwise AND: (token & 1) is 1 for odd tokens, 0 for even tokens. */
    public int getGate() {
        return (token & 1) == 1 ? 1 : 2;
    }

    public int getItemCount() {
        return items.length;
    }

    public LineItem getItem(int index) {
        return items[index];
    }

    public double getAmountAfterDiscount() {
        return subtotal - discountAmount;
    }

    @Override
    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public double getDiscountAmount() {
        return discountAmount;
    }

    @Override
    public double getGstAmount() {
        return gstAmount;
    }

    @Override
    public double getFinalTotal() {
        return finalTotal;
    }

    @Override
    public String toString() {
        return bookingId + " | " + viewer + " | Token " + token + " | Rs. "
                + String.format("%.2f", finalTotal);
    }
}

// ===== BillPrinter.java (billing package in the packaged version) =====
/** Prints a Booking as a formatted bill on the console. */
class BillPrinter {

    private static final String LINE = "========================================";
    private static final String DASH = "----------------------------------------";

    public static void print(Booking b) {
        // cast: the rate is a double like 0.20; (int)(0.20 * 100) shows it as 20
        int discountPercent = (int) Math.round(b.getViewer().getDiscountRate() * 100);
        int gstPercent = (int) Math.round(BillingCalculator.GST * 100);

        System.out.println();
        System.out.println(LINE);
        System.out.println("            SHOWTIME BILL");
        System.out.println(LINE);
        System.out.println("Booking ID  : " + b.getBookingId());
        System.out.println("Viewer      : " + b.getViewer().getName());
        System.out.println("Viewer type : " + b.getViewer().getViewerType());
        System.out.println("Token no.   : " + b.getToken());
        System.out.println("Gate        : Gate " + b.getGate());
        System.out.println(DASH);
        System.out.printf("%-16s %4s %9s %10s%n", "Seat Class", "Qty", "Fare", "Amount");
        for (int i = 0; i < b.getItemCount(); i++) {
            LineItem item = b.getItem(i);
            System.out.printf("%-16s %4d %9.2f %10.2f%n",
                    item.getSeatClass().getName(), item.getQuantity(),
                    item.getSeatClass().getFare(), item.getAmount());
        }
        System.out.println(DASH);
        System.out.printf("%-26s Rs. %9.2f%n", "Subtotal", b.getSubtotal());
        System.out.printf("%-26s Rs. %9.2f%n", "Discount (" + discountPercent + "%)", b.getDiscountAmount());
        System.out.printf("%-26s Rs. %9.2f%n", "Amount after discount", b.getAmountAfterDiscount());
        System.out.printf("%-26s Rs. %9.2f%n", "GST (" + gstPercent + "%)", b.getGstAmount());
        System.out.println(DASH);
        System.out.printf("%-26s Rs. %9.2f%n", "FINAL TOTAL", b.getFinalTotal());
        System.out.println(LINE);
    }
}

// ===== Main.java (main package in the packaged version) =====
/**
 * ShowTime - Cinema Ticket Booking System (console, menu driven).
 *
 * All project data is kept in plain arrays:
 *   seatClasses  -> SeatClass[]   (the seat chart)
 *   bookings     -> Booking[]     (every successful booking of the day)
 */
 class Main {

    private static final int MAX_BOOKINGS = 100;   // size of the bookings array
    private static final int MAX_LINES = 20;       // max seat-class lines in one booking

    private static SeatClass[] seatClasses = new SeatClass[6];
    private static Booking[] bookings = new Booking[MAX_BOOKINGS];
    private static int bookingCount = 0;           // how many slots of bookings[] are used
    private static int tokenCounter = 0;           // last token number handed out

    // ------------------------------------------------------------------
    // UNIT II - main menu: do-while loop + switch
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);   // always print 873.20, never 873,20
        initSeatChart();

        // UNIT V - try-with-resources: the Scanner is closed automatically
        try (Scanner in = new Scanner(System.in)) {
            int choice;
            do {
                printMenu();
                try {
                    choice = readInt(in, "Enter your choice (1-5): ");
                } catch (NumberFormatException e) {
                    choice = 0;   // letters typed -> treated as an invalid choice
                }

                switch (choice) {
                    case 1:
                        viewSeatChart();
                        break;
                    case 2:
                        newBooking(in);
                        break;
                    case 3:
                        viewBill(in);
                        break;
                    case 4:
                        bookingReport();
                        break;
                    case 5:
                        System.out.println("\nThank you for using ShowTime. Goodbye!");
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please enter a number from 1 to 5.");
                }
            } while (choice != 5);
        } catch (NoSuchElementException e) {
            // input stream ended (e.g. Ctrl+D or a finished test file)
            System.out.println("\nNo more input. Exiting ShowTime.");
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("          SHOWTIME CINEMA");
        System.out.println("========================================");
        System.out.println("1. View Seat Chart");
        System.out.println("2. New Booking");
        System.out.println("3. View Bill");
        System.out.println("4. Booking Report");
        System.out.println("5. Exit");
        System.out.println("========================================");
    }

    // ------------------------------------------------------------------
    // UNIT I - seat chart stored in a SeatClass[] array
    // ------------------------------------------------------------------
    private static void initSeatChart() {
        // fares are written as ints (120) but the constructor wants double -> automatic widening
        seatClasses[0] = new SeatClass(101, "Front Stall", "Stall", 120, 40);
        seatClasses[1] = new SeatClass(102, "Middle Stall", "Stall", 160, 40);
        seatClasses[2] = new SeatClass(103, "Balcony", "Balcony", 220, 30);
        seatClasses[3] = new SeatClass(104, "Gold Recliner", "Premium", 350, 10);
        seatClasses[4] = new SeatClass(105, "Couple Sofa", "Premium", 500, 6);
        seatClasses[5] = new SeatClass(106, "Wheelchair Bay", "Special", 120, 4);
    }

    private static void viewSeatChart() {
        System.out.println();
        System.out.println("==================== SEAT CHART ====================");
        System.out.printf("%-5s %-16s %-10s %10s %6s  %s%n",
                "ID", "Seat Class", "Category", "Fare (Rs.)", "Seats", "Status");
        System.out.println("----------------------------------------------------");
        for (int i = 0; i < seatClasses.length; i++) {
            SeatClass s = seatClasses[i];
            System.out.printf("%-5d %-16s %-10s %10.2f %6d  %s%n",
                    s.getId(), s.getName(), s.getCategory(), s.getFare(),
                    s.getSeatsAvailable(), s.getStatus());
        }
        System.out.println("====================================================");
    }

    // ------------------------------------------------------------------
    // Input helpers
    // ------------------------------------------------------------------

    /** Reads one line and converts it to int. Throws NumberFormatException for letters etc. */
    private static int readInt(Scanner in, String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(in.nextLine().trim());
    }

    /** Keeps asking until the clerk types a non-empty name. */
    private static String readName(Scanner in) {
        while (true) {
            System.out.print("Viewer name: ");
            String name = in.nextLine().trim();
            if (!name.isEmpty()) {
                return name;
            }
            System.out.println("Name cannot be empty. Please try again.");
        }
    }

    /** Keeps asking until the clerk types a valid viewer type. Uses a char + switch. */
    private static Viewer readViewer(Scanner in, String name) {
        while (true) {
            System.out.print("Viewer type (A = Adult, S = Senior Citizen): ");
            String text = in.nextLine().trim().toLowerCase();

            char type = '?';   // '?' means "not recognised"
            if (text.equals("a") || text.equals("adult")) {
                type = 'A';
            } else if (text.equals("s") || text.equals("senior") || text.equals("senior citizen")) {
                type = 'S';
            }

            switch (type) {
                case 'A':
                    return new Adult(name);
                case 'S':
                    return new SeniorCitizen(name);
                default:
                    System.out.println("Invalid type. Type A for Adult or S for Senior Citizen.");
            }
        }
    }

    // ------------------------------------------------------------------
    // UNIT V - throw / throws: a lookup that can fail
    // ------------------------------------------------------------------
    private static SeatClass findSeatClass(int id) throws SeatClassNotFoundException {
        for (int i = 0; i < seatClasses.length; i++) {
            if (seatClasses[i].getId() == id) {
                return seatClasses[i];
            }
        }
        throw new SeatClassNotFoundException(id);
    }

    // ------------------------------------------------------------------
    // New booking
    // ------------------------------------------------------------------
    private static void newBooking(Scanner in) {
        if (bookingCount >= bookings.length) {
            System.out.println("\nThe booking register is full. No more bookings can be taken today.");
            return;
        }

        System.out.println("\n--------------- NEW BOOKING ---------------");
        String name = readName(in);
        Viewer viewer = readViewer(in, name);

        // Temporary selection. Nothing is taken from the seat chart until the booking is confirmed.
        ItemStack selection = new ItemStack(MAX_LINES);

        System.out.println("\nEnter seat class id and quantity.  -1 = undo last seat class,  0 = finish.");
        viewSeatChart();

        try {                                          // OUTER try: the whole booking session
            boolean finished = false;
            while (!finished) {
                try {                                  // INNER try: one entry (nested try)
                    int id = readInt(in, "\nSeat class id (-1 undo, 0 finish): ");

                    if (id == -1) {
                        if (selection.isEmpty()) {
                            System.out.println("Nothing to undo.");
                        } else {
                            LineItem removed = selection.pop();
                            System.out.println("Removed: " + removed);
                            showRunningTotal(selection);
                        }
                    } else if (id == 0) {
                        finished = true;
                    } else {
                        SeatClass seat = findSeatClass(id);          // may throw SeatClassNotFoundException
                        int quantity = readInt(in, "Quantity for " + seat.getName() + ": ");

                        if (quantity <= 0) {
                            System.out.println("Quantity must be at least 1.");
                        } else {
                            // Seats already chosen for this same class count too.
                            seat.checkAvailable(quantity + selection.quantityFor(id)); // may throw SeatsNotAvailableException
                            if (selection.push(new LineItem(seat, quantity))) {
                                System.out.println("Added: " + quantity + " x " + seat.getName());
                                showRunningTotal(selection);
                            } else {
                                System.out.println("A booking can have at most " + MAX_LINES
                                        + " seat class lines. Enter 0 to finish.");
                            }
                        }
                    }
                } catch (SeatClassNotFoundException e) {             // multiple catch blocks
                    System.out.println("ERROR: " + e.getMessage());
                } catch (SeatsNotAvailableException e) {
                    System.out.println("ERROR: " + e.getMessage() + " This entry was not added.");
                } catch (NumberFormatException e) {
                    System.out.println("ERROR: Please enter whole numbers only (no letters or decimals).");
                }
            }

            completeBooking(viewer, selection);        // may throw SeatsNotAvailableException

        } catch (SeatsNotAvailableException e) {
            System.out.println("\nBOOKING REJECTED: " + e.getMessage());
            System.out.println("No seats were changed.");
        } finally {
            // finally always runs - whether the booking succeeded, failed or was cancelled
            System.out.println("\n[Booking session closed - returning to main menu]");
        }
    }

    /** Shows the subtotal of the current selection using the recursive subtotal method. */
    private static void showRunningTotal(ItemStack selection) {
        double running = BillingCalculator.subtotal(selection.toArray());
        System.out.printf("Selected so far: %d line(s), subtotal Rs. %.2f%n", selection.size(), running);
    }

    /**
     * PHASE 1: validate the whole booking (nothing is changed yet).
     * PHASE 2: deduct seats, create the Booking object and print the bill.
     */
    private static void completeBooking(Viewer viewer, ItemStack selection) throws SeatsNotAvailableException {
        LineItem[] items = selection.toArray();

        if (items.length == 0) {
            System.out.println("\nNo seats were selected. Booking cancelled.");
            return;
        }

        // PHASE 1 - validate every seat class again, with its TOTAL quantity
        for (int i = 0; i < items.length; i++) {
            SeatClass seat = items[i].getSeatClass();
            seat.checkAvailable(selection.quantityFor(seat.getId()));
        }

        // PHASE 2 - everything is valid, so now (and only now) deduct the seats
        for (int i = 0; i < items.length; i++) {
            items[i].getSeatClass().deduct(items[i].getQuantity());
        }

        tokenCounter++;
        Booking booking = new Booking(viewer, items, tokenCounter);
        bookings[bookingCount] = booking;
        bookingCount++;

        System.out.println("\nBooking confirmed!");
        BillPrinter.print(booking);
    }

    // ------------------------------------------------------------------
    // View an old bill
    // ------------------------------------------------------------------
    private static void viewBill(Scanner in) {
        if (bookingCount == 0) {
            System.out.println("\nNo bookings have been made yet.");
            return;
        }

        System.out.print("\nEnter booking id (e.g. BKG1): ");
        String text = in.nextLine().trim();
        if (text.matches("\\d+")) {          // allow just "1" for "BKG1"
            text = "BKG" + text;
        }

        Booking found = null;
        for (int i = 0; i < bookingCount; i++) {
            if (bookings[i].getBookingId().equalsIgnoreCase(text)) {
                found = bookings[i];
            }
        }

        if (found == null) {
            System.out.println("No booking found with id \"" + text + "\".");
        } else {
            BillPrinter.print(found);
        }
    }

    // ------------------------------------------------------------------
    // End-of-day report
    // ------------------------------------------------------------------
    private static void bookingReport() {
        int[] ticketsSold = new int[seatClasses.length];   // parallel to seatClasses[]
        int totalTickets = 0;
        double totalRevenue = 0.0;

        for (int b = 0; b < bookingCount; b++) {
            totalRevenue = totalRevenue + bookings[b].getFinalTotal();
            for (int k = 0; k < bookings[b].getItemCount(); k++) {
                LineItem item = bookings[b].getItem(k);
                for (int s = 0; s < seatClasses.length; s++) {
                    if (seatClasses[s].getId() == item.getSeatClass().getId()) {
                        ticketsSold[s] = ticketsSold[s] + item.getQuantity();
                        totalTickets = totalTickets + item.getQuantity();
                    }
                }
            }
        }

        // most popular = highest number of tickets (ties are all listed)
        int max = 0;
        for (int s = 0; s < ticketsSold.length; s++) {
            if (ticketsSold[s] > max) {
                max = ticketsSold[s];
            }
        }
        String popular = "";
        for (int s = 0; s < ticketsSold.length; s++) {
            if (max > 0 && ticketsSold[s] == max) {
                popular = popular.isEmpty() ? seatClasses[s].getName() : popular + ", " + seatClasses[s].getName();
            }
        }
        if (max == 0) {
            popular = "N/A (no tickets sold yet)";
        }

        System.out.println();
        System.out.println("============= END-OF-DAY REPORT =============");
        System.out.println("Total bookings        : " + bookingCount);
        System.out.println("Total tickets sold    : " + totalTickets);
        System.out.printf("Total revenue         : Rs. %.2f (including GST)%n", totalRevenue);
        if (bookingCount > 0) {
            // type casting: (double) stops integer division from dropping the decimals
            double average = (double) totalTickets / bookingCount;
            System.out.printf("Avg tickets / booking : %.2f%n", average);
        }
        System.out.println("Most popular class    : " + popular);
        System.out.println("---------------------------------------------");
        System.out.println("Tickets sold per seat class:");
        for (int s = 0; s < seatClasses.length; s++) {
            System.out.printf("  %-5d %-16s %4d%n",
                    seatClasses[s].getId(), seatClasses[s].getName(), ticketsSold[s]);
        }
        System.out.println("=============================================");
    }
}