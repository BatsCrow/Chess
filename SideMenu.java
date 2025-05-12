import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SideMenu extends JPanel{

    ChessBoard chessBoard;
    JLabel info;
    MovePanel[] panels;
    // 0xF2E9F4

    SideMenu (ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        this.setPreferredSize(new Dimension(200, 800));
        this.setBackground(new Color(0xF2E9F4));
        this.setLayout(new GridLayout(11, 1));
        
        JPanel infoPanel = new JPanel();
        info = new JLabel("Welcome to Chess! White Moves First!");
        info.setFont(new Font("Comic Sans", Font.BOLD, 36));
        infoPanel.setBackground(new Color(0xF6FFE8));
        infoPanel.add(info);
        
        this.add(infoPanel);

        this.panels = new MovePanel[10];
        for (int i = 0; i < 10; i++) {
            this.panels[i] = new MovePanel(this);
            this.add(this.panels[i]);
        }
    
        this.setVisible(true);
    }

    public void addMove(Piece piece, int start, int finish) {
        for (int i = 9; i > 0; i--) {
            this.panels[i].lbl.setText(this.panels[i-1].lbl.getText());
            this.panels[i].lbl.setIcon(this.panels[i-1].lbl.getIcon());
        }
        String movDes = piece.toString() + " moved from " + indexToString(start) + " to " + indexToString(finish);
        this.panels[0].lbl.setText(movDes);
        this.panels[0].lbl.setIcon(ImageResizer.resizeImageIcon(piece.getIcon(), 40, 40));
    }

    public static String indexToString(int pos) {
        int horzPos = pos % 8;
        char column = (char) ((int)'A' + horzPos);
        int vertPos = Math.abs((pos / 8) - 8);
        String index = column + Integer.toString(vertPos);
        return index;
    }
}

class MovePanel extends JPanel {

    JLabel lbl;
    SideMenu menu;

    MovePanel (SideMenu menu) {
        this.setLayout(new GridLayout(1,1 , 10, 10));
        lbl = new JLabel("");
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0x544A56), 3));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        lbl.setVerticalAlignment(JLabel.CENTER);
        lbl.setBackground(new Color(0xF2E9F4));
        lbl.setFont(new Font("Comic Sans", Font.BOLD, 24));
        this.setSize(new Dimension(200, 100));
        this.add(lbl);
        this.menu = menu;
    }
}
