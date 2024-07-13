public class RowConstraint extends Constraint {

    public RowConstraint(SudokuData data, int consNumb) {
        super(data, consNumb);
    }

    @Override
    public void processTechniques() {
        super.processTechniques();

        // if a value within this row only occurs within a subgrid,
        // remove that value from all other spots in that subgrid

        CellInfo[] cells = this.getCells();
        int[] primes = { 2, 3, 5 };
        for (int num = 1; num <= SudokuData.SIZE; num++) {
            int subgridProduct = 1;
            CellInfo ciFound = null;
            for (int k = 0; k < SudokuData.SIZE; k++) {
                CellInfo ci = cells[k];
                if (ci.checkPossible(num)) {
                    subgridProduct *= primes[k/3];
                    ciFound = ci;
                }
            }

            if (subgridProduct == 4 || subgridProduct == 8 || subgridProduct == 9 || subgridProduct == 27 || subgridProduct == 25 || subgridProduct == 125) {
                this.getData().addRCBlockGridEvent(this.getNumber(), ROWCONS, ciFound.getSubgrid(), num);
            }
        }
    }

    @Override
    public boolean isRowConstraint() { return true; }

    @Override
    public boolean isColConstraint() { return false; }

    @Override
    public boolean isSubgridConstraint() { return false; }

    @Override
    public void processTypeSpecificEvent(Event e) {
        if (e.getType().equals(Event.SET)) {
            if (e.getCell().getRow() == this.getNumber())
                this.setSolved(e.getCell().getValue());

        }
        if (e.getType().equals(Event.GRIDBLOCK) && e.getConstraintType().equals(ROWCONS) && e.getConstraintValue2() == this.getNumber()) {
            CellInfo[] cells = this.getCells();
            for (int k = 0; k < cells.length; k++) {
                CellInfo ci = cells[k];
                if (ci.getSubgrid() != e.getConstraintValue()) {
                    ci.removePossible(e.getValue());
                }
            }
        }
        if (e.getType() == Event.ROWCHECK) {
            this.processTechniques();
        }
    }

    @Override
    public String getTypeString() { return Constraint.ROWCONS; }
}
