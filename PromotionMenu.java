import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

// This class is used to implement the Promotion Menu associated with Pawns

public class PromotionMenu extends JFrame {

    ChessFile chessfile;
    Piece.Side color;

    PromotionMenu (ChessFile chessfile, Piece.Side color) {
        this.setTitle("Promotion Menu");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        this.setSize(new Dimension(400, 100));
        this.setIconImage(new ImageIcon("pawnIcon.png").getImage());

        this.setLayout(new GridLayout(1, 4));

        this.chessfile = chessfile;
        this.color = color;

        // Create Panels for each potential type of promotion
        this.add(new PromotionPanel(new Rook(color), this));
        this.add(new PromotionPanel(new Bishop(color), this));
        this.add(new PromotionPanel(new Knight(color), this));
        this.add(new PromotionPanel(new Queen(color), this));

        this.setVisible(true);
    }
    
}

class PromotionPanel extends JPanel implements MouseListener {

    Piece piece;
    JLabel pieceHolder;
    PromotionMenu menu;
    PromotionPanel (Piece piece, PromotionMenu menu) {
        this.piece = piece;
        this.menu = menu;
        this.setLayout(new GridLayout(1, 1, 10, 10));
        this.pieceHolder = new JLabel();
        this.pieceHolder.setHorizontalAlignment(JLabel.CENTER);
        this.pieceHolder.setVerticalAlignment(JLabel.CENTER);
        this.pieceHolder.setBounds(0, 0, 80, 80);
        Border border = BorderFactory.createLineBorder(Color.BLACK, 3);
        this.pieceHolder.setBorder(border);
        this.pieceHolder.setBackground(new Color(0xC7E586));

        this.pieceHolder.setIcon(ImageResizer.resizeImageIcon(piece.getIcon(), 60, 60));
    
        this.addMouseListener(this);
        this.add(this.pieceHolder);
        this.setBounds(0, 0, 100, 100);
        
    }

    // On MouseClick, replace with the selected piece, unlock the board and close the menu
    @Override
    public void mouseClicked(MouseEvent e) {
        this.menu.chessfile.placePiece(this.piece);
        this.menu.chessfile.chessBoard.locked = false;
        int kingPos = (this.menu.color == Piece.Side.BLACK) ? 
            this.menu.chessfile.chessBoard.blackKingPos : 
            this.menu.chessfile.chessBoard.whiteKingPos;
        Piece.Side oppSide = (this.menu.color == Piece.Side.BLACK) ? Piece.Side.WHITE : Piece.Side.BLACK;
        if (this.menu.chessfile.chessBoard.inCheck(oppSide, kingPos)) {
            System.out.println("You are in Check!");
        }
        this.menu.dispose();
    }
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}


}
