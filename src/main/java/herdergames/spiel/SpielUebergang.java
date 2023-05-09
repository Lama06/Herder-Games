package herdergames.spiel;

import herdergames.video.Video;

import java.util.Objects;

public record SpielUebergang(Video video, int frame) {
    public static final SpielUebergang UEBERGANG_1 = new SpielUebergang(Video.UEBERGANG_1, Video.LOOP_VIDEO.frames()-1);

    public SpielUebergang {
        Objects.requireNonNull(video);

        if (frame < 0 || frame >= Video.LOOP_VIDEO.frames()) {
            throw new IllegalArgumentException();
        }
    }
}
