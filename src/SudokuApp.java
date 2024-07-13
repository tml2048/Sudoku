import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SudokuApp implements Runnable {
    public static void main(String[] args) throws Exception {

        EventQueue.invokeLater(new SudokuApp());
    }

    @Override
    public void run() {
        JFrame myFrame = new JFrame("Sudoku App");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new SudokuGUI();
        myFrame.getContentPane().add(mainPanel);

        myFrame.pack();
        myFrame.setLocationByPlatform(true);
        myFrame.setVisible(true);
    }
}
