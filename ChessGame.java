import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class ChessGame {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setTitle("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setSize(new Dimension(1400, 800));
        window.setIconImage(new ImageIcon("pawnIcon.png").getImage());
        
        ChessBoard chessBoard = new ChessBoard(null);
        SideMenu sideMenu = new SideMenu(chessBoard);
        chessBoard.sideMenu = sideMenu;

        window.setLayout(new GridLayout(1, 2));
        
        window.add(chessBoard);
        window.add(sideMenu);
        window.setVisible(true);
    }
}

// QOL Improvements
        // Start Menu and such
        // Make the Chess Board a panel inside of a larger Frame, so 
        // that We can have an info box at the top
        // After promotion, need to recompute check

        // Weird behavior when I have double queen
        // Allowed self capture by the queen???
        // Not the duplicated one

        // Need to revalidate the checkmate function

        // Add sidebar to show status of board and history