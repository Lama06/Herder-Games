package herdergames.video;

import java.util.Objects;

public record Video(String path, int frames) {
    public static final Video LOOP_VIDEO = new Video("titlescreen/loop", 504);
    public static final Video UEBERGANG_1 = new Video("titlescreen/uebergaenge/1", 351);

    public Video {
        Objects.requireNonNull(path);

        if (frames <= 0) {
            throw new IllegalArgumentException();
        }
    }

    public String getFramePath(int frame) {
        String fileName = Integer.toString(frame + 1);
        if (fileName.length() == 1) {
            fileName = "000" + fileName;
        } else if (fileName.length() == 2) {
            fileName = "00" + fileName;
        } else if (fileName.length() == 3) {
            fileName = "0" + fileName;
        }
        return path + "/" + fileName + ".png";
    }
}
