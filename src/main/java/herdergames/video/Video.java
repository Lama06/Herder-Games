package herdergames.video;

import java.util.Objects;

public record Video(String path, int frames, int fps) {
    public static final Video LOOP_VIDEO = new Video("loop", 399);
    public static final Video UEBERGANG_B008 = new Video("uebergaenge/b008", 439);
    public static final Video UEBERGANG_B107 = new Video("uebergaenge/b107", 385);
    public static final Video UEBERGANG_E202 = new Video("uebergaenge/e202", 101);
    public static final Video UEBERGANG_E901 = new Video("uebergaenge/e901", 279);
    public static final Video UEBERGANG_F106 = new Video("uebergaenge/f106", 381);
    public static final Video UEBERGANG_F109 = new Video("uebergaenge/f109", 389);
    public static final Video UEBERGANG_BANK = new Video("uebergaenge/bank", 137);

    public Video {
        Objects.requireNonNull(path);

        if (frames <= 0) {
            throw new IllegalArgumentException();
        }

        if (fps <= 0) {
            throw new IllegalArgumentException();
        }
    }

    public Video(String path, int frames) {
        this(path, frames, 30);
    }

    public String getFramePath(int frame) {
        return path + "/" + (frame + 1) + ".jpeg";
    }
}
