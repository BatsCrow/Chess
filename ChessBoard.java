import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

// import javax.swing.ImageIcon;
// import javax.swing.JFrame;
import javax.swing.JPanel;

// This class serves as the container for the ChessFiles and contains functions pertaining to the entire board

public class ChessBoard extends JPanel {

    SideMenu sideMenu;

    ChessFile[] files;
    ChessFile activeFile;
    ChessFile enPassantFile;
    ArrayList<ChessFile> highlightedFiles;

    Piece.Side turn;
    boolean locked;

    int blackKingPos;
    int whiteKingPos;

    // Constructer for ChessBoard Class
    ChessBoard(SideMenu sideMenu) {
        // Define the Window
        // this.setTitle("Chess");
        // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // this.setResizable(true);
        this.setPreferredSize(new Dimension(800, 800));
        // this.setIconImage(new ImageIcon("pawnIcon.png").getImage());

        // Instantiate the Files of the Board
        this.setLayout(new GridLayout(8, 8));
        files = new ChessFile[64];
        for (int i = 0; i < 64; i++) {
            files[i] = new ChessFile(i, this);
            this.add(files[i]);
        }

        // Setup Board with Pieces
        setupBoard();
        //setupTestBoard();

        this.sideMenu = sideMenu;
        this.activeFile = null;
        this.enPassantFile = null;
        this.highlightedFiles = null;
        this.turn = Piece.Side.WHITE;
        this.locked = false;

        this.setVisible(true);
    }

    // Initializes the Normal Starting Positions of a Chess Board
    public void setupBoard() {
        files[0].placePiece(new Rook(Piece.Side.BLACK));
        files[1].placePiece(new Knight(Piece.Side.BLACK));
        files[2].placePiece(new Bishop(Piece.Side.BLACK));
        files[3].placePiece(new Queen(Piece.Side.BLACK));
        files[4].placePiece(new King(Piece.Side.BLACK));
        this.blackKingPos = 4;
        files[5].placePiece(new Bishop(Piece.Side.BLACK));
        files[6].placePiece(new Knight(Piece.Side.BLACK));
        files[7].placePiece(new Rook(Piece.Side.BLACK));

        for (int i = 8; i < 16; i++)
            files[i].placePiece(new Pawn(Piece.Side.BLACK));

        for (int i = 48; i < 56; i++)
            files[i].placePiece(new Pawn(Piece.Side.WHITE));

        files[56].placePiece(new Rook(Piece.Side.WHITE));
        files[57].placePiece(new Knight(Piece.Side.WHITE));
        files[58].placePiece(new Bishop(Piece.Side.WHITE));
        files[59].placePiece(new Queen(Piece.Side.WHITE));
        files[60].placePiece(new King(Piece.Side.WHITE));
        this.whiteKingPos = 60;
        files[61].placePiece(new Bishop(Piece.Side.WHITE));
        files[62].placePiece(new Knight(Piece.Side.WHITE));
        files[63].placePiece(new Rook(Piece.Side.WHITE));
    }

    // Return Any highlighted Files to original display
    public void paintBoard() {
        if (this.highlightedFiles != null)
            for (ChessFile file : this.highlightedFiles)
                file.reset();
    }

    // Given a ChessFile, display the moves that the piece on that file can make
    public void displayMoves(ChessFile file) {
        // If there is no piece on the file: Do Nothing
        if (file.piece == null) {
            return;
        }

        // Save the selected File and those to be highlighted
        this.activeFile = file;
        this.highlightedFiles = new ArrayList<>();

        // Iterate over each path, breaking when encountering an obstacle
        for (ArrayList<Integer> path : file.piece.computeMoves(file.index)) {
            for (int pos : path) {
                if (pos < 64 && pos > -1) {
                    if (this.files[pos].piece == null) {
                        markFile(pos);
                    } else if (this.files[pos].piece.getSide() != this.activeFile.piece.getSide()) {
                        // Break given that Pawns do not capture in the same way they move
                        if (this.activeFile.piece.getType() == Piece.Type.PAWN) break;
                        markFile(pos);
                        break;
                    } else
                        break;
                }
            }
        }

        // Handle Capture Pawn Behavior
        if (this.activeFile.piece.getType() == Piece.Type.PAWN) {
            for (int pos : ((Pawn) this.activeFile.piece).computeKillMoves(this.activeFile.index)) {
                if (pos < 64 && pos > -1 && 
                    this.files[pos].piece != null && 
                    this.files[pos].piece.getSide() != this.activeFile.piece.getSide())
                        markFile(pos);
                else if (pos < 64 && pos > -1 && this.enPassantFile != null && this.enPassantFile.index == pos) {
                    markFile(pos);
                }
            }
        }
    
        // Handle King Castling Behavior
        if (this.activeFile.piece.getType() == Piece.Type.KING) {
            if (canCastle(turn, true)) {
                this.files[file.index - 2].castleStatus = ChessFile.CastleStatus.LEFTCASTLE;
                markFile(file.index - 2);
            }
            if (canCastle(turn, false)) {
                this.files[file.index + 2].castleStatus = ChessFile.CastleStatus.RIGHTCASTLE;
                markFile(file.index + 2);
            }
        }
    }

    // Highlight a file at a given position in the board and mark as available
    public void markFile(int pos) {
        this.highlightedFiles.add(this.files[pos]);
        this.files[pos].setBackground(this.activeFile.availableColor);
        this.files[pos].available = true;
    }

