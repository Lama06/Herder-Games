# Dokumentation

Ich war in HerderGames für diese Spiele zuständig: Dame, Schach, Vier Gewinnt, Tic Tac Toe, Bälle, Flappy Oinky, Pacman,
Snake, Tetris, Stapeln, Latein Formen, Rain Catcher, Harry Potter Quiz, Bälle, Perfektion.
Ich habe auch das Hauptmenü verwaltet.
Außerdem habe ich die folgenden Klassen verwaltet, die jeweils von mehreren anderen Spielen benutzt werden:
- AI
- Steuerung
- Kreis
- Rechteck
- Spiel
- GewichteteListe

Ich werde in meiner Dokumentation nicht auf alles, sondern nur auf die spannendsten Dinge eingehen.

## Videos
Im Hauptmenü werden die Animationen von Thomas als Videos abgespielt. In Processing gibt es standardmäßig keine Möglichkeit,
um Videos abzuspielen.

Es gibt zwar eine externe, mit Processing kompatibele Bibliothek, die Videos abspielen kann. Aus mehreren Gründen habe
ich mich aber dagegen entschieden, diese zu verwenden:
- Die Bibliothek ist über einen halben Gigabyte groß, sowohl im exportierten Projekt als auch wenn das Spiel läuft.
- Die Bibliothek ist nur zum Teil in Java geschrieben, und ruft im Hintergrund nativen C++ Code
  - Deswegen können die Videos nicht vom Garbage Collector gelöscht werden, wenn zum Beispiel die Animation zu Ende ist,
    sondern müssen manuell mit dem Aufruf einer Methode aus dem Speicher gelöscht werden, was natürlich sehr fehleranfällig
    und unsicher ist.
- Die Bibliothek kann Videos nicht rückwärts abspielen, was essentziel für unser Spiel ist

Statt diese Bibliothek zu verwenden, sind die Videos als einzelne Frames gespeichert, die nacheinander angezeigt werden.

## Aufbau des Projekts
Es gibt 3 abstrakte Klassen, von denen jedes Spiel genau eine vererbt: EinzelspielerSpiel, SpielerGegenSpielerSpiel und MehrspielerSpiel.
Jedes Spiel ist ein eigenes Package zum Beispiel herdergames.schach, damit das Projekt übersichtlicher ist. 
Das Hauptmenü befindet sich im Package herdergames. Außerdem gibt es ein paar Packages, wie herdergames.ai oder herdergames.util, die Klassen
enthalten, die von mehreren anderen Spielen verwendet werden.

## AI
In HerderGames gibt es 4 Spiele, die man gegen eine AI spielen kann: Dame, Schach, VierGewinnt und TicTacToe.
Alle diese Spiele teilen sich den Code für die AI über die Datei AI.java. Diese Datei definiert mehrere Interfaces,
die von jedem der Spiele mit Klassen implementiert werden. Spiele mit AI müssen beispielsweise Methoden definieren,
die die möglichen Züge eines Spielers berechnen oder ein Spielbrett aus Sicht eines Spielers bewerten.
Wenn diese Interfaces implementiert sind, kann die Methode AI.bestenNaechstenZugBerechnen verwendet werden.

Der Algorithmus, der von der Datei AI.java implementiert wird und den alle Spiele mit AI nutzen, ist der Minimax-Algorithmus,
der üblicherweise für AIs für Spiele beutzt wird, bei denen 2 Spieler abwechselnd Züge machen. Ein Vorteil dieses
Algorithmuses ist, dass nicht der komplette Spielbaum (also alle möglichen Spielabläufe) genieriert werden muss.
Der Spielbaum wird nur bis zu einer bestimmten Tiefe berechnet und bei dieser Tiefe wird dann durch eine Schätzfunktion
bestimmt, wie nahe man einem Gewinn ist. Bei Dame oder Schach bedeutet das zum Beispiel, die Steine zu zählen.

Der Minimax-Algorithmus funktioniert so:
- Der Spielbaum wird bis zu einer bestimmten Tiefe generiert
- Auf der untersten Ebene des Spielbaumes werden die Spielbretter durch eine Bewertungsfunktion statisch bewertet:
  - Das heißt zum Beispiel, dass in Dame Steine des Spielers und Gegners oder in VierGewinnt fast fertige Viererreihen gezählt werden.
  - Diese Bewertung ist zwar nicht perfekt und nur eine Schätzung, dafür aber viel einfacher zu berechnen
- Jetzt wird der Spielbaum von der vorletzten bis zur obersten Reihe abgegangen:
  - Bei jedem Spielbrett wird davon ausgegangen, dass der Spieler, der am Zug ist, den für ihn bestmöglichen Zug macht.
  - Die Bewertung eines Spielbrettes ist also die des für den Spieler, der am Zug ist, besten folgenden Zuges.
- Wenn man am oben im Spielbaum angekommen ist, kann der folgende Zug mit der besten Bewertung als bester möglicher Zug verwendet werden.

Meine erste Implementation dieses Algorithmuses war nicht perfekt. Ich habe damals nämlich tatsächlich den Spielbaum bis zu einer
bestimmten Tiefe in einer Liste gespeichert und darauf die Operationen wie oben beschrieben durchgeführt.
Diese Implementation habe ich bevor wir mit Herder Games angefangen haben in den Sprachen Go und Rust geschrieben:
- https://github.com/Lama06/Dame-Go/blob/main/ai/ai.go
- https://github.com/Lama06/Dame/blob/main/src/ai/mod.rs

In HerderGames allerdings benutze ich eine rukursive Funktion, die dasselbe macht, aber deutlich weniger RAM gleichzeitig verwendet.
Die Ramverwendung vorher war je nach Tiefe des Spielbaumes bis zu 10 GB, jetzt wird zwar immer noch so viel Ram
allocated, der Garbage Collector kann das verwendet Ram aber während der Algorithmus noch läuft wieder freigeben
und das Program braucht daher nicht so viel Ram auf einmal.

## Datenstrukturen

Ich verwende fast keine Arrays, denn Arrays sind sehr unflexibel, weil sie nicht ihre Länge ändern können und es unmöglich
ist, ein Array einzufrieren, als weitere Änderungen zu verbieten.
Stattdessen verwende ich Listen (also List) und deren Implementierung: ArrayList. Eine ArrayList ist im Hintergrund quasi
ein flexibeles Array, mit dem Unterschied, dass die Kapazizät des Arrays, wenn nötig, erhöht wird und nicht einmalig
auf einen sehr hohen Wert gesetzt wird.

Ich benutzte außerdem oft die Datenstruktur Map und ihre Implementierung HashMap, die es ermöglicht,
Keys Werte zuzuordnen.

## Optional
In habe an fast keiner Stelle in meinen Klassen null verwendet, weil das Verwenden von null sehr schnell, wenn man nicht
ganz genau aufpasst, zum Crashen des Codes führen kann (wenn man nämlich irgendwas mit einem Objekt macht das null ist).
Stattdessen verwende ich die Klasse Optional, die mit Java 8 hinzugefügt wurde. So ist es sehr klar, wann ein Wert
nicht vorhanden sein kann, und unmöglich das zu vergessen.

## Lambdas und Streams
Ich habe an einigen Stellen in meinen Klassen ein relativ neues Java-Feature verwendet, das den Code sehr viel kürzer und lesbarer
macht.

Als beispiel hier ein Code aus dem Latein Formen Spiel, wie er ohne Streams in klassischem Java aussehen würde:

```
Set<NomenForm> result = new HashSet<>();
for (Numerus numerus : Numerus.values()) {
    for (Kasus kasus : Kasus.values()) {
        NomenForm form = new NomenForm(numerus, kasus);
        if (!form.zuWort(aktuellesNomen, aktuellesAdjektiv).equals(getAktuellesWort())) {
            continue;
        }
        result.add(form);
    }
}
return result;
```

Und wie derselbe Code mit Streams aussieht:

```
Arrays.stream(Numerus.values())
      .flatMap(numerus -> Arrays.stream(Kasus.values()).map(kasus -> new NomenForm(numerus, kasus)))
      .filter(form -> form.zuWort(aktuellesNomen, aktuellesAdjektiv).equals(getAktuellesWort()))
      .collect(Collectors.toUnmodifiableSet())
```

## Unit Tests
Weil der Code von Herder Games einfach so lang ist, habe ich für einige kompilizierte Teile des Codes Unit-Tests erstellt.
Ein Unit-Test ist Code, der automatisch ausgeführt wird und automatisiert testet, ob alles noch funktioniert.
Das ist zum Beispiel praktisch, um nach einer Änderung zu wissen, ob man versehentlich irgendwas kaputt gemacht hat.
Ich benutze die Unit-Testing Libary JUnit 5, die der Standard für Java ist.

Hier ein Beispiel für einen Unit-Test, der die e-Deklination im Latein Formen Spiel anhand des Wortes res testet:

```
@Test
void testEDeklination() {
    testDeklination(
            Set.of(
                    new NomenWoerterbuchEintrag("res", Optional.of("rei"), Optional.empty()),
                    new NomenWoerterbuchEintrag("res", Optional.of("rei"), Optional.of(Genus.FEMININUM))
            ),
            Genus.FEMININUM,
            Map.of(
                    Numerus.SINGULAR, Map.of(
                            Kasus.NOMINATIV, "res",
                            Kasus.GENITIV, "rei",
                            Kasus.DATIV, "rei",
                            Kasus.AKKUSATIV, "rem",
                            Kasus.ABLATIV, "re",
                            Kasus.VOKATIV, "res"
                    ),
                    Numerus.PLURAL, Map.of(
                            Kasus.NOMINATIV, "res",
                            Kasus.GENITIV, "rerum",
                            Kasus.DATIV, "rebus",
                            Kasus.AKKUSATIV, "res",
                            Kasus.ABLATIV, "rebus",
                            Kasus.VOKATIV, "res"
                    )
            )
    );
}
```

## Intellij-Idea
Weil unser Code über 10.000 Zeilen lang ist und hunderte Klassen, Interfaces, Records und Enums erstellt, war es nicht mehr
möglich, das Ganze in der Processing IDE zu schreiben, denn in der Processing IDE kann man zum Beispiel noch nicht einmal
packages erstellen. Deswegen waren die Dateien teilweise über 1600 Zeilen lang.
Dann haben wir uns dazu entschieden, zu einer richtigen Ide zu wechseln, nämlich Intellij Idea: https://www.jetbrains.com/de-de/idea/.
Diese Ide ist zwar im Vergleich zur Processing IDE deutlich komplizierter, aber auch viel, viel besser.
