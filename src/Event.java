public class Event {

    // constants
    public static final String SET = "SET", PAIR = "PAIR";
    public static final String ROWCHECK = "ROWCHECK", COLCHECK = "COLCHECK", GRIDCHECK = "GRIDCHECK";
    public static final String GRIDBLOCK = "GRIDBLOCK", RCBLOCK = "RCBLOCK";

    // fields
    private String type;
    private CellInfo ci;
    private CellInfo ci2;
    private int value;
    private int value2;
    private String constraintType;
    private int constraintValue;
    private int constraintValue2;
    private boolean repeatable;

    // constructor
    private Event(String type, boolean repeatable) {
        this.type = type;
        this.repeatable = repeatable;
    }

    public Event(String type) {
        this(type, true);
    }

    public Event(String type, CellInfo ci, int value) {
        this(type, false);
        this.ci = ci;
        this.value = value;
    }

    public Event(String type, CellInfo ci, CellInfo ci2, int value1, int value2, String constraintType) {
        this(type, false);
        this.ci = ci;
        this.ci2 = ci2;
        this.value = value1;
        this.value2 = value2;
        this.constraintType = constraintType;
    }

    public Event(String type, int constraintNumber, String otherConstraint, int otherNumber, int value) {
        this(type, false);
        this.ci = null;
        this.ci2 = null;
        this.constraintType = otherConstraint;
        this.constraintValue = constraintNumber;
        this.constraintValue2 = otherNumber;
        this.value = value;
    }

    // method
    public String getType() { return type; }
    public CellInfo getCell() { return ci; }
    public CellInfo getCell2() { return ci2; }
    public int getValue() { return value; }
    public int getValue2() { return value2; }
    public String getConstraintType() { return constraintType; }
    public int getConstraintValue() { return this.constraintValue; }
    public int getConstraintValue2() { return this.constraintValue2; }

    @Override
    public String toString() {
        switch (this.type) {
            case Event.SET:
                return "SET " + value + " " + ci.getPosition();
            case Event.PAIR:
                return "PAIR " + value + "+" + value2 + " " + ci.getPosition() + " " + ci2.getPosition();
            case Event.GRIDBLOCK:
                return "GRIDBLOCK " + (constraintValue+1) + " removing other " + value + " on " + constraintType + " " + (constraintValue2 + 1);
            case Event.RCBLOCK:
                return constraintType + " " + (constraintValue+1) + " BLOCK GRID " + (constraintValue2 + 1) + " for value " + value;
        }

        return type;
    }

    public boolean isRepeatable() { return this.repeatable; }
}
