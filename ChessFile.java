import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

// This ChessFile Class serves as the container of the Piece and also handles Movement Logic

public class ChessFile extends JPanel implements MouseListener {

    // Def Colors for Files
    final Color defWhite = new Color(0xF4E8D9);
    final Color defGreen = new Color(0xC7E586);
    final Color availableColor = new Color(0x95BEEC);

    int index;
    Piece piece;
    JLabel pieceHolder;
    ChessBoard chessBoard;
    Color defaultColor;
    boolean available;

    // True iff this square can be used to capture a pawn via En Passant
    boolean enPassant;

    enum CastleStatus {
        NOTCASTLE,
        RIGHTCASTLE,
        LEFTCASTLE
    }

    CastleStatus castleStatus = CastleStatus.NOTCASTLE;


    // Class Constructor
    ChessFile(int index, ChessBoard chessBoard) {

        this.index = index;
        this.chessBoard = chessBoard;
        Color fileColor;
        this.setLayout(new GridLayout(1, 1, 20, 20));

        this.piece = null;
        this.available = false;
        this.enPassant = false;

        this.pieceHolder = new JLabel();
        this.pieceHolder.setHorizontalAlignment(JLabel.CENTER);
        this.pieceHolder.setVerticalAlignment(JLabel.CENTER);
        this.pieceHolder.setBounds(0, 0, 60, 60);
        this.add(this.pieceHolder);

        // Determine color of square based on its index
        if (((index / 8) & 1) == 0)
            fileColor = ((index & 1) == 0) ? defWhite : defGreen;
        else
            fileColor = ((index & 1) == 0) ? defGreen : defWhite;
        defaultColor = fileColor;
        this.setBackground(fileColor);

        this.addMouseListener(this);

        this.setBounds(0, 0, 100, 100);
        
    }

    // Update piece value and icon
    public void placePiece(Piece piece) {
        this.piece = piece;
        this.pieceHolder.setIcon(ImageResizer.resizeImageIcon(piece.getIcon(), 60, 60));
    }

    // Remove piece and its icon
    public void removePiece() {
        this.piece = null;
        this.pieceHolder.setIcon(null); 
    }

    // Return file to default state
    public void reset() {
        this.setBackground(defaultColor);
        this.available = false;
    }

    // Move the piece from the activeFile of the chessBoard to this ChessFile
    public void movePiece() {

        if (this.isValidMove()) {

            // En Passant Reset and Check
            if (this.chessBoard.enPassantFile != null) {
                // If this is an En Passant Move
                if (this.chessBoard.enPassantFile.index == this.index && this.chessBoard.activeFile.piece.getType() == Piece.Type.PAWN) {
                    if (this.chessBoard.activeFile.piece.getSide() == Piece.Side.BLACK) {
                        this.chessBoard.files[this.index - 8].removePiece();
                    } else this.chessBoard.files[this.index + 8].removePiece();
                }
                // Regardless of choice, flip the stored file to null
                this.chessBoard.enPassantFile = null;
            }

            // Place the piece on this file
            this.placePiece(this.chessBoard.activeFile.piece);
    
            this.chessBoard.sideMenu.addMove(this.piece, this.chessBoard.activeFile.index, this.index);

            // Handle Pawn Promotion Logic
            if (this.piece.getType() == Piece.Type.PAWN)
            {
                ((Pawn) this.piece).notFirstMove();
                if (((Pawn) this.piece).checkPromotion(this.index)) {
                    new PromotionMenu(this, this.piece.getSide());
                    this.chessBoard.locked = true;
                }

                // En Passant handling
                ChessFile tmp;
                if (Math.abs(this.index - this.chessBoard.activeFile.index) == 16) {
                    tmp = (this.piece.getSide() == Piece.Side.BLACK) ? this.chessBoard.files[this.index - 8] : this.chessBoard.files[this.index + 8];
                    tmp.enPassant = true;
                    this.chessBoard.enPassantFile = tmp;
                }
            }
    
            // If King is moved update, board parameters
            if (this.piece.getType() == Piece.Type.KING) {
                ((King) this.piece).firstMove = false;
                if (this.piece.getSide() == Piece.Side.BLACK)
                    this.chessBoard.blackKingPos = this.index;
                else
                    this.chessBoard.whiteKingPos = this.index;
            }

            if (this.piece.getType() == Piece.Type.ROOK) ((Rook) this.piece).firstMove = false;
    
            // Remove the old piece and toggle turns
            this.chessBoard.activeFile.removePiece();
            this.chessBoard.activeFile = null;
            this.chessBoard.turn = (this.chessBoard.turn == Piece.Side.WHITE) ? Piece.Side.BLACK : Piece.Side.WHITE;
            this.chessBoard.sideMenu.info.setText(this.chessBoard.turn + "'s Turn!");
            int kingPos = (this.chessBoard.turn == Piece.Side.WHITE) ? this.chessBoard.whiteKingPos : this.chessBoard.blackKingPos;

            // Check if the enemy is in check
            if (this.chessBoard.inCheck(this.chessBoard.turn, kingPos)) {
                this.chessBoard.sideMenu.info.setText(this.chessBoard.turn + " is in Check!");
                if (this.chessBoard.inCheckMate(this.chessBoard.turn, this)) {
                    this.chessBoard.sideMenu.info.setText(this.chessBoard.turn + " is in Checkmate!");
                    this.chessBoard.locked = true;
                }
            }
        } else {
            // If the move would result in a King Capture
            this.chessBoard.sideMenu.info.setText(this.chessBoard.turn + "Move would result in Check/Mate");
            this.chessBoard.activeFile = null;
        }
    } 

