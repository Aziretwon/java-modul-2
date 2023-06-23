import java.util.*;

public class BattleshipGame {
    private static final int BOARD_SIZE = 8;
    private static final int SHIP_COUNT = 3;

    private char[][] board;
    private List<Ship> ships;
    private List<String> topScores;
    private long startTime;

    private static class Ship {
        private List<String> coordinates;

        public Ship() {
            coordinates = new ArrayList<>();
        }
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = '-';
            }
        }
    }

    private void placeShips() {
        Random random = new Random();
        for (int i = 0; i < SHIP_COUNT; i++) {
            Ship ship = new Ship();
            int size = i + 1;

            while (true) {
                int row = random.nextInt(BOARD_SIZE);
                int col = random.nextInt(BOARD_SIZE);

                boolean isValid = true;
                for (int j = 0; j < size; j++) {
                    if (col + j >= BOARD_SIZE || board[row][col + j] != '-') {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    for (int j = 0; j < size; j++) {
                        board[row][col + j] = 'S';
                        ship.coordinates.add(row + "" + (col + j));
                    }
                    ships.add(ship);
                    break;
                }
            }
        }
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private void markAdjacentCells(int row, int col) {
        int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};

        for (int i = 0; i < dx.length; i++) {
            int newRow = row + dx[i];
            int newCol = col + dy[i];

            if (isValidCell(newRow, newCol) && board[newRow][newCol] == '-') {
                board[newRow][newCol] = 'o';
            }
        }
    }

    private void printBoard() {
        System.out.println("\n  A B C D E F G H");
        System.out.println("  ----------------");
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void playGame() {
        Scanner scanner = new Scanner(System.in);
        int guesses = 0;
        startTime = System.currentTimeMillis();

        while (true) {
            System.out.print("\nКуда стреляем: ");
            String input = scanner.nextLine().trim().toUpperCase();
             if (input.length() != 2) {
                System.out.println("Неверный формат ввода! Введите букву и цифру.");
                continue;
            }

            char columnChar = input.charAt(0);
            char rowChar = input.charAt(1);

            if (columnChar < 'A' || columnChar > 'H' || rowChar < '1' || rowChar > '8') {
                System.out.println("Неверные координаты! Попробуйте снова.");
                continue;
            }

            int row = rowChar - '1';
            int col = columnChar - 'A';

            if (board[row][col] == 'X' || board[row][col] == 'U' || board[row][col] == 'o') {
                System.out.println("Вы уже стреляли в эту ячейку! Попробуйте снова.");
                continue;
            }

            guesses++;

            if (board[row][col] == 'S') {
                System.out.println("Вы попали!");
                board[row][col] = 'U';
                Ship destroyedShip = null;

                for (Ship ship : ships) {
                    if (ship.coordinates.contains(row + "" + col)) {
                        ship.coordinates.remove(row + "" + col);
                        if (ship.coordinates.isEmpty()) {
                            destroyedShip = ship;
                        }
                        break;
                    }
                }

                if (destroyedShip != null) {
                    System.out.println("Корабль уничтожен!");
                    for (String coord : destroyedShip.coordinates) {
                        int shipRow = coord.charAt(0) - '0';
                        int shipCol = coord.charAt(1) - '0';
                        board[shipRow][shipCol] = 'X';
                        markAdjacentCells(shipRow, shipCol);
                    }
                    ships.remove(destroyedShip);
                }
            } else {
                System.out.println("Промах!");
                board[row][col] = 'o';
            }

            printBoard();

            if (checkGameOver()) {
                long endTime = System.currentTimeMillis();
                double gameTime = (endTime - startTime) / 1000.0;
                System.out.println("\nПоздравляем! Вы победили!");
                System.out.println("Время игры: " + String.format("%.2f", gameTime) + " секунд");
                addScore(gameTime);
                break;
            }

            if (guesses >= 15) {
                System.out.println("\nСлишком долго! Вы проиграли.");
                break;
            }
        }
    }

    private boolean checkGameOver() {
        return ships.isEmpty();
    }

    private void addScore(double time) {
        if (topScores.size() < 3) {
            topScores.add(String.format("%.2f", time));
            topScores.sort(Comparator.comparingDouble(Double::parseDouble));
        } else {
            double minTime = Double.parseDouble(topScores.get(0));
            if (time < minTime) {
                topScores.remove(0);
                topScores.add(String.format("%.2f", time));
                topScores.sort(Comparator.comparingDouble(Double::parseDouble));
            }
        }
    }

    private void printTopScores() {
        System.out.println("\nТоп 3 самых быстрых игр:");
         if (topScores.isEmpty()) {
            System.out.println("Нет сохраненных результатов.");
        } else {
            for (int i = 0; i < topScores.size(); i++) {
                System.out.println((i + 1) + ". " + topScores.get(i) + " сек");
            }
        }
    }

    private void startGame() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nМЕНЮ:");
            System.out.println("1. Новая игра");
            System.out.println("2. Результаты");
            System.out.println("3. Выход");
            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\nНовая игра");
                    board = new char[BOARD_SIZE][BOARD_SIZE];
                    ships = new ArrayList<>();
                    initializeBoard();
                    placeShips();
                    printBoard();
                    playGame();
                    break;
                case 2:
                    System.out.println("\nРезультаты");
                    printTopScores();
                    break;
                case 3:
                    System.out.println("\nВыход");
                    exit = true;
                    break;
                default:
                    System.out.println("\nНеверный выбор. Попробуйте снова.");
                    break;
            }
        }
    }

    public void run() {
        topScores = new ArrayList<>();
        startGame();
    }

    public static void main(String[] args) {
        BattleshipGame game = new BattleshipGame();
        game.run();
    }
}