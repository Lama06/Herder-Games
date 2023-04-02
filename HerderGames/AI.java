import java.util.*;

// https://www.youtube.com/watch?v=l-hh51ncgDI

final class AI {
    interface Brett<Self extends Brett<Self, Z, S>, Z extends Zug<Self>, S extends Spieler<S>> {
        Set<Z> getPossibleZuegeForSpieler(S spieler);

        int getBewertung(S perspektive);
    }

    interface Zug<B extends Brett<B, ?, ?>> {
        B getResult();
    }

    interface Spieler<Self extends Spieler<Self>> {
        Self getGegner();
    }

    private static class Node<B extends Brett<B, Z, S>, Z extends Zug<B>, S extends Spieler<S>> {
        private int depth;
        private Z zug;
        private B brett;
        private S spielerAmZug;
        private int bewertung;
        private Node<B, Z, S> bestChild;
        private Set<Node<B, Z, S>> children;
    }

    static <B extends Brett<B, Z, S>, Z extends Zug<B>, S extends Spieler<S>> Z calculateBestZug(B brett, S spieler, int maxDepth) {
        List<Node<B, Z, S>> nodes = new ArrayList<>();

        Node<B, Z, S> startNode = new Node<>();
        startNode.depth = 0;
        startNode.zug = null;
        startNode.brett = brett;
        startNode.spielerAmZug = spieler;
        startNode.bewertung = 0;
        startNode.bestChild = null;
        startNode.children = new HashSet<>();
        nodes.add(startNode);

        // Mögliche Züge generieren
        for (int depth = 1; depth <= maxDepth; depth++) {

            // Hier Zählvariabele verwenden, sonst ConcurrentModificationException
            for (int nodeIndex = 0; nodeIndex < nodes.size(); nodeIndex++) {
                Node<B, Z, S> parent = nodes.get(nodeIndex);
                if (parent.depth != depth-1) {
                    continue;
                }

                Set<Z> possibleZuege = parent.brett.getPossibleZuegeForSpieler(parent.spielerAmZug);
                for (Z possibleZug : possibleZuege) {
                    Node<B, Z, S> child = new Node<>();
                    child.depth = depth;
                    child.zug = possibleZug;
                    child.spielerAmZug = parent.spielerAmZug.getGegner();
                    child.bewertung = 0;
                    child.brett = possibleZug.getResult();
                    child.bestChild = null;
                    child.children = new HashSet<>();

                    parent.children.add(child);
                    nodes.add(child);
                }
            }
        }

        // Bewertungen der untersten Zeile berechnen
        for (Node<B, Z, S> node : nodes) {
            if (node.depth != maxDepth) {
                continue;
            }

            node.bewertung = node.brett.getBewertung(spieler);
        }

        // Bewertungen in den Zeilen darüber berechnen
        for (int depth = maxDepth - 1; depth >= 0; depth--) {
            for (Node<B, Z, S> node : nodes) {
                if (node.depth != depth) {
                    continue;
                }

                if (node.children.isEmpty()) {
                    node.bewertung = node.brett.getBewertung(spieler);
                    continue;
                }

                Node<B, Z, S> bestChild = null;
                for (Node<B, Z, S> child : node.children) {
                    if (bestChild == null) {
                        bestChild = child;
                        continue;
                    }

                    if (node.spielerAmZug == spieler && child.bewertung > bestChild.bewertung) {
                        bestChild = child;
                    } else if (node.spielerAmZug == spieler.getGegner() && child.bewertung < bestChild.bewertung) {
                        bestChild = child;
                    }
                }

                node.bestChild = bestChild;
                node.bewertung = bestChild.bewertung;
            }
        }

        return nodes.get(0).bestChild.zug;
    }
}