    // Determine if a move would result in a King Capture
    public boolean isValidMove() {
        int kingPos;
        Piece cur = this.chessBoard.activeFile.piece;

        // Determine what the projected position of the king is
        if (this.chessBoard.activeFile.piece != null && this.chessBoard.activeFile.piece.getType() == Piece.Type.KING)
            kingPos = this.index;
        else
            kingPos = (this.chessBoard.turn == Piece.Side.BLACK) ? this.chessBoard.blackKingPos : this.chessBoard.whiteKingPos;
        
        // If the piece to be moved is not the King, For Computation imagine a pawn is there
        if (this.chessBoard.activeFile.piece != null && 
            this.chessBoard.activeFile.piece.getType() != Piece.Type.KING) {
            this.piece = new Pawn(this.chessBoard.turn);
            this.chessBoard.activeFile.piece = null;
        }
        
        // Determine if a Check would occur under the configuration created by the move
        boolean validity = !this.chessBoard.inCheck(this.chessBoard.turn, kingPos);
        
        // Return the board to it's original state and return validity
        this.piece = null;
        this.chessBoard.activeFile.piece = cur;
        return validity;
    }

    // Triggers on Mouse Click on the ChessFile
    @Override
    public void mouseClicked(MouseEvent e) {
        if (!this.chessBoard.locked) {
            // If there is no piece already selected, then display the moves of the clicked piece
            if (this.chessBoard.activeFile == null && this.piece != null && this.chessBoard.turn == this.piece.getSide())
                this.chessBoard.displayMoves(this);
            else if (this.available) {
                // If the file selected is avaible
                if (this.castleStatus != CastleStatus.NOTCASTLE) {
                    this.movePiece();
                    if (this.castleStatus == CastleStatus.LEFTCASTLE) {
                        this.chessBoard.activeFile = this.chessBoard.files[this.index - 2];
                        this.chessBoard.files[this.index + 1].movePiece();
                        this.chessBoard.turn = (this.chessBoard.turn == Piece.Side.BLACK) ? Piece.Side.WHITE : Piece.Side.BLACK;
                    }
                    else {
                        this.chessBoard.activeFile = this.chessBoard.files[this.index + 1];
                        this.chessBoard.files[this.index - 1].movePiece();
                        this.chessBoard.turn = (this.chessBoard.turn == Piece.Side.BLACK) ? Piece.Side.WHITE : Piece.Side.BLACK;
                    }
                } else this.movePiece();
                this.chessBoard.paintBoard();
            } else {
                this.chessBoard.paintBoard();
                if (this.piece != null && this.chessBoard.turn == this.piece.getSide())
                    this.chessBoard.displayMoves(this);
            }
        }
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
