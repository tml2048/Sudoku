import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuOptions extends JPanel implements ActionListener {

    // fields
    private SudokuData data;
    private JButton step;
    private JToggleButton showPossible;
    private JLabel stepCounter;
    private JLabel nextEvent;
    private JToggleButton autorun;
    
    // constructor
    public SudokuOptions(SudokuData data) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.data = data;
        this.showPossible = new JToggleButton("Show Possible");
        this.add(showPossible);
        
        this.step = new JButton("Step");
        this.add(step);
        step.addActionListener(this);

        this.autorun = new JToggleButton("Autorun");
        this.add(autorun);

        this.stepCounter = new JLabel("Step: 0");
        this.add(stepCounter);

        this.nextEvent = new JLabel("Empty!");
        this.add(nextEvent);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.stepCounter.setText("Step: " + data.getStepCount());
        Event e = this.data.getCurrentEvent();
        if (e != null) {
            this.nextEvent.setText(e.toString());
        }
        else {
            this.nextEvent.setText("Empty!");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.step) {
            this.data.processStep();
        }
        if (this.autorun.getModel().isSelected()) {
            this.data.processStep();
        }
    }

    public boolean possibleState() {
        return this.showPossible.getModel().isSelected();
    }
}

