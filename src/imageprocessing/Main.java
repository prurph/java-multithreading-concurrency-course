package imageprocessing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
  public static final String SOURCE_FILE = "./resources/many-flowers.jpg";
  public static final String DESTINATION_FILE = "./out/many-flowers.jpg";

  public static void main(String[] args) throws IOException {
    BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
    BufferedImage resultImage = new BufferedImage(originalImage.getWidth(),
        originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

    long startTime = System.currentTimeMillis();
    // recolorSingleThreaded(originalImage, resultImage);
    recolorMultithreaded(originalImage, resultImage, 6);
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    ImageIO.write(resultImage, "jpg", new File(DESTINATION_FILE));

    System.out.println("Processing took: " + duration + " ms");
  }

  public static void recolorMultithreaded(BufferedImage originalImage, BufferedImage resultImage,
                                          int numberOfThreads) {
    // Break into horizontal bands of the image
    int width = originalImage.getWidth();
    int height = originalImage.getHeight() / numberOfThreads;

    List<Thread> threads =
        IntStream
            .range(0, numberOfThreads)
            .boxed()
            .map((i) -> new Thread(() -> recolorImage(originalImage, resultImage, 0, height * i,
                width, height)))
            .collect(Collectors.toUnmodifiableList());
    threads.forEach(Thread::start);
    threads.forEach((thread) -> {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
  }

  public static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
    recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(),
        originalImage.getHeight());
  }

  public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage,
                                  int leftCorner, int topCorner, int width, int height) {
    for (int x = leftCorner; x < Math.min(leftCorner + width, originalImage.getWidth()); x++) {
      for (int y = topCorner; y < Math.min(topCorner + height, originalImage.getHeight()); y++) {
        recolorPixel(originalImage, resultImage, x, y);
      }
    }
  }

  public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x,
                                  int y) {
    int rgb = originalImage.getRGB(x, y);

    int red = getRed(rgb);
    int green = getGreen(rgb);
    int blue = getBlue(rgb);

    if (isShadeOfGray(red, green, blue)) {
      // Recolor to a purplish shade
      red = Math.min(255, red + 10);
      green = Math.max(0, green - 80);
      blue = Math.max(0, blue - 20);
    }

    setRGB(resultImage, x, y, createRGBFromColors(red, green, blue));
  }

  public static void setRGB(BufferedImage image, int x, int y, int rgb) {
    image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
  }

  public static boolean isShadeOfGray(int red, int green, int blue) {
    return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;

  }

  public static int createRGBFromColors(int red, int green, int blue) {
    int rgb = 0;

    rgb |= blue;
    rgb |= green << 8;
    rgb |= red << 16;
    rgb |= 0xFF000000; // max alpha makes pixel opaque

    return rgb;
  }

  public static int getBlue(int rgb) {
    return rgb & 0x000000FF;
  }

  public static int getGreen(int rgb) {
    return (rgb & 0x0000FF00) >> 8;
  }

  public static int getRed(int rgb) {
    return (rgb & 0x00FF0000) >> 16;
  }
}
