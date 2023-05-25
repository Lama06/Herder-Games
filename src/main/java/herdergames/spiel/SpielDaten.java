package herdergames.spiel;

import herdergames.ampel.AmpelSpiel;
import herdergames.baelle.Baelle;
import herdergames.bowling.Bowling;
import herdergames.break_out.BreakOut;
import herdergames.cookie_clicker.CookieClicker;
import herdergames.crossy_road.CrossyRoad;
import herdergames.oinky_run.OinkyRun;
import herdergames.flappy_oinky.FlappyOinky;
import herdergames.hangman.Hangman;
import herdergames.harry_potter_quiz.HarryPotterQuiz;
import herdergames.kirschbaeume.KirschbaumSpiel;
import herdergames.latein.Latein;
import herdergames.minesweeper.Minesweeper;
import herdergames.pacman.PacmanSpiel;
import herdergames.perfektion.Perfektion;
import herdergames.pong.Pong;
import herdergames.rain_catcher.RainCatcher;
import herdergames.reaktions_zeit.ReaktionsZeit;
import herdergames.schach.Schach;
import herdergames.schiffe_versenken.SchiffeVersenken;
import herdergames.snake.Snake;
import herdergames.stapeln.Stapeln;
import herdergames.tetris.Tetris;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public record SpielDaten(String name, Spiel.Factory factory, SpielUebergang uebergang, Consumer<PApplet> init) {
    // Muss mutable sein, weil Spiele entfernt werden, die nicht initialisiert werden können
    public static final List<SpielDaten> SPIELE = new ArrayList<>(List.of(
            new SpielDaten(
                    "Dame",
                    (SpielerGegenSpielerSpiel.Factory) herdergames.dame.SpielerGegenSpielerSpiel::new,
                    SpielUebergang.B008
            ),
            new SpielDaten(
                    "Dame AI",
                    (EinzelspielerSpiel.Factory) herdergames.dame.SpielerGegenAISpiel::new,
                    SpielUebergang.B008
            ),

            new SpielDaten(
                    "Vier Gewinnt",
                    (SpielerGegenSpielerSpiel.Factory) herdergames.vier_gewinnt.SpielerGegenSpielerSpiel::new,
                    SpielUebergang.B107
            ),
            new SpielDaten(
                    "Vier Gewinnt AI",
                    (EinzelspielerSpiel.Factory) herdergames.vier_gewinnt.SpielerGegenAISpiel::new,
                    SpielUebergang.B107
            ),

            new SpielDaten(
                    "Schach",
                    (SpielerGegenSpielerSpiel.Factory) herdergames.schach.SpielerGegenSpielerSpiel::new,
                    SpielUebergang.E202,
                    Schach::init
            ),
            new SpielDaten(
                    "Schach AI",
                    (EinzelspielerSpiel.Factory) herdergames.schach.SpielerGegenAISpiel::new,
                    SpielUebergang.E202
            ),

            new SpielDaten(
                    "Tic Tac Toe",
                    (SpielerGegenSpielerSpiel.Factory) herdergames.tic_tac_toe.SpielerGegenSpielerSpiel::new,
                    SpielUebergang.E901
            ),
            new SpielDaten(
                    "Tic Tac Toe AI",
                    (EinzelspielerSpiel.Factory) herdergames.tic_tac_toe.SpielerGegenAISpiel::new,
                    SpielUebergang.E901
            ),

            new SpielDaten(
                    "Flappy Oinky",
                    (MehrspielerSpiel.Factory) FlappyOinky::new,
                    SpielUebergang.F106,
                    FlappyOinky::init
            ),
            new SpielDaten(
                    "Bälle",
                    (MehrspielerSpiel.Factory) Baelle::new,
                    SpielUebergang.F109
            ),
            new SpielDaten(
                    "Snake",
                    (MehrspielerSpiel.Factory) Snake::new,
                    SpielUebergang.BANK,
                    Snake::init
            ),
            new SpielDaten(
                    "Tetris",
                    (MehrspielerSpiel.Factory) Tetris::new,
                    SpielUebergang.B008
            ),
            new SpielDaten(
                    "Stapeln",
                    (MehrspielerSpiel.Factory) Stapeln::new,
                    SpielUebergang.B107
            ),
            new SpielDaten(
                    "Rain Catcher",
                    (MehrspielerSpiel.Factory) RainCatcher::new,
                    SpielUebergang.E202,
                    RainCatcher::init
            ),
            new SpielDaten(
                    "Pacman",
                    (MehrspielerSpiel.Factory) PacmanSpiel::new,
                    SpielUebergang.E901,
                    PacmanSpiel::init
            ),
            new SpielDaten(
                    "Harry Potter Trivia",
                    (MehrspielerSpiel.Factory) HarryPotterQuiz::new,
                    SpielUebergang.F106,
                    HarryPotterQuiz::init
            ),
            new SpielDaten(
                    "Latein Formen",
                    (MehrspielerSpiel.Factory) Latein::new,
                    SpielUebergang.F109,
                    Latein::init
            ),
            new SpielDaten(
                    "Breakout",
                    (MehrspielerSpiel.Factory) BreakOut::new,
                    SpielUebergang.BANK
            ),
            new SpielDaten(
                    "Perfektion",
                    (MehrspielerSpiel.Factory) Perfektion::new,
                    SpielUebergang.B008
            ),
            new SpielDaten(
                    "Schiffe Versenken",
                    (SpielerGegenSpielerSpiel.Factory) SchiffeVersenken::new,
                    SpielUebergang.B107
            ),
            new SpielDaten(
                    "Pong",
                    (SpielerGegenSpielerSpiel.Factory) Pong::new,
                    SpielUebergang.E202
            ),
            new SpielDaten(
                    "Kirschbäume",
                    (EinzelspielerSpiel.Factory) KirschbaumSpiel::new,
                    SpielUebergang.E901,
                    KirschbaumSpiel::init
            ),
            new SpielDaten(
                    "Crossy Road",
                    (MehrspielerSpiel.Factory) CrossyRoad::new,
                    SpielUebergang.F106
            ),
            new SpielDaten(
                    "Cookie Clicker",
                    (EinzelspielerSpiel.Factory) CookieClicker::new,
                    SpielUebergang.F109,
                    CookieClicker::init
            ),
            new SpielDaten(
                    "Bowling",
                    (MehrspielerSpiel.Factory) Bowling::new,
                    SpielUebergang.BANK,
                    Bowling::init
            ),
            new SpielDaten(
                    "Ampel",
                    (MehrspielerSpiel.Factory) AmpelSpiel::new,
                    SpielUebergang.E202,
                    AmpelSpiel::init
            ),
            new SpielDaten(
                    "Hangman",
                    (EinzelspielerSpiel.Factory) Hangman::new,
                    SpielUebergang.B008
            ),
            new SpielDaten(
                    "Reaktions-Zeit",
                    (MehrspielerSpiel.Factory) ReaktionsZeit::new,
                    SpielUebergang.E901,
                    ReaktionsZeit::init
            ),
            new SpielDaten(
                    "Minesweeper",
                    (EinzelspielerSpiel.Factory) Minesweeper::new,
                    SpielUebergang.F106
            ),
            new SpielDaten(
                    "Oinky Run",
                    (MehrspielerSpiel.Factory) OinkyRun::new,
                    SpielUebergang.E202,
                    OinkyRun::init
            )
    ));

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
