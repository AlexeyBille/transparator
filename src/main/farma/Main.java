package farma;

import org.apache.commons.io.FilenameUtils;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        String srcDir = "/Users/alexey/IdeaProjects/farma/images/src";
        String distDir = "/Users/alexey/IdeaProjects/farma/images/dist";

        int mb = 1024 * 1024;

        // get Runtime instance
        Runtime instance = Runtime.getRuntime();

        List<File> files = Arrays.asList(new File(srcDir).listFiles());

        files.sort(Comparator.comparing(File::getName));

        for (File file : files) {
            try {


                MBFImage input = ImageUtilities.readMBF(new File(file.getPath()));

                MBFImage image = ColourSpace.RGBA.convertFromRGB(input);

                long start = System.currentTimeMillis();
                new TranspareCommand(image).execute();
                long end = System.currentTimeMillis();
                System.out.println(file.getName() + " time: " + (end - start) + "ms " + " memory: " + (instance.totalMemory() - instance.freeMemory()) / mb + "MB");

                FileOutputStream outputStream = new FileOutputStream(distDir + "/" + FilenameUtils.removeExtension(file.getName()) + ".png");
                ImageUtilities.write(image, "PNG", outputStream);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (StackOverflowError e) {
                System.out.println("STACK TRACE LENGTH " + e.getStackTrace().length);
            }
        }


    }
}
