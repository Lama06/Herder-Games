package herdergames.video;

import processing.core.PApplet;
import processing.core.PConstants;
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
            frames[frame] = applet.requestImage(video.getFramePath(applet, frame));
        }
        for (int frame = 0; frame < startFrame; frame++) {
            frames[frame] = applet.requestImage(video.getFramePath(applet, frame));
        }

        currentFrame = startFrame;
    }

    public void draw() {
        applet.imageMode(PConstants.CORNER);

        PImage frame = frames[(int) currentFrame];
        if (frame.width == 0) {
            if (lastFrame == null) {
                return;
            }
            applet.image(lastFrame, 0, 0, applet.width, applet.height);
            return;
        }

        applet.image(frame, 0, 0, applet.width, applet.height);
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
