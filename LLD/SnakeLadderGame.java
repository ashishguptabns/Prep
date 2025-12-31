package LLD;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class SnakeLadderGame {
    public static void main(String[] args) {
        Board board = new Board(100);
        board.setCell(14, new SnakeCell(4));
        board.setCell(7, new LadderCell(31));

        try {
            runGame(board, 10);
        } catch (InterruptedException e) {
        }
    }

    static void runGame(Board board, int playerCount) throws InterruptedException {
        Thread[] players = new Thread[playerCount];
        java.util.Random dice = new java.util.Random();

        for (int i = 0; i < playerCount; i++) {
            final int id = i;
            players[i] = new Thread(() -> {
                int pos = 0;
                while (pos < 100) {
                    pos = board.movePlayer(id, dice.nextInt(6) + 1);
                    System.out.println("Player " + id + " at square " + pos);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
                System.out.println(">>> Player " + id + " WINS! <<<");
            });
            players[i].start();
        }
        for (Thread p : players) {
            p.join();
        } // Wait for all to finish
    }
}

interface Cell {
    int resolve(int current);
}

class NormalCell implements Cell {
    final int endPos;

    public NormalCell(int endPos) {
        this.endPos = endPos;
    }

    @Override
    public int resolve(int current) {
        return endPos;
    }
}

class SnakeCell implements Cell {
    final int endPos;

    public SnakeCell(int destination) {
        this.endPos = destination;
    }

    @Override
    public int resolve(int current) {
        return endPos;
    }
}

class LadderCell implements Cell {
    final int endPos;

    public LadderCell(int destination) {
        this.endPos = destination;
    }

    @Override
    public int resolve(int current) {
        return endPos;
    }
}

class Board {
    private final Cell[] cells;
    private final AtomicIntegerArray playerPositions;

    public Board(int size) {
        this.cells = new Cell[size + 1];
        for (int i = 0; i <= size; i++) {
            cells[i] = new NormalCell(i);
        } // Default: No jump
        this.playerPositions = new AtomicIntegerArray(1000); // Support 1000 players
    }

    public void setCell(int i, Cell cell) {
        cells[i] = cell;
    }

    public int movePlayer(int playerId, int roll) {
        int current, next;
        do {
            current = playerPositions.get(playerId);
            next = Math.min(current + roll, cells.length - 1);
            next = cells[next].resolve(current); // Apply snake/ladder logic
        } while (!playerPositions.compareAndSet(playerId, current, next));
        return next;
    }
}