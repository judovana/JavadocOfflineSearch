/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.formatters;

import java.awt.Color;
import java.io.PrintStream;

/**
 *
 * @author jvanek
 */
public class ColoredPlainTextFormatter implements Formatter {

    private final PrintStream out;

    public ColoredPlainTextFormatter(PrintStream out) {
        this.out = out;
    }

    @Override
    public Object highlitStart() {
        return colorToEscapedString(Color.red, true);
    }

    @Override
    public Object highlightEnd() {
        return reset(true);
    }

    private static String reset(boolean fg1) {
        if (fg1) {
            return ansiColorToEscapedString(15, true);
        } else {
            return ansiColorToEscapedString(0, false);
        }
    }

    private static String ansiColorToEscapedString(int ansi, boolean fg) {
        if (fg) {
            return "\033[38;5;" + ansi + "m"; //fg
        } else {
            return "\033[48;5;" + ansi + "m"; //bg
        }

    }

    private static String colorToEscapedString(Color c, boolean fg) {
        return ansiColorToEscapedString(colorToAnsiColor(c, false), fg);

    }

    /**
     * r=3; g=0; b=2; let number=16+36*r+6*g+b; COLOR='\033[38;5;'$number'm' ;
     * NC='\033[0m' ; echo -e "I ${COLOR}love${NC} Stack"
     * http://stackoverflow.com/questions/15682537/ansi-color-specific-rgb-sequence-bash
     *
     * @param pixelColor
     * @return
     */
    private static int colorToAnsiColor(Color pixelColor, boolean negative) {
        int n = 0;
        if (negative) {
            n = 255;
        }
        int ansiR = Math.abs(n - pixelColor.getRed()) / 50;
        int ansiG = Math.abs(n - pixelColor.getGreen()) / 50;
        int ansiB = Math.abs(n - pixelColor.getBlue()) / 50;
        int ansi = 16 + 36 * ansiR + 6 * ansiG + ansiB;
        return ansi;
    }

}
