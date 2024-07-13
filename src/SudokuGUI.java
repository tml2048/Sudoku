import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.Timer;

public class SudokuGUI extends JPanel implements ActionListener {

    // fields
    private SudokuData data;
    private SudokuOptions options;
    private SudokuVisual visual;

    // constructor
    public SudokuGUI() {
        try {
            this.data = new SudokuData("sample_hard3.txt");
        } catch (IOException e) {
            System.out.println("Could not load file!");
            e.printStackTrace();
        }
        this.options = new SudokuOptions(data);
        this.visual = new SudokuVisual(data, options);

        this.setLayout(new BorderLayout());
        this.add(options, BorderLayout.NORTH);
        this.add(visual, BorderLayout.CENTER);
        this.visual.setPreferredSize(new Dimension(729, 729));
        
        Timer t = new Timer(100, this);
        t.start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.options.actionPerformed(e);
        this.repaint();
    }

    // methods

}
