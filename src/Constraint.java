public abstract class Constraint {

    // constants
    public static final String ROWCONS = "ROW", COLCONS = "COL", SUBCONS = "GRID";

    // fields
    private CellInfo[] cells;
    private int filled;
    private boolean[] valueMarked;
    private int consNumber;
    private SudokuData data;
    private boolean[] solved;

    // constructors
    public Constraint(SudokuData data, int consNumb) {
        this.data = data;
        this.consNumber = consNumb;
        this.cells = new CellInfo[SudokuData.SIZE];
        this.valueMarked = new boolean[SudokuData.SIZE];
        this.filled = 0;
        this.solved = new boolean[SudokuData.SIZE];
    }

    // methods
    public void addCell(CellInfo ci) {
        assert filled < SudokuData.SIZE;
        cells[filled] = ci;
        if (ci.getValue() != CellInfo.BLANK) {
            valueMarked[ci.getValue() - 1] = true;
        }
        filled++;
    }

    public boolean isSatisfied() {
        for (int k = 0; k < cells.length; k++) {
            if (cells[k].getValue() == CellInfo.BLANK) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Constraint " + this.getTypeString() + " " + consNumber + " done? " + isSatisfied();
    }

    public void detailedPrint() {
        System.out.print(toString());
        System.out.print("\t[ ");
        for (int k = 0; k < valueMarked.length; k++) {
            boolean marked = valueMarked[k];
            if (marked) {
                System.out.print(k + 1 + " ");
            }
            else
                System.out.print("_ ");
        }
        System.out.println("]");
    }

    // processTechniques will apply any advanced techniques to help solve (above trivialish detection of one possible value)
    public void processTechniques() {
        checkOneValueOneCell();
        checkPairedPossibilities();
    }

    public void processEvent(Event e) {
        processTypeSpecificEvent(e);
    }

    public void checkOneValueOneCell() {
        for (int num = 1; num <= SudokuData.SIZE; num++) {
            int count = 0;
            CellInfo ciFound = null;
            for (int k = 0; k < cells.length; k++) {
                CellInfo ci = cells[k];
                if (ci.getHasSetEvent() && ci.getValue() == num)
                    break; // already did this one
                if (ci.checkPossible(num)) {
                    ciFound = ci;
                    count++;
                }
            }
            if (count == 1) {
                this.data.addSetEvent(ciFound, num);
            }
        }
    }

    public abstract void processTypeSpecificEvent(Event e);

    public void checkPairedPossibilities() {
        for (int k = 0; k < cells.length; k++) {
            CellInfo kCell = cells[k];
            if (kCell.getValue() != CellInfo.BLANK) {
                continue;
            }

            for (int p = k + 1; p < cells.length; p++) {
                CellInfo pCell = cells[p];
                if (pCell.getValue() != CellInfo.BLANK) {
                    continue;
                }
                if (kCell.matchedPairPossibilities(pCell)) {
                    data.addPairedEvent(kCell, pCell, this.getTypeString());
                }   
            }
        }
    }

    public abstract boolean isRowConstraint();
    public abstract boolean isColConstraint();
    public abstract boolean isSubgridConstraint();
    public int getNumber() { return this.consNumber; }
    public void setSolved(int value) {
        this.solved[value - 1] = true;
    }
    public abstract String getTypeString();
    public CellInfo[] getCells() { return this.cells; }
    public SudokuData getData() { return this.data; }
}
