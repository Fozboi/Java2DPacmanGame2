package pacman.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

class BitmapFontRenderer {

    private BufferedImage bitmapFontImage;
    private BufferedImage[] letters;
    private int letterWidth;
    private int letterHeight;

    private static final int LETTER_VERTICAL_SPACING = 0;
    private static final int LETTER_HORIZONTAL_SPACING = 0;

    BitmapFontRenderer(final String fontRes,
                       final int cols,
                       final int rows) {
        loadFont(fontRes, cols, rows);
    }

    void drawText(final Graphics2D g,
                  final String text,
                  final int x,
                  final int y) {
        if (this.letters == null) {
            return;
        }
        int px = 0;
        int py = 0;
        for (int i=0; i<text.length(); i++) {
            int c = text.charAt(i);
            if (c == (int) '\n') {
                py += this.letterHeight + LETTER_VERTICAL_SPACING;
                px = 0;
                continue;
            }
            else if (c == (int) '\r') {
                continue;
            }
            final Image letter = this.letters[c];
            g.drawImage(letter, px + x, py + y + 1, null);
            px += this.letterWidth + LETTER_HORIZONTAL_SPACING;
        }
    }

    private void loadFont(final String filename,
                          final Integer cols,
                          final Integer rows) {
        try {
            this.bitmapFontImage = ImageIO.read(getClass().getResourceAsStream(filename));
            loadFont(this.bitmapFontImage, cols, rows);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadFont(final BufferedImage image,
                          final Integer cols,
                          final Integer rows) {
        int lettersCount = cols * rows;
        this.bitmapFontImage = image;
        this.letters = new BufferedImage[lettersCount];
        this.letterWidth = this.bitmapFontImage.getWidth() / cols;
        this.letterHeight = this.bitmapFontImage.getHeight() / rows;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x <cols; x++) {
                this.letters[y * cols + x] = new BufferedImage(this.letterWidth, this.letterHeight, BufferedImage.TYPE_INT_ARGB);
                final Graphics2D graphics2D = (Graphics2D) this.letters[y * cols + x].getGraphics();
                graphics2D.drawImage(this.bitmapFontImage, 0, 0, this.letterWidth, this.letterHeight, x * this.letterWidth,
                                 y * this.letterHeight, x * this.letterWidth + this.letterWidth, y * this.letterHeight + this.letterHeight, null);
            }
        }
    }

}
