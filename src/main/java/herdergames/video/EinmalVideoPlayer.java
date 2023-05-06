package herdergames.video;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public final class EinmalVideoPlayer {
    private final PApplet applet;
    private final float speed;
    private final int destination;
    private final PImage[] frames;
    private float currentFrame;
    private PImage lastFrame;
    private boolean finished;

    public EinmalVideoPlayer(PApplet applet, Video video, float speed, int startFrame, int destination) {
        if (startFrame < 0 || startFrame >= video.frames()) {
            throw new IllegalArgumentException();
        }

        if (destination < 0 || destination >= video.frames()) {
            throw new IllegalArgumentException();
        }

        this.applet = applet;
        this.speed = speed;
        this.destination = destination;

        frames = new PImage[video.frames()];

        if (startFrame < destination) {
            for (int frame = startFrame; frame <= destination; frame++) {
                frames[frame] = applet.requestImage(video.getFrameFileName(frame));
            }
        } else {
            for (int frame = startFrame; frame >= destination; frame--) {
                frames[frame] = applet.requestImage(video.getFrameFileName(frame));
            }
        }

        currentFrame = startFrame;
    }

    public EinmalVideoPlayer(PApplet applet, Video video, float speed) {
        this(applet, video, speed, 0, video.frames() - 1);
    }

    public EinmalVideoPlayer(LoopVideoPlayer loopVideoPlayer, float speed, int destination) {
        frames = loopVideoPlayer.getFrames();

        if (destination < 0 || destination >= frames.length) {
            throw new IllegalArgumentException();
        }

        applet = loopVideoPlayer.getApplet();
        this.speed = speed;
        this.destination = destination;
        currentFrame = loopVideoPlayer.getCurrentFrame();
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

        if (finished) {
            return;
        }

        if (currentFrame < destination) {
            currentFrame += speed;
            if (currentFrame >= destination) {
                currentFrame = destination;
                finished = true;
            }
        } else if (currentFrame > destination) {
            currentFrame -= speed;
            if (currentFrame <= destination) {
                currentFrame = destination;
                finished = true;
            }
        } else if (currentFrame == destination) {
            finished = true;
        }
    }

    public boolean istFertig() {
        return finished;
    }
}
