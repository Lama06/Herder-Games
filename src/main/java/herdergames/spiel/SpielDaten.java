package herdergames.spiel;

import herdergames.baelle.Baelle;
import herdergames.break_out.BreakOut;
import herdergames.flappy_oinky.FlappyOinky;
import herdergames.harry_potter_quiz.HarryPotterQuiz;
import herdergames.latein.Latein;
import herdergames.pacman.PacmanSpiel;
import herdergames.perfektion.Perfektion;
import herdergames.rain_catcher.RainCatcher;
import herdergames.schach.Schach;
import herdergames.snake.Snake;
import herdergames.stapeln.Stapeln;
import herdergames.tetris.Tetris;
import processing.core.PApplet;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public record SpielDaten(String name, Spiel.Factory factory, SpielUebergang uebergang, Consumer<PApplet> init) {
    public static final List<SpielDaten> SPIELE = List.of(
            new SpielDaten(
                    "Dame",
                    (SpielerGegenSpielerSpiel.Factory) herdergames.dame.SpielerGegenSpielerSpiel::new,
                    SpielUebergang.UEBERGANG_1
            ),
            new SpielDaten(
                    "Dame AI",
                    (EinzelspielerSpiel.Factory) herdergames.dame.SpielerGegenAISpiel::new,
                    SpielUebergang.UEBERGANG_1
            ),

            new SpielDaten(
                    "Vier Gewinnt",
                    (SpielerGegenSpielerSpiel.Factory) herdergames.vier_gewinnt.SpielerGegenSpielerSpiel::new,
                    SpielUebergang.UEBERGANG_1
            ),
            new SpielDaten(
                    "Vier Gewinnt AI",
                    (EinzelspielerSpiel.Factory) herdergames.vier_gewinnt.SpielerGegenAISpiel::new,
                    SpielUebergang.UEBERGANG_1
            ),

            new SpielDaten(
                    "Schach",
                    (SpielerGegenSpielerSpiel.Factory) herdergames.schach.SpielerGegenSpielerSpiel::new,
                    SpielUebergang.UEBERGANG_1,
                    Schach::init
            ),
            new SpielDaten(
                    "Schach AI",
                    (EinzelspielerSpiel.Factory) herdergames.schach.SpielerGegenAISpiel::new,
                    SpielUebergang.UEBERGANG_1
            ),

            new SpielDaten(
                    "Tic Tac Toe",
                    (SpielerGegenSpielerSpiel.Factory) herdergames.tic_tac_toe.SpielerGegenSpielerSpiel::new,
                    SpielUebergang.UEBERGANG_1
            ),
            new SpielDaten(
                    "Tic Tac Toe AI",
                    (EinzelspielerSpiel.Factory) herdergames.tic_tac_toe.SpielerGegenAISpiel::new,
                    SpielUebergang.UEBERGANG_1
            ),

            new SpielDaten(
                    "Flappy Oinky",
                    (MehrspielerSpiel.Factory) FlappyOinky::new,
                    SpielUebergang.UEBERGANG_1,
                    FlappyOinky::init
            ),
            new SpielDaten(
                    "Bälle",
                    (MehrspielerSpiel.Factory) Baelle::new,
                    SpielUebergang.UEBERGANG_1
            ),
            new SpielDaten(
                    "Snake",
                    (MehrspielerSpiel.Factory) Snake::new,
                    SpielUebergang.UEBERGANG_1,
                    Snake::init
            ),
            new SpielDaten(
                    "Tetris",
                    (MehrspielerSpiel.Factory) Tetris::new,
                    SpielUebergang.UEBERGANG_1
            ),
            new SpielDaten(
                    "Stapeln",
                    (MehrspielerSpiel.Factory) Stapeln::new,
                    SpielUebergang.UEBERGANG_1
            ),
            new SpielDaten(
                    "Rain Catcher",
                    (MehrspielerSpiel.Factory) RainCatcher::new,
                    SpielUebergang.UEBERGANG_1,
                    RainCatcher::init
            ),
            new SpielDaten(
                    "Pacman",
                    (MehrspielerSpiel.Factory) PacmanSpiel::new,
                    SpielUebergang.UEBERGANG_1,
                    PacmanSpiel::init
            ),
            new SpielDaten(
                    "Harry Potter Trivia",
                    (MehrspielerSpiel.Factory) HarryPotterQuiz::new,
                    SpielUebergang.UEBERGANG_1,
                    HarryPotterQuiz::init
            ),
            new SpielDaten(
                    "Latein Formen",
                    (MehrspielerSpiel.Factory) Latein::new,
                    SpielUebergang.UEBERGANG_1,
                    Latein::init
            ),
            new SpielDaten(
                    "Breakout",
                    (MehrspielerSpiel.Factory) BreakOut::new,
                    SpielUebergang.UEBERGANG_1
            ),
            new SpielDaten(
                    "Perfektion",
                    (MehrspielerSpiel.Factory) Perfektion::new,
                    SpielUebergang.UEBERGANG_1
            )
    );

    public SpielDaten {
        Objects.requireNonNull(name);
        Objects.requireNonNull(factory);
        Objects.requireNonNull(uebergang);
        Objects.requireNonNull(init);
    }

    public SpielDaten(String name, Spiel.Factory factory, SpielUebergang uebergang) {
        this(name, factory, uebergang, applet -> { });
    }
}
