package farma;


import org.openimaj.image.MBFImage;

import java.util.Stack;


class TranspareCommand {

    private MBFImage image;
    private Boolean[][] wasHereMap;
    private static final float FUZZINESS = 0.05F;

    // StackOverflowError
    private Stack<HandleCell> executionStack;

    TranspareCommand(MBFImage image) {

        this.image = image;
        this.executionStack = new Stack<>();

        this.wasHereMap = new Boolean[image.getWidth()][image.getHeight()];

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                wasHereMap[i][j] = false;
            }
        }
    }

    void execute() {
        for (int i = 0; i < this.image.getHeight(); i++) {
            this.executionStack.push(new HandleCell(0, i));
            this.executionStack.push(new HandleCell(this.image.getWidth() - 1, i));
        }

        for (int i = 0; i < this.image.getHeight(); i++) {
            this.executionStack.push(new HandleCell(i, 0));
            this.executionStack.push(new HandleCell(i, this.image.getHeight() - 1));
        }
        while (!executionStack.empty()) {
            this.executionStack.pop().run();
        }
    }


    private class HandleCell implements Runnable {

        private int x;
        private int y;

        HandleCell(int x, int y) {

            this.x = x;
            this.y = y;
        }

        public void run() {
            if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
                return;
            }

            if (wasHereMap[x][y]) {
                return;
            }

            wasHereMap[x][y] = true;


            if (chechFuzziness(x, y)) {
                // image.setPixel does not work
                image.getBand(3).setPixel(x, y, 0F);


                executionStack.push(new HandleCell(x - 1, y));
                executionStack.push(new HandleCell(x + 1, y));
                executionStack.push(new HandleCell(x, y - 1));
                executionStack.push(new HandleCell(x + 1, y - 1));
                executionStack.push(new HandleCell(x - 1, y - 1));
                executionStack.push(new HandleCell(x, y + 1));
                executionStack.push(new HandleCell(x - 1, y + 1));
                executionStack.push(new HandleCell(x + 1, y + 1));
            }
        }

        private Boolean chechFuzziness(int x, int y) {
            float fuzziness = FUZZINESS;
            Float[] white = {1F, 1F, 1F};

            for (int i = 0; i < 3; i++) {
                fuzziness = fuzziness - Math.abs(image.getPixel(x, y)[i] - white[i]);
            }

            return fuzziness > 0;
        }
    }


}
