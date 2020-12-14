package pacman.engine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

class BitmapFontRenderer {

    private BufferedImage bitmapFontImage;
    private BufferedImage[] letters;
    private int letterWidth;
    private int letterHeight;

    private static final int LETTER_VERTICAL_SPACING = 0;
    private static final int LETTER_HORIZONTAL_SPACING = 0;

    BitmapFontRenderer() {
        loadFont();
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

    private void loadFont() {
        try {
            final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("font8x8.png");
            System.out.println(in == null);
            this.bitmapFontImage = ImageIO.read(in);
            loadFont(this.bitmapFontImage);
            in.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadFont(final BufferedImage image) {
        int lettersCount = 16 * 16;
        this.bitmapFontImage = image;
        this.letters = new BufferedImage[lettersCount];
        this.letterWidth = this.bitmapFontImage.getWidth() / 16;
        this.letterHeight = this.bitmapFontImage.getHeight() / 16;

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                this.letters[y * 16 + x] = new BufferedImage(this.letterWidth, this.letterHeight, BufferedImage.TYPE_INT_ARGB);
                final Graphics2D graphics2D = (Graphics2D) this.letters[y * 16 + x].getGraphics();
                graphics2D.drawImage(this.bitmapFontImage, 0, 0, this.letterWidth, this.letterHeight, x * this.letterWidth,
                                 y * this.letterHeight, x * this.letterWidth + this.letterWidth, y * this.letterHeight + this.letterHeight, null);
            }
        }
    }

}
