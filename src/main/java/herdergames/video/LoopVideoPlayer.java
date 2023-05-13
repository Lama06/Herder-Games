package herdergames.video;

import herdergames.util.ImageUtil;
import processing.core.PApplet;
import processing.core.PImage;

public final class LoopVideoPlayer {
    private final PApplet applet;
    private final float speed;
    private final PImage[] frames;
    private float currentFrame;
    private PImage lastFrame;

    public LoopVideoPlayer(PApplet applet, Video video, float speed, int startFrame) {
        if (startFrame < 0 || startFrame >= video.frames() || speed <= 0) {
            throw new IllegalArgumentException();
        }

        this.applet = applet;
        this.speed = speed;

        frames = new PImage[video.frames()];
        for (int frame = startFrame; frame < video.frames(); frame++) {
            frames[frame] = applet.requestImage(video.getFramePath(frame));
        }
        for (int frame = 0; frame < startFrame; frame++) {
            frames[frame] = applet.requestImage(video.getFramePath(frame));
        }

        currentFrame = startFrame;
    }

    public void draw() {
        PImage frame = frames[(int) currentFrame];
        if (frame.width == 0) {
            if (lastFrame == null) {
                return;
            }
            ImageUtil.imageVollbildZeichnen(applet, lastFrame);
            return;
        }

        ImageUtil.imageVollbildZeichnen(applet, frame);
        lastFrame = frame;

        currentFrame += speed;
        if (currentFrame >= frames.length) {
            currentFrame = 0;
        }
    }

    public int getCurrentFrame() {
        return (int) currentFrame;
    }

    PApplet getApplet() {
        return applet;
    }

    PImage[] getFrames() {
        return frames;
    }
}
