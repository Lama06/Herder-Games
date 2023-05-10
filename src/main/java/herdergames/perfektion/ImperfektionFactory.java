package herdergames.perfektion;

import processing.core.PApplet;

interface ImperfektionFactory {
    Imperfektion mitZufallswerten(PApplet applet, Form form);

    boolean istMitFormKompatibel(Form form);
}
