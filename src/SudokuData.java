import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class SudokuData {

    // constants
    public static final int SIZE = 9;

    // fields
    private CellInfo[][] grid;
    private Constraint[] rowConstraints;
    private Constraint[] colConstraints;
    private Constraint[] subgridConstraints;
    private Queue<Event> queue;
    private HashSet<String> eventsDone;
    private int stepCount;
    private boolean isFinished;

    // constructors
    public SudokuData() {
        this.grid = new CellInfo[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                this.grid[row][col] = new CellInfo(1, row, col);
            }
        }
        initRest();
    }

    public SudokuData(String filename) throws IOException {
        this.grid = new CellInfo[SIZE][SIZE];
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int row = 0;
            while (true) {
                String line = br.readLine();
                if (line == null) 
                    break;

                Scanner scan = new Scanner(line);
                for (int col = 0; col < SIZE; col++) {
                    int value = scan.nextInt();
                    this.grid[row][col] = new CellInfo(value, row, col);
                }
                row++;
                scan.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find the input file " + filename + "!");
            e.printStackTrace();
        }
        initRest();
    }

    private void initRest() {
        this.rowConstraints = new Constraint[SIZE];
        this.colConstraints = new Constraint[SIZE];
        this.subgridConstraints = new Constraint[SIZE];
        this.eventsDone = new HashSet<String>();
        this.queue = new LinkedList<Event>();
        this.stepCount = 0;
        this.isFinished = false;

        for (int k = 0; k < SIZE; k++) {
            this.rowConstraints[k] = new RowConstraint(this, k);
            this.colConstraints[k] = new ColConstraint(this, k);
            this.subgridConstraints[k] = new SubgridConstraint(this, k);
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                CellInfo ci = this.grid[row][col];
                int sub = (row / 3) * 3 + (col / 3);
                this.rowConstraints[row].addCell(ci);
                this.colConstraints[col].addCell(ci);
                this.subgridConstraints[sub].addCell(ci);
                if (ci.getValue() != CellInfo.BLANK) {
                    this.addSetEvent(ci, ci.getValue());
                }
            }
        }
    }

    private void addEventToQueue(Event e) {
        if (e.isRepeatable() || eventsDone.contains(e.toString()) == false) {
            eventsDone.add(e.toString());
            this.queue.add(e);
        }
    }

    public void addSetEvent(CellInfo ci, int value) {
        if (ci.getHasSetEvent() == false) {
            ci.setHasSetEvent(true);
            Event e = new Event("SET", ci, value);
            this.addEventToQueue(e);
        }
    }

    public void addPairedEvent(CellInfo p1, CellInfo p2, String constraintType) {
        if (p1.getHasSetEvent() || p2.getHasSetEvent()) {
            return;
        }
        Event e = new Event("PAIR", p1, p2, p1.getFirstPossible(), p1.getNthPossible(2), constraintType);
        this.addEventToQueue(e);
    }

    public void addGridBlockOtherEvent(int gridNumber, String blockType, int blockNumber, int value) {
        Event e = new Event(Event.GRIDBLOCK, gridNumber, blockType, blockNumber, value);
        this.addEventToQueue(e);
    }

    public void addRCBlockGridEvent(int rcNumber, String rcType, int gridNumber, int value) {
        Event e = new Event(Event.RCBLOCK, rcNumber, rcType, gridNumber, value);
        this.addEventToQueue(e);
    }

    public void printGrid() {
        for (int row = 0; row < SIZE; row++) {
            if (row % 3 == 0) {
                System.out.println(" ------- ------- -------");
            }
            for (int col = 0; col < SIZE; col++) {
                if (col % 3 == 0) 
                    System.out.print("| ");
                CellInfo cell = grid[row][col];
                int value = cell.getValue();
                if (value != CellInfo.BLANK)
                    System.out.print(value + " ");
                else   
                    System.out.print("~ ");

            }
            System.out.println("|");
        }
        System.out.println(" ------- ------- -------");

    }

    public void printConstraints() {
        for (int k = 0; k < this.rowConstraints.length; k++) {
            this.rowConstraints[k].detailedPrint();
        }
        for (int k = 0; k < this.colConstraints.length; k++) {
            this.colConstraints[k].detailedPrint();
        }
        for (int k = 0; k < this.subgridConstraints.length; k++) {
            this.subgridConstraints[k].detailedPrint();
        }
    }

    public Event getCurrentEvent() {
        return this.queue.peek();
    }

    public void processStep() {
        if (this.isFinished)
            return;

        this.stepCount++;

        if (this.queue.isEmpty()) {
            if (checkFinished()) {
                this.isFinished = true;
                return;
            }

            this.queue.add(new Event(Event.ROWCHECK));
            this.queue.add(new Event(Event.COLCHECK));
            this.queue.add(new Event(Event.GRIDCHECK));
            
            return;
        }

        Event e = this.queue.remove();
        if (e != null) {
            String type = e.getType();
            switch (type) {
                case Event.SET:
                    e.getCell().setValue(e.getValue());
                    break;
            }
            for (Constraint cons : rowConstraints) {
                cons.processEvent(e);
            }
            for (Constraint cons : colConstraints) {
                cons.processEvent(e);
            }
            for (Constraint cons : subgridConstraints) {
                cons.processEvent(e);
            }
            
            if (type.equals(Event.SET)) {
                for (int row = 0; row < SIZE; row++) {
                    for (int col = 0; col < SIZE; col++) {
                        CellInfo ci = this.grid[row][col];
                        ci.processSetEvent(e.getType(), e.getCell(), e.getValue());
                        if (ci.checkOnlyPossible()) {
                            this.addSetEvent(ci, ci.getFirstPossible());
                        }
                    }
                }
            } else if (type.equals(Event.PAIR)) {
                for (int row = 0; row < SIZE; row++) {
                    for (int col = 0; col < SIZE; col++) {
                        CellInfo ci = this.grid[row][col];
                        ci.processPairedEvent("PAIR", e.getCell(), e.getCell2(), e.getValue(), e.getValue2(), e.getConstraintType());
                        if (ci.checkOnlyPossible()) {
                            this.addSetEvent(ci, ci.getFirstPossible());
                        }
                    }
                }
            }
        }
    }

    public boolean getIsFinished() {
        return this.isFinished;
    }

    private boolean checkFinished() {
        if (isFinished)
            return true;

        for (Constraint c : rowConstraints) {
            if (!c.isSatisfied())
                return false;
        }
        for (Constraint c : colConstraints) {
            if (!c.isSatisfied())
                return false;
        }
        for (Constraint c : subgridConstraints) {
            if (!c.isSatisfied())
                return false;
        }
        return true;
    }

    public void processQueue() {
        while (!this.isFinished && this.queue.isEmpty() == false) {
            processStep();
        }
    }

    public CellInfo[][] getGrid() {
        return grid;
    }

    public int getStepCount() { return this.stepCount; }
}
