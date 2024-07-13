import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SudokuSolverApp implements Runnable {
    public static void main(String[] args) throws Exception {

        EventQueue.invokeLater(new SudokuSolverApp());
    }

    @Override
    public void run() {
        JFrame myFrame = new JFrame("Sudoku Solver App");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new SudokuSolverGUI();
        myFrame.getContentPane().add(mainPanel);

        myFrame.pack();
        myFrame.setLocationByPlatform(true);
        myFrame.setVisible(true);
    }
}