    // Determine whether a given Board Position is in Check
    public boolean inCheck(Piece.Side side, int kingPos) {

        Piece.Side oppSide = (side == Piece.Side.BLACK) ? Piece.Side.WHITE : Piece.Side.BLACK;
        boolean rtn = false;

        // Iterate through the Board
        for (ChessFile file : this.files) {
            // Check if file has a piece on it
            if (file.piece != null && file.piece.getSide() == oppSide) {
                // Compute whether king is in danger
                for (ArrayList<Integer> path : file.piece.computeMoves(file.index)) {
                    // Special case to account for Pawn Captures
                    if (file.piece.getType() == Piece.Type.PAWN) {
                        for(int pos : ((Pawn) file.piece).computeKillMoves(file.index))
                            if (pos == kingPos) return true;
                        break;
                    }
                    for (int pos : path) {
                        if (pos < 64 && pos > -1) {
                            // Mark if the king is in danger and continue to check to catch backwards moves
                            if (pos == kingPos) rtn = true;
                            if (this.files[pos].piece != null && this.files[pos].piece.getType() != Piece.Type.KING)
                                break;
                        }
                    }
                }
            }
        }

        return rtn;
    }

    // Determine if Castling is possible for a given Side and direction
    public boolean canCastle(Piece.Side side, boolean left) {
        int kingPos;
        int leftRookPos;
        int rightRookPos;
        int targetKingPos;

        // Determine value of constants based on the Side parameter
        if (side == Piece.Side.BLACK) {
            kingPos = 4;
            leftRookPos = 0;
            rightRookPos = 7;
            targetKingPos = this.blackKingPos;
        } else {
            kingPos = 60;
            leftRookPos = 56;
            rightRookPos = 63;
            targetKingPos = this.whiteKingPos;
        }
        
        // If the king is in check or the wrong position: return false
        if (targetKingPos != kingPos) return false;
        if (this.inCheck(side, targetKingPos)) return false;

        Piece leftRook = this.files[leftRookPos].piece;
        Piece rightRook = this.files[rightRookPos].piece;
        Piece king = this.files[kingPos].piece;

        // Ensure the Castling Requirements are Met
        if (((King) king).firstMove) {
            if (left && leftRook != null && leftRook.getType() == Piece.Type.ROOK && ((Rook) leftRook).firstMove) {
                // Iterate over spaces between Rook and King to make sure they are empty and not in Check
                for (int i = kingPos-1; i > leftRookPos; i--) {
                    if (this.files[i].piece != null) 
                        return false;
                    if ((i > kingPos-3) && this.inCheck(side, i)) return false;
                }
            } else if (!left && rightRook != null && rightRook.getType() == Piece.Type.ROOK && ((Rook) rightRook).firstMove) {
                // Iterate over spaces between Rook and King to make sure they are empty and not in Check
                for (int i = kingPos+1; i < rightRookPos; i++) {
                    if (this.files[i].piece != null)
                        return false;
                    if (this.inCheck(side, i)) return false;
                }
            } else return false;
        }
        return true;
    }

    // Determine if given side is in CheckMate, curFile is the file of the most recently moved piece
    public boolean inCheckMate(Piece.Side side, ChessFile curFile) {

        //Piece.Side oppSide = (side == Piece.Side.BLACK) ? Piece.Side.WHITE : Piece.Side.BLACK;
        int kingPos = (side == Piece.Side.BLACK) ? blackKingPos : whiteKingPos;

        int horzPos = kingPos % 8;
        int vertPos  = kingPos / 8;

        int leftBound = (horzPos == 0) ? 0 : -1;
        int rightBound = (horzPos == 7) ? 0 : 1;
        int topBound = (vertPos == 0) ? 0 : -1;
        int bottomBound = (vertPos == 7) ? 0 : 1;

        // This first loop is the quick catch for Checkmates based only on moving the King
        for (int i = leftBound; i <= rightBound; i++) {
            for (int j = topBound; j <= bottomBound; j++) {
                if (i == 0 && j == 0) continue;
                int testPos = kingPos + i + (8*j);
                // If there is a file where the king can move and be safe then return false
                if (testPos < 0 || testPos > 63) continue;
                if (this.files[testPos].piece == null && !inCheck(side, testPos)) return false;
            }
        }

        ArrayList<Integer> killPath = null;
        for (ArrayList<Integer> path : curFile.piece.computeMoves(curFile.index)){
            if (path.contains(kingPos)) {
                killPath = path;
                break;
            }
        }

        if (killPath == null) {
            System.err.println("An error has occured in the Check function: No Capture Path Found!");
            System.err.println("Terminating Game via CheckMate!");
            return true;
        }
        
        killPath.add(curFile.index);
        for (ChessFile file : this.files) {
            if (file.piece != null && file.piece.getSide() == side && file.piece.getType() != Piece.Type.KING) {
                for (ArrayList<Integer> path : file.piece.computeMoves(file.index)) {
                    for (int pos : path) {
                        if (file.piece.getType() == Piece.Type.PAWN && pos == curFile.index) continue;
                        if (killPath.contains(pos)) return false;
                        if (pos > -1 && pos < 64 && this.files[pos].piece != null) break;
                    }
                }
                if (file.piece.getType() == Piece.Type.PAWN) {
                        for (int pos : ((Pawn) file.piece).computeKillMoves(file.index)) {
                            if (pos == curFile.index) return false;
                        }
                }
            }
        }

        return true;
    }
}
