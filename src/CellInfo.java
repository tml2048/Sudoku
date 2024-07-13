
public class CellInfo {

    // constants
    public static final int BLANK = 0;
    private static boolean DEBUG = false;

    // fields
    private int value;
    private boolean isLocked; // user locked or if given info
    private boolean isQuestion; // user assigned question mark
    private boolean isGiven;
    private boolean[] possible;
    private boolean hasSetEvent;
    private int row, col, subgrid;

    // constructor
    public CellInfo(int value, int row, int col) {
        this.value = value;
        this.isQuestion = false;
        this.hasSetEvent = false;
        if (value == BLANK) {
            isLocked = false;
            isGiven = false;
        } else {
            isLocked = true;
            isGiven = true;
        }

        this.row = row;
        this.col = col;
        this.subgrid = (row / 3) * 3 + (col / 3);

        this.possible = new boolean[SudokuData.SIZE];
        if (!isGiven) {
            for (int k = 0; k < possible.length; k++) {
                this.possible[k] = true;
            }
        }
    }

    // methods
    public int getValue() { return value; }
    public boolean isLocked() { return isLocked; }
    public boolean isQuestion() { return isQuestion; }
    public boolean isGiven() { return isGiven; }
    public boolean getHasSetEvent() { return this.hasSetEvent; }

    public void setHasSetEvent(boolean value) {
        this.hasSetEvent = value;
    }

    public void setValue(int value) {
        if (isGiven && this.value != value) {
            System.out.println("Cannot change a given cell to different value!");
            return;
        }
        if (isLocked && this.value != value) {
            System.out.println("Cannot change a locked cell to a different value!");
            return;
        }
        this.value = value;
        for (int k = 0; k < this.possible.length; k++) {
            if (k != value - 1) {
                this.possible[k] = false;
            }
        }
    }

    public void removePossible(int value) {
        this.possible[value - 1] = false;
        if (DEBUG) {
            System.out.println("Removing possible " + value + " from cell row " + row + " col " + col);
        }     
    }

    public void addPossible(int value) {
        this.possible[value - 1] = true;
    }

    public boolean checkPossible(int value) {
        return this.possible[value - 1] == true;
    }

    public boolean checkOnlyPossible() {
        if (isGiven) {
            return false;
        }
        int count = 0;
        for (int k = 0; k < this.possible.length; k++) {
            if (this.possible[k]) {
                count++;
            }
        }
        return (count == 1);
    }

    public int getFirstPossible() {
        for (int i = 0; i < possible.length; i++) {
            if (possible[i] == true) {
                return i + 1;
            }
        }
        return -1;
    }

    public int getNthPossible(int nth) {
        int count = 0;
        for (int i = 0; i < possible.length; i++) {
            if (possible[i] == true) {
                count++;
                if (count == nth)
                    return i + 1;
            }
        }
        return -1;
    }

    public void processSetEvent(String eventType, CellInfo ci, int value) {
        if (ci == this)
            return;
        if (ci.row == this.row || ci.col == this.col || ci.subgrid == this.subgrid) {
            // sharing some constraint, remove value from possible
            removePossible(value);
        }
    }

    public void processInfoConstraint(String eventType, CellInfo ci, int value, String constraintType) {
        if (constraintType.equals(Constraint.ROWCONS) && ci.row == this.row)
            removePossible(value);
        if (constraintType.equals(Constraint.COLCONS) && ci.col == this.col)
            removePossible(value);
        if (constraintType.equals(Constraint.SUBCONS) && ci.subgrid == this.subgrid) {
            removePossible(value);
        }
    }

    public void processPairedEvent(String eventType, CellInfo ci1, CellInfo ci2, int value1, int value2, String sharedConstraint) {
        if (this == ci1 || this == ci2)
            return;

        processInfoConstraint("REMOVE", ci1, value1, sharedConstraint);
        processInfoConstraint("REMOVE", ci1, value2, sharedConstraint);
        processInfoConstraint("REMOVE", ci2, value1, sharedConstraint);
        processInfoConstraint("REMOVE", ci2, value2, sharedConstraint);
    }

    public boolean[] getPossible() {
        return possible;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getSubgrid() { return subgrid; }

    public boolean matchedPairPossibilities(CellInfo other) {
        int count = 0;
        for (int k = 0; k < possible.length; k++) {
            if (possible[k] && other.possible[k])
                count++;
            if (possible[k] != other.possible[k])
                return false;
        }
        return (count == 2);
    }

    public String getPosition() {
        return "(r" + (row+1) + ", c" + (col+1) + ")";
    }
}
