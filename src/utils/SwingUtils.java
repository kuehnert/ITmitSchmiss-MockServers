package utils;

import java.awt.*;
import java.util.Arrays;

public class SwingUtils {
    private static final String[] PREFERRED_FONTS = {"Cascadia Code", "", "Source Code Pro", "Menlo", "Lucida " +
            "Console", "Courier New"};
    private static Font myFont = null;

    public static void findAllFonts() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        System.out.println(Arrays.toString(fonts));
    }

    public static Font getFont() {
        if (myFont != null) {
            return myFont;
        } else {
            String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            int index = 0; while (myFont == null && index < PREFERRED_FONTS.length) {
                String currentFont = PREFERRED_FONTS[index++]; if (Arrays.binarySearch(fonts, currentFont) >= 0) {
                    System.out.println("Choosing Font " + currentFont); myFont = new Font(currentFont, Font.PLAIN, 14);
                }
            }

            return myFont;
        }
    }
}
