package herdergames.video;

import processing.core.PApplet;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record Video(String path, int frames) {
    public static final Set<Video> VIDEOS = new HashSet<>();

    public static final Video LOOP_VIDEO = new Video("titlescreen/loop", 504);
    public static final Video UEBERGANG_1 = new Video("titlescreen/uebergaenge/1", 351);

    public Video {
        Objects.requireNonNull(path);

        if (frames <= 0) {
            throw new IllegalArgumentException();
        }

        VIDEOS.add(this);
    }

    private String getFrameFileName(int frame) {
        String fileName = Integer.toString(frame + 1);
        if (fileName.length() == 1) {
            fileName = "000" + fileName;
        } else if (fileName.length() == 2) {
            fileName = "00" + fileName;
        } else if (fileName.length() == 3) {
            fileName = "0" + fileName;
        }
        return fileName + ".png";
    }

    public String getOriginalFrameFileName(int frame) {
        return path + "/" + getFrameFileName(frame);
    }

    public String getSkaliertFrameFileName(PApplet applet, int frame) {
        return "skaliert" + applet.width + "x" + applet.height + "/" + path + "/" + getFrameFileName(frame);
    }

    public boolean istSkaliert(PApplet applet, int frame) {
        String skaliertFrameFileName = getSkaliertFrameFileName(applet, frame);
        return Files.exists(applet.sketchFile(skaliertFrameFileName).toPath());
    }

    public String getFrameFileName(PApplet applet, int frame) {
        if (istSkaliert(applet, frame)) {
            return getSkaliertFrameFileName(applet, frame);
        }
        return getOriginalFrameFileName(frame);
    }
}
