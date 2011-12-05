/**
 * Manages pieces captured by the player
 */
package edu.cmu.cs211.chess.gui.online;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Charles Ruhland
 */
public final class CapturedPieces extends JPanel {
    private static final long serialVersionUID = 3504247603194755121L;

    // Number of capturable chess piece types
    private static final int numPieceTypes = 5;
    
    // Display order of pieces
    private static final String pieceOrder = "QRBNP";
    
    // Displays the available pieces
    private PieceBox pieces = new PieceBox();

    // Counts of the number of pieces available
	@SuppressWarnings("unused")
    private JLabel[] counts = {new JLabel("0"), new JLabel("0"),
                               new JLabel("0"), new JLabel("0"),
                               new JLabel("0")};
    
    public CapturedPieces() {
        setLayout(new Layout());
        add("Pieces", pieces);
    }
    
    // Draws the pieces in a nice box on screen
    private final class PieceBox extends Canvas {
		private static final long serialVersionUID = 8185758303632287146L;
		private transient Painter painter;
        
        public PieceBox() {
            painter = new Painter();
        }
        
        public void paint(Graphics g) {
            // Each pieces gets equal space
            int squareSize = getHeight() / numPieceTypes;

            for (int i = 0; i < numPieceTypes; i++) {
                painter.drawPiece(g, pieceOrder.charAt(i), squareSize, 0,
                                  i * squareSize);
            }
        }
    }
    
    // Class to arrange the PieceBox and piece counts
    private final class Layout implements LayoutManager {
    	@SuppressWarnings("unused")
        private Component pieces, queenCount, bishopCount, knightCount,
            rookCount, pawnCount;

        public void addLayoutComponent(String name, Component comp) {
            if ("Pieces".equals(name)) {
                pieces = comp;
            } else if ("QueenCount".equals(name)) {
                queenCount = comp;
            } else if ("BishopCount".equals(name)) {
                bishopCount = comp;
            } else if ("KnightCount".equals(name)) {
                knightCount = comp;
            } else if ("RookCount".equals(name)) {
                rookCount = comp;
            } else if ("PawnCount".equals(name)) {
                pawnCount = comp;
            } else {
                throw new RuntimeException(
                        "CapturedPieces.Layout: Unsupported component type \""
                        + name + "\"");    
            }
        }

        public void layoutContainer(Container parent) {
            if (pieces != null) {
                pieces.setBounds(0, 0, parent.getWidth(), parent.getHeight());
            }
        }

        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(parent.getWidth(), parent.getHeight());
        }

        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension(parent.getWidth(), parent.getHeight());
        }

        public void removeLayoutComponent(Component comp) {
            throw new UnsupportedOperationException();
        }
    }

}
