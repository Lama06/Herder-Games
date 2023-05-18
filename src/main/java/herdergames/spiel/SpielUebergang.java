package herdergames.spiel;

import herdergames.video.Video;

import java.util.Objects;

public record SpielUebergang(Video video, int frame) {
    public static final SpielUebergang B008 = new SpielUebergang(Video.UEBERGANG_B008, 0);
    public static final SpielUebergang B107 = new SpielUebergang(Video.UEBERGANG_B107, 0);
    public static final SpielUebergang E202 = new SpielUebergang(Video.UEBERGANG_E202, 0);
    public static final SpielUebergang E901 = new SpielUebergang(Video.UEBERGANG_E901, 0);
    public static final SpielUebergang F106 = new SpielUebergang(Video.UEBERGANG_F106, 0);
    public static final SpielUebergang F109 = new SpielUebergang(Video.UEBERGANG_F109, 0);
    public static final SpielUebergang BANK = new SpielUebergang(Video.UEBERGANG_BANK, 0);

    public SpielUebergang {
        Objects.requireNonNull(video);

        if (frame < 0 || frame >= Video.LOOP_VIDEO.frames()) {
            throw new IllegalArgumentException();
        }
    }
}
