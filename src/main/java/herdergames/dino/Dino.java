package herdergames.dino;

import herdergames.spiel.MehrspielerSpiel;
import herdergames.spiel.Spieler;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static processing.core.PConstants.*;

public final class Dino extends MehrspielerSpiel {
    static PImage oinky;
    static PImage herbert;
    static PImage wolke;

    public static void init(PApplet applet) {
        oinky = applet.loadImage("dino/oinky.png");
        herbert = applet.loadImage("dino/herbert.png");
        wolke = applet.loadImage("dino/wolke.png");
    }

    int cPrimary = applet.color(18, 20, 5);
    int cPriB = applet.color(134, 209, 87);
    int cAccent = applet.color(87, 209);

    Player player1;
    Player player2;
    Player player3;
    Player player4;

    float[][] obstacle = new float[2][2];
    float[][] ground = new float [50][2];
    float speed = 10;
    int counter = 0;
    int lastSpeedBoost = 0;

    public Dino(PApplet applet, Set<Spieler> alleSpieler) {
        super(applet);
        player1 = new Player(applet.width/10, alleSpieler.stream().anyMatch(spieler -> spieler.id() == Spieler.Id.SPIELER_1));
        player2 = new Player(applet.width/10 + (applet.width/10) * 1, alleSpieler.stream().anyMatch(spieler -> spieler.id() == Spieler.Id.SPIELER_2));
        player3 = new Player(applet.width/10 + (applet.width/10) * 2, alleSpieler.stream().anyMatch(spieler -> spieler.id() == Spieler.Id.SPIELER_3));
        player4 = new Player(applet.width/10 + (applet.width/10) * 3, alleSpieler.stream().anyMatch(spieler -> spieler.id() == Spieler.Id.SPIELER_4));
        obstacle[0][0] = 0;
        obstacle[1][0] = applet.width * 0.5f;
    }

    @Override
    public Optional<List<Spieler.Id>> draw() {
        mainScreen();
        boden();
        obstacle();
        boden();

        if (!player1.alive && !player2.alive && !player3.alive && !player4.alive) {
            return Optional.of(List.of());

            // Hier ist das Spiel zuende lol
            // Wenn du das liest bist du Andreas oder Andreas hat vergessen diesen Kommentar zu löschen

            // Psst! Ich bin nicht Andreas. Ich bin sein Sklave, den er zwingt an Herder Games zu arbeiten. Bitte befreit mich!
        }

        return Optional.empty();
    }

    @Override
    public void keyPressed() {
        if (applet.key == 'w') {
            player1.moveUp();
        }

        if (applet.key == 't') {
            player2.moveUp();
        }

        if (applet.key == 'i') {
            player3.moveUp();
        }

        if (applet.keyCode == UP) {
            player4.moveUp();
        }

        if (applet.key == ' ') {
            speed++;
        }
    }

    void mainScreen() {
        applet.background(255);
        applet.image(wolke, ground[1][0], ground[1][1]-applet.height*0.8f, applet.width/8f, applet.height/8f);
        applet.image(wolke, ground[3][0], ground[3][1]-applet.height*0.8f, applet.width/8f, applet.height/8f);
        applet.image(wolke, ground[8][0], ground[8][1]-applet.height*0.8f, applet.width/8f, applet.height/8f);

        applet.textSize(applet.height*0.1f);
        applet.textAlign(CENTER);
        applet.fill(cPriB);
        applet.text("DINOGAME", applet.width*0.5f, applet.height*0.1f);
        info();

        player1.zeichnen();
        player2.zeichnen();
        player3.zeichnen();
        player4.zeichnen();
    }

    void info() {
        applet.textSize(applet.height*0.03f);
        applet.textAlign(CORNER);
        applet.fill(cPrimary);
        applet.text("DINOGAME", applet.width*0.05f, applet.height*0.2f);
        applet.text("Counter: " + counter, applet.width*0.05f, applet.height*0.25f);
        applet.text("Speed: " + speed, applet.width*0.05f, applet.height*0.3f);
    }

    void boden() {
        applet.rectMode(CORNERS);
        applet.fill(120, 120, 80);
        applet.rect(0, applet.height-applet.height/5f, applet.width, applet.height);
        applet.rectMode(CORNER);
        for (int i = 0; i < ground.length; i++) {
            ground[i][0] -= applet.height*speed*0.001f;
            if (ground[i][0] <= -applet.width/8f) {
                ground[i][0] = applet.width + applet.random(applet.width);
                ground[i][1] = applet.height - applet.random(applet.height/6f);
            }
            applet.fill(100);
            applet.rect(ground[i][0], ground[i][1], 10, 10);
        }
    }

    void obstacle() {
        for (int i = 0; i < obstacle.length; i++) {
            obstacle[i][0] -= applet.width * speed * 0.001f;
            if (obstacle[i][0] <= 0) {
                obstacle[i][0] = applet.width + applet.width*(i+1);
                obstacle[i][1] = applet.height-applet.height/5f - applet.height/applet.random(2, 3);
                counter++;
            }

            applet.rectMode(CORNER);
            applet.image(herbert, obstacle[i][0] - applet.height/16f, applet.height-obstacle[i][1], applet.height/4f, applet.height/4f);
            if (counter%3 == 0 && !(lastSpeedBoost == counter)) {
                lastSpeedBoost = counter;
                speed++;
            }
        }
    }

    class Player {
        boolean active;
        int x;
        boolean alive = true;
        int y = applet.height-applet.height/3;
        float speedY = 0;

        Player(int pX, boolean pActive) {
            x = pX;
            active = pActive;
        }

        void zeichnen() {
            // Damit die Spieler nicht ein wenig in den Boden kommen
            if (y > applet.height-applet.height/3) {
                y = applet.height - applet.height/3;
            }

            applet.rectMode(CORNER);
            if (alive) {
                applet.fill(cAccent);
            } else {
                applet.fill(cPrimary);
            }

            if (!alive) {
                applet.tint(40);
            }
            if (active) {
                applet.image(oinky, x, y, applet.height/7f, applet.height/7f);
            }
            applet.noTint();
            y += speedY;
            if (y < applet.height-applet.height/3) {
                speedY += 0.0025f*applet.height;
            } else {
                speedY = 0;
            }
            collision();
        }

        void moveUp() {
            if (!(y < applet.height-applet.height/3)) {
                speedY = -0.05f*applet.height;
            } else {
                y = applet.height - applet.height/3;
                speedY = 0;
            }
        }

        void collision() {
            for (int i = 0; i < obstacle.length; i++) {
                if (x + applet.height/7f > obstacle[i][0]) {
                    if (x < obstacle[i][0] + applet.height/8f) {
                        if (y + applet.height/7f > applet.height-obstacle[i][1]) {
                            alive = false;
                        }
                    }
                }
            }
        }
    }
}
