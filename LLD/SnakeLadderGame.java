package LLD;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class SnakeLadderGame {
    public static void main(String[] args) {
        Board board = new Board(100);
        board.setJump(14, 4); // Snake
        board.setJump(7, 31); // Ladder
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

class Board {
    private final int[] cells;
    private final AtomicIntegerArray playerPositions;

    public Board(int size) {
        this.cells = new int[size + 1];
        for (int i = 0; i <= size; i++) {
            cells[i] = i;
        } // Default: No jump
        this.playerPositions = new AtomicIntegerArray(1000); // Support 1000 players
    }

    public void setJump(int start, int end) {
        cells[start] = end;
    }

    public int movePlayer(int playerId, int roll) {
        int current, next;
        do {
            current = playerPositions.get(playerId);
            next = Math.min(current + roll, cells.length - 1);
            next = cells[next]; // Apply snake/ladder logic
        } while (!playerPositions.compareAndSet(playerId, current, next));
        return next;
    }
}