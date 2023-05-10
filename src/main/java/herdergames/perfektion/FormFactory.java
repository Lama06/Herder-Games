package herdergames.perfektion;

import processing.core.PApplet;

interface FormFactory {
    Form mitZufallswerten(PApplet applet, float x, float y, float breite, float hoehe);

    Form kopieVon(Form andereForm, PApplet applet, float x, float y, float breite, float hoehe);
}
