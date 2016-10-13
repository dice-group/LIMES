package org.aksw.limes.core.util;

public class ProgressBar {

    protected static String progressChar = "■";
    protected static int width = 50; // progress bar width in chars

    public static String getProgressChar() {
        return progressChar;
    }

    public static void setProgressChar(String progressChar) {
        ProgressBar.progressChar = progressChar;
    }

    /**
     * print progress bar to the standard output
     *
     * @param progressPercentage
     */
    public static void print(double progressPercentage) {


        System.out.print("\r[");
        int i = 0;
        for (; i <= (int) (progressPercentage * width); i++) {

            System.out.print(progressChar);
        }
        for (; i < width; i++) {
            System.out.print(" ");
        }
        System.out.print("]");
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        ProgressBar.width = width;
    }

}
