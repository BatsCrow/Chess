import java.util.ArrayList;

import javax.swing.ImageIcon;

// This file contains the definitions of Classes for each Piece using the Interface Below

public interface Piece {

    // This function is used to initialize the Icon of the Piece
    public void define();

    // Getter for the Icon of the Piece
    public ImageIcon getIcon();

    //  Getter for the Type of the Piece
    public Type getType();

    // Getter for the Side of the Piece
    public Side getSide();

    // Returns an Array of Paths that a given piece can take | The Paths are defined as an array of indices on that path
    public ArrayList<ArrayList<Integer>> computeMoves(int index);

    enum Side {
        BLACK,
        WHITE
    }

    enum Type {
        PAWN,
        ROOK,
        KNIGHT,
        BISHOP,
        QUEEN,
        KING
    }
}


class Pawn implements Piece {

    final Type type = Piece.Type.PAWN;
    Side color;
    ImageIcon icon;
    boolean firstMove;

    Pawn (Side color) {
        this.color = color;
        this.firstMove = true;
        define();
    }

    public void define() {
        try {
            if (color == Side.BLACK)
                icon = new ImageIcon("Icons/black-pawn.png");
            else
                icon = new ImageIcon("Icons/white-pawn.png");
        } catch (Exception e) {
            System.err.println("Could not initialize Pawn Object!");
        }
    }

    public ImageIcon getIcon() {
        return this.icon;
    }

    public Type getType() {
        return this.type;
    }

    public Side getSide() {
        return this.color;
    }

    // Setter for the firstMove variable
    public void notFirstMove() {
        this.firstMove = false;
    }

    @Override
    public String toString() {
        return this.color.toString() + " Pawn";
    }

    public ArrayList<ArrayList<Integer>> computeMoves(int index) {
        ArrayList<ArrayList<Integer>> newPos = new ArrayList<>();

        // Used to adjust movement depending on the side of the Pawn
        int adjust = (this.color == Piece.Side.BLACK) ? 1 : -1;

        newPos.add(new ArrayList<>());
        newPos.get(0).add(index+(8*adjust));
        // On FirstMove, Pawns can move 2
        if (this.firstMove)
            newPos.get(0).add(index+(16*adjust));

        return newPos;
    }

    // Computes the capture moves of Pawns
    public ArrayList<Integer> computeKillMoves(int index) {
        ArrayList<Integer> newPos = new ArrayList<>();
        int adjust = (this.color == Piece.Side.BLACK) ? 1 : -1;

        int horzPos = index % 8;
        if (horzPos != 0) newPos.add(index + (8*adjust) - 1);
        if (horzPos != 7) newPos.add(index + (8*adjust) + 1);

        return newPos;
    }

    // Determine whether a Pawn is eligible for Promotion
    public boolean checkPromotion(int index) {
        if (this.color == Piece.Side.BLACK) {
            if (index > 55) return true;
        } else if (index < 8) return true;
        return false;
    }
}

class Rook implements Piece {

    final Type type = Piece.Type.ROOK;
    Side color;
    ImageIcon icon;
    boolean firstMove;
    
 
    Rook (Side color) {
        this.color = color;
        this.firstMove = true;
        
        define(); 
    }

    public void define() {
        try {
            if (color == Side.BLACK)
                icon = new ImageIcon("Icons/black-rook.png");
            else
                icon = new ImageIcon("Icons/white-rook.png");
        } catch (Exception e) {
            System.err.println("Could not initialize Rook Object!");
        }
    }

    public ImageIcon getIcon() {
        return this.icon;
    }
  
    public Type getType() {
        return this.type;
    }

    public Side getSide() {
        return this.color;
    }

    // Setter for firstMove
    public void notFirstMove() {
        this.firstMove = false;
    }

    @Override
    public String toString() {
        return this.color.toString() + " Rook";
    }

    public ArrayList<ArrayList<Integer>> computeMoves(int index) {
        ArrayList<ArrayList<Integer>> newPos = new ArrayList<>();

        // Used to avoid overshooting outside of board bounds
        int horzPos = index % 8;
        int vertPos = index / 8;

        // Multiple Loops to compute the Straight Paths
        for (int i = 0; i < 4; i++) newPos.add(new ArrayList<>());
        for (int i = 1; i <= horzPos; i++) newPos.get(0).add(index - i);
        for (int i = 1; i < 8 - horzPos; i++) newPos.get(1).add(index + i);
        for (int i = 1; i <= vertPos; i++) newPos.get(2).add(index - (8*i));
        for (int i = 1; i < 8 - vertPos; i++) newPos.get(3).add(index + (8 * i));

        return newPos;
    }
}

class Knight implements Piece {

    final Type type = Piece.Type.KNIGHT;
    Side color;
    ImageIcon icon;
    
 
    Knight (Side color) {
        this.color = color;
        
        define(); 
    }

    public void define() {
        try {
            if (color == Side.BLACK)
                icon = new ImageIcon("Icons/black-knight.png");
            else
                icon = new ImageIcon("Icons/white-knight.png");
        } catch (Exception e) {
            System.err.println("Could not initialize Knight Object!");
        }
    }

    public ImageIcon getIcon() {
        return this.icon;
    }

    public Type getType() {
        return this.type;
    }

    public Side getSide() {
        return this.color;
    }

    @Override
    public String toString() {
        return this.color.toString() + " Knight";
    }

