package herdergames.schiffe_versenken;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

record Position(int zeile, int spalte) {
    static boolean isValid(int zeile, int spalte) {
        return zeile >= 0 && zeile < SpielBrett.SIZE && spalte >= 0 && spalte < SpielBrett.SIZE;
    }

    static Optional<Position> create(int zeile, int spalte) {
        return isValid(zeile, spalte) ? Optional.of(new Position(zeile, spalte)) : Optional.empty();
    }

    static Optional<Position> fromMausPosition(float xOffset, float yOffset, float feldSize) {
        int zeile = (int) Math.floor(yOffset / feldSize);
        int spalte = (int) Math.floor(xOffset / feldSize);
        return create(zeile, spalte);
    }

    Position {
        if (!isValid(zeile, spalte)) {
            throw new IllegalArgumentException();
        }
    }

    List<Position> getDirekteNachbarn() {
        List<Position> result = new ArrayList<>();

        for (int xVerschiebung = -1; xVerschiebung <= 1; xVerschiebung++) {
            for (int yVerschiebung = -1; yVerschiebung <= 1; yVerschiebung++) {
                if (xVerschiebung == 0 && yVerschiebung == 0) {
                    continue;
                }

                if (xVerschiebung != 0 && yVerschiebung != 0) {
                    continue;
                }

                Optional<Position> position = create(zeile + yVerschiebung, spalte + xVerschiebung);
                if (position.isEmpty()) {
                    continue;
                }

                result.add(position.get());
            }
        }

        return result;
    }
}