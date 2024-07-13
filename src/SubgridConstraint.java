public class SubgridConstraint extends Constraint {

    public SubgridConstraint(SudokuData data, int number) {
        super(data, number);
    }

    @Override
    public void processTechniques() {
        super.processTechniques();

        // if a value is only found within a row, remove that value from possibles in *other* rowmates
        // similarly, if a value is only found within a col, remove that value from possibles in *other* columnmates
        // calculation is done by making a product of row/col primes
        // if product is all of one prime, then number occured in one row/col
        
        CellInfo[] cells = this.getCells();
        int[] primes = { 2, 3, 5 };
        for (int num = 1; num <= SudokuData.SIZE; num++) { 
            int rowProduct = 1; // product of row primes
            int colProduct = 1; // product of col primes
            
            CellInfo ciFound = null;

            for (int k = 0; k < SudokuData.SIZE; k++) {
                CellInfo ci = cells[k];
                if (ci.checkPossible(num)) {
                    rowProduct *= primes[k/3];
                    colProduct *= primes[k%3];
                    ciFound = ci;
                }
            }

            if (rowProduct == 4 || rowProduct == 8 || rowProduct == 9 || rowProduct == 27 || rowProduct == 25 || rowProduct == 125) {
                this.getData().addGridBlockOtherEvent(this.getNumber(), ROWCONS, ciFound.getRow(), num);
            }

            if (colProduct == 4 || colProduct == 8 || colProduct == 9 || colProduct == 27 || colProduct == 25 || colProduct == 125) {
                this.getData().addGridBlockOtherEvent(this.getNumber(), COLCONS, ciFound.getCol(), num);
            }
        }
    }

    @Override
    public boolean isRowConstraint() { return false; }

    @Override
    public boolean isColConstraint() { return false; }

    @Override
    public boolean isSubgridConstraint() { return true; }

    @Override
    public void processTypeSpecificEvent(Event e) {
        if (e.getType().equals(Event.SET)) {
            if (e.getCell().getSubgrid() == this.getNumber())
                this.setSolved(e.getCell().getValue());

        }
        if (e.getType().equals(Event.RCBLOCK) && e.getConstraintValue2() == this.getNumber()) {
            CellInfo[] cells = this.getCells();
            for (int k = 0; k < cells.length; k++) {
                CellInfo ci = cells[k];
                if (e.getConstraintType().equals(ROWCONS) && ci.getRow() != e.getConstraintValue()) {
                    ci.removePossible(e.getValue());
                }
                else if (e.getConstraintType().equals(COLCONS) && ci.getCol() != e.getConstraintValue()) {
                    ci.removePossible(e.getValue());
                }
            }
        }
        
        if (e.getType() == Event.GRIDCHECK) {
            this.processTechniques();
        }
    }
    
    @Override
    public String getTypeString() { return Constraint.SUBCONS; }

}
