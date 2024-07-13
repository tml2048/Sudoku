import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class SudokuVisual extends JPanel {

    // constant
    private static final Font BIGFONT = new Font("Sans Serif", Font.BOLD, 40);
    private static final Font SMALLFONT = new Font("Serif", Font.PLAIN, 20);
    private static final Color posColor = new Color(200, 200, 250);
    private static final Color defColor = new Color(230, 230, 230);

    // fields
    private SudokuData data;
    private SudokuOptions options;
    private int gridWidth, gridHeight;
    private int subgridWidth, subgridHeight;

    // constructors

    public SudokuVisual(SudokuData data, SudokuOptions options) {
        this.data = data;
        this.options = options;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.calcLengths();
        g.setColor(this.options.possibleState() ? Color.WHITE : new Color(240, 240, 240));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        CellInfo[][] grid = data.getGrid();
        for (int row = 0; row < SudokuData.SIZE; row++) {
            for (int col = 0; col < SudokuData.SIZE; col++) {
                CellInfo ci = grid[row][col];
                if (this.options.possibleState() == false) {
                    this.drawBlankCell(g, ci, defColor);
                    drawCell(g, ci);
                }
                else {
                    this.drawBlankCell(g, ci, posColor);
                    drawCellPossibles(g, ci);
                }
            }
        }
        
        drawMajorGridlines(g);
    }

    private void calcLengths() {
        this.gridWidth = this.getWidth() / SudokuData.SIZE;
        this.gridHeight = this.getHeight() / SudokuData.SIZE;
        this.subgridWidth = this.gridWidth / 3;
        this.subgridHeight = this.gridHeight / 3;
    }

    private void drawBlankCell(Graphics g, CellInfo ci, Color color) {
        g.setColor(color);
        g.fillRect(ci.getCol() * gridWidth, ci.getRow() * gridHeight, gridWidth - 3, gridHeight - 3);
    }

    private void drawCell(Graphics g, CellInfo ci) {
        if (ci.getValue() == CellInfo.BLANK) {
            return;
        } 
        else {
            if (ci.isGiven()) 
                this.drawMainValue(g, ci.getRow(), ci.getCol(), ci.getValue(), Color.BLACK);
            else
                this.drawMainValue(g, ci.getRow(), ci.getCol(), ci.getValue(), Color.BLUE);
        }            
    }

    private void drawCellPossibles(Graphics g, CellInfo ci) {
        boolean[] possible = ci.getPossible();
        if (ci.isGiven()) {
            drawMainValue(g, ci.getRow(), ci.getCol(), ci.getValue(), Color.RED);
            return;
        } 
        else if (ci.getHasSetEvent() && ci.getValue() != CellInfo.BLANK) {
            drawMainValue(g, ci.getRow(), ci.getCol(), ci.getValue(), Color.BLUE);
            return;
        }
        for (int k = 0; k < possible.length; k++) {
            boolean ok = possible[k];
            if (ok) {
                drawPossibleValue(g, ci.getRow(), ci.getCol(), k + 1, Color.BLACK);
            } 
        }
    }

    private void drawMainValue(Graphics g, int row, int col, int value, Color color) {
        g.setFont(BIGFONT);
        int textWidth = g.getFontMetrics().stringWidth("" + value);
        int textX = col * gridWidth + (gridWidth - textWidth) / 2;
        int textY = (row + 1) * gridHeight - (int) (0.4 * gridHeight);

        g.setColor(color);
        g.drawString(value + "", textX, textY);
        
    }

    private void drawPossibleValue(Graphics g, int row, int col, int value, Color color) {
        int gridX = col * this.gridWidth;
        int gridY = row * this.gridHeight;
        int littleY = ((value - 1) / 3 + 1) * this.subgridHeight;
        int littleX = (value - 1) % 3 * this.subgridWidth;
        int x = gridX + littleX + 5;
        int y = gridY + littleY - 5;
        g.setFont(SMALLFONT);
        g.setColor(color);
        if (value == -1) {
            System.out.println("BAD VALUE!");
        }
        g.drawString(value + "", x, y);
    }

    private void drawMajorGridlines(Graphics g) {
        g.setColor(Color.BLUE);
        for (int col = 3; col < 9; col += 3) {
            int x = col * this.getWidth() / SudokuData.SIZE;
            g.fillRect(x - 4, 0, 4, this.getHeight());
        }
        for (int row = 3; row < 9; row += 3) {
            int y = row * this.getHeight() / SudokuData.SIZE;
            g.fillRect(0, y - 4, this.getWidth(), 4);
        }
    }

}
