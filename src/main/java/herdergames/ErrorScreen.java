package herdergames;

import processing.core.PConstants;

import java.io.PrintWriter;
import java.io.StringWriter;

final class ErrorScreen extends Screen {
    private static final String TITLE_TEXT = "Fehler";
    private static final float TTITLE_Y = 0.02f;
    private static final float TITLE_TEXT_SIZE = 0.04f;

    private static final String DESCRIPTION_TEXT = "Bitte melden! Zurück zum Hauptmenü mit Space.";
    private static final float DESCRIPTION_Y = TTITLE_Y + TITLE_TEXT_SIZE + 0.02f;
    private static final float DESCRIPTION_TEXT_SIZE = 0.03f;

    private static final float ERR_MSG_Y = DESCRIPTION_Y + DESCRIPTION_TEXT_SIZE + 0.03f;
    private static final float ERR_MSG_TEXT_SIZE = 0.02f;

    private final Throwable error;

    ErrorScreen(HerderGames herderGames, Throwable error) {
        super(herderGames);
        this.error = error;
    }

    @Override
    void draw() {
        applet.background(255);
        applet.textAlign(PConstants.CENTER, PConstants.TOP);
        applet.fill(0);

        applet.textSize(TITLE_TEXT_SIZE * applet.height);
        applet.text(TITLE_TEXT, applet.width / 2f, TTITLE_Y * applet.height);

        applet.textSize(DESCRIPTION_TEXT_SIZE * applet.height);
        applet.text(DESCRIPTION_TEXT, applet.width / 2f, DESCRIPTION_Y * applet.height);

        applet.textSize(ERR_MSG_TEXT_SIZE * applet.height);
        applet.textLeading(ERR_MSG_TEXT_SIZE * applet.height);
        applet.text(getErrorMessage(), applet.width / 2f, ERR_MSG_Y * applet.height);
    }

    @Override
    void keyPressed() {
        if (applet.key != ' ') {
            return;
        }

        herderGames.openScreen(new SpielAuswahlScreen(herderGames, 0));
    }

    private String getErrorMessage() {
        StringWriter message = new StringWriter();
        error.printStackTrace(new PrintWriter(message));
        return message.toString();
    }
}
