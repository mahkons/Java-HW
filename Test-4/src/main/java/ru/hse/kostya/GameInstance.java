package ru.hse.kostya;

import java.util.*;

public class GameInstance {

    private enum State {
        NO_BUTTON,
        ONE_BUTTON,
        SHOWING_BUTTONS,
        FINISH_GAME;
    }

    public enum CellState {
        OPENED,
        NOT_OPENED;
    }

    private State state = State.NO_BUTTON;
    private GameCell firstCell;
    private GameCell secondCell;

    private final int fieldSize;
    private final GameCell[][] board;
    private int opened;


    public String getStatus() {
        if (state == State.FINISH_GAME) {
            return "You won!";
        } else {
            return "You need to find " + (fieldSize * fieldSize - opened) / 2 + " more pairs";
        }
    }

    public GameInstance(int fieldSize) {
        if (fieldSize < 2 || fieldSize > 10) {
            throw new IllegalArgumentException("Field should be an integer between 2 and 10");
        }
        this.fieldSize = fieldSize;
        board = new GameCell[fieldSize][fieldSize];

        initializeCells();
    }

    public boolean likeToShow() {
        return state == State.SHOWING_BUTTONS || state == State.FINISH_GAME;
    }

    public boolean hasPair() {
        return state == State.ONE_BUTTON;
    }

    public int getPairX() {
        return firstCell.x;
    }

    public int getPairY() {
        return firstCell.y;
    }

    private boolean isCenter(int x, int y) {
        return fieldSize % 2 == 1 && x == fieldSize / 2 && y == fieldSize / 2;
    }

    private void initializeCells() {
        List<Integer> permutation = new ArrayList<>();
        for (int i = 1; i <= (fieldSize * fieldSize) / 2; i++) {
            permutation.add(i);
            permutation.add(i);
        }
        Collections.shuffle(permutation, new Random(System.currentTimeMillis()));

        for (int i = 0, pos = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (!isCenter(i, j)) {
                    board[i][j] = new GameCell(i, j, permutation.get(pos++));
                } else {
                    opened++;
                    board[i][j] = new GameCell(i, j, 0);
                    board[i][j].state = CellState.OPENED;
                }
            }
        }
    }

    private boolean gameFinished() {
        return opened == fieldSize * fieldSize;
    }

    public CellState getState(int x, int y) {
        if (fieldSize <= x || x < 0 || fieldSize <= y || y < 0) {
            throw new IllegalArgumentException("coordination are not in bounds");
        }
        return board[x][y].state;
    }

    public int getValue(int x, int y) {
        if (fieldSize <= x || x < 0 || fieldSize <= y || y < 0) {
            throw new IllegalArgumentException("coordination are not in bounds");
        }
        return board[x][y].value;
    }

    public boolean put(int x, int y) {

        if (fieldSize <= x || x < 0 || fieldSize <= y || y < 0) {
            throw new IllegalArgumentException("Wrong bounds");
        }
        if (board[x][y].state == CellState.OPENED) {
            return false;
        }

        switch (state) {
            case FINISH_GAME:
                return false;
            case SHOWING_BUTTONS:
                return false;
            case NO_BUTTON:
                firstCell = board[x][y];
                state = State.ONE_BUTTON;
                return true;
            case ONE_BUTTON:
                if (firstCell.isAtPosition(x, y)) {
                    return false;
                }

                secondCell = board[x][y];
                state = State.SHOWING_BUTTONS;

                firstCell.state = CellState.OPENED;
                secondCell.state = CellState.OPENED;

                if (firstCell.value != secondCell.value) {
                    firstCell.state = CellState.NOT_OPENED;
                    secondCell.state = CellState.NOT_OPENED;
                } else {
                    opened += 2;
                }
                state = State.SHOWING_BUTTONS;

                return true;
        }
        return false;
    }

    public void finishShowing() {
        if (state != State.SHOWING_BUTTONS) {
            return;
        }
        if (gameFinished()) {
            state = State.FINISH_GAME;
        } else {
            state = State.NO_BUTTON;
        }
    }

    private static class GameCell {
        private final int x;
        private final int y;
        
        private final int value;
        private CellState state;

        public GameCell(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
            state = CellState.NOT_OPENED;
        }

        public boolean isAtPosition(int x, int y) {
            return x == this.x && y == this.y;
        }

    }

}
