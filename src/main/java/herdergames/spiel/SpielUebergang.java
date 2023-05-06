package herdergames.spiel;

import herdergames.video.Video;

public record SpielUebergang(Video video, int frame) {
    public static final SpielUebergang UEBERGANG_1 = new SpielUebergang(new Video("titlescreen/uebergaenge/1", 351), Video.LOOP_VIDEO.frames()-1);

    public SpielUebergang {
        if (frame < 0 || frame >= Video.LOOP_VIDEO.frames()) {
            throw new IllegalArgumentException();
        }
    }
}