    public ArrayList<ArrayList<Integer>> computeMoves(int index) {
        ArrayList<ArrayList<Integer>> newPos = new ArrayList<>();

        int horzPos = index % 8;

        // Used to avoid jumping out of bounds
        int leftBound = (horzPos < 2) ? horzPos : 2;
        int rightBound = (horzPos < 6) ? 2 : 7 - horzPos;

        // Loop used to compute array of positions
        int k = 0;
        for (int i = -leftBound; i <= rightBound; i++) {
            if (i == 0) continue;
            if ((i & 1) == 0) {
                for (int j = 0; j < 2; j++) newPos.add(new ArrayList<>());
                newPos.get(k).add(index + i + 8);
                k++;
                newPos.get(k).add(index + i - 8);
                k++;
            } else {
                for (int j = 0; j < 2; j++) newPos.add(new ArrayList<>());
                newPos.get(k).add(index + i + 16);
                k++;
                newPos.get(k).add(index + i - 16);
                k++;
            }
        }

        return newPos;
    }
}

class Bishop implements Piece {

    final Type type = Piece.Type.BISHOP;
    Side color;
    ImageIcon icon;
    
 
    Bishop (Side color) {
        this.color = color;
        
        define(); 
    }

    public void define() {
        try {
            if (color == Side.BLACK)
                icon = new ImageIcon("Icons/black-bishop.png");
            else
                icon = new ImageIcon("Icons/white-bishop.png");
        } catch (Exception e) {
            System.err.println("Could not initialize Bishop Object!");
        }
    }

    public ImageIcon getIcon() {
        return this.icon;
    }

    public Type getType() {
        return this.type;
    }

    public Side getSide() {
        return this.color;
    }

    @Override
    public String toString() {
        return this.color.toString() + " Bishop";
    }

    public ArrayList<ArrayList<Integer>> computeMoves(int index) {
        ArrayList<ArrayList<Integer>> newPos = new ArrayList<>();
        for (int i = 0; i < 4; i++) newPos.add(new ArrayList<>());

        int horzPos = index % 8;

        // Two Loops to compute diagonals
        for (int i = 1; i <= horzPos; i++) {
            newPos.get(0).add(index - i + (i*8));
            newPos.get(1).add(index - i - (i*8));
        }

        for (int i = 1; i < (8 - horzPos); i++) {
            newPos.get(2).add(index + i + (i*8));
            newPos.get(3).add(index + i - (i*8));
        }

        return newPos;
    }
}

class Queen implements Piece {

    final Type type = Piece.Type.QUEEN;
    Side color;
    ImageIcon icon;
    
 
    Queen (Side color) {
        this.color = color;
        
        define(); 
    }

    public void define() {
        try {
            if (color == Side.BLACK)
                icon = new ImageIcon("Icons/black-queen.png");
            else
                icon = new ImageIcon("Icons/white-queen.png");
        } catch (Exception e) {
            System.err.println("Could not initialize Queen Object!");
        }
    }

    public ImageIcon getIcon() {
        return this.icon;
    }

    public Type getType() {
        return this.type;
    }

    public Side getSide() {
        return this.color;
    }

    @Override
    public String toString() {
        return this.color.toString() + " Queen";
    }

    public ArrayList<ArrayList<Integer>> computeMoves(int index) {
        ArrayList<ArrayList<Integer>> newPos = new ArrayList<>();
        for (int i = 0; i < 8; i++) newPos.add(new ArrayList<>());

        // Combine methods of Rook and Bishop
        int horzPos = index % 8;
        int vertPos = index / 8;
        for (int i = 1; i <= horzPos; i++) {
            newPos.get(0).add(index - i + (i*8));
            newPos.get(1).add(index - i - (i*8));
            newPos.get(2).add(index - i);
        }
        for (int i = 1; i < (8 - horzPos); i++) {
            newPos.get(3).add(index + i + (i*8));
            newPos.get(4).add(index + i - (i*8));
            newPos.get(5).add(index + i);
        }
        for (int i = 1; i <= vertPos; i++)
            newPos.get(6).add(index - (i*8));
        for (int i = 1; i < (8 - vertPos); i++)
            newPos.get(7).add(index + (i*8));


        return newPos;
    }
}

class King implements Piece {

    final Type type = Piece.Type.KING;
    Side color;
    ImageIcon icon;
    boolean firstMove;
 
    King (Side color) {
        this.color = color;
        this.firstMove = true;
        define(); 
    }

    public void define() {
        try {
            if (color == Side.BLACK)
                icon = new ImageIcon("Icons/black-king.png");
            else
                icon = new ImageIcon("Icons/white-king.png");
        } catch (Exception e) {
            System.err.println("Could not initialize King Object!");
        }
    }

    public ImageIcon getIcon() {
        return this.icon;
    }

    public Type getType() {
        return this.type;
    }

    public Side getSide() {
        return this.color;
    }

    // Setter for firstMove
    public void notFirstMove() {
        this.firstMove = false;
    }

    @Override
    public String toString() {
        return this.color.toString() + " King";
    }

    public ArrayList<ArrayList<Integer>> computeMoves(int index) {
        ArrayList<ArrayList<Integer>> newPos = new ArrayList<>();
        for (int i = 0; i < 9; i++) newPos.add(new ArrayList<>());

        // simple Loop to compute square arond King

        int horzPos = index % 8;
        int vertPos  = index / 8;

        int leftBound = (horzPos == 0) ? 0 : -1;
        int rightBound = (horzPos == 7) ? 0 : 1;
        int topBound = (vertPos == 0) ? 0 : -1;
        int bottomBound = (vertPos == 7) ? 0 : 1;

        int k = 0;
        for (int i = leftBound; i <= rightBound; i++) {
            for (int j = topBound; j <= bottomBound; j++) {
                newPos.get(k).add(index + i + (j*8));
                k++;
            }
        }

        return newPos;
    }
}