import java.util.Arrays;
import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;
/* EXTRA CREDIT:
 * 1) There's a score of how many guesses you're currently at
 *    out of total guesses (check makeScene)
 * 3) Displays a time by seconds (check onTick and makeScene)
 * 2) Makes the waterfall collapse inwards for onTick
 *    (need to take out comments to work)
 */

// Top left is where 0,0 is

// constructor
/* For 2x2 order in FloodIt:
 *  1 2
 *  3 4
 *  
 * Naming system for board:
 *  00 01
 *  10 11
 */

// Represents a single square of the game area
class Cell {
  // The constant size of the cell
  int cellSize = 20;

  // In logical coordinates, with the origin at the top-left corner of the screen
  int row;
  int col;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // constructor
  Cell(int row, int col, Color color) {
    this.row = row;
    this.col = col;
    this.color = color;
    this.flooded = false;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  // changes the cells color
  void changeColor(Color color) {
    this.color = color;
  }

  // draws a single cell
  WorldImage drawCell() {
    return new RectangleImage(this.cellSize, this.cellSize, "solid", this.color);
  }

  // checks if this cell is flooded
  boolean isFlooded() {
    return this.flooded;
  }

  // checks if this Cell has the same color as the given color
  boolean sameColor(Color given) {
    return this.color == given;
  }

  // EFFECT: sets the Cell's left, top, right, or down to a cell
  void setPosition(Cell cell, String position) {
    if (position.equals("left")) {
      this.left = cell;
    }
    else if (position.equals("top")) {
      this.top = cell;
    }
    else if (position.equals("right")) {
      this.right = cell;
    }
    else if (position.equals("bottom")) {
      this.bottom = cell;
    }
    else {
      return;
    }
  }

  // changes the flooded to true
  void trueFlooded() {
    this.flooded = true;
  }
}

// Represents the Flood It Game
class FloodIt extends World {
  // The 6 colors
  ArrayList<Color> colorList = new ArrayList<Color>(
      Arrays.asList(Color.red, Color.orange, Color.yellow, 
          Color.green, Color.blue, Color.black));

  // All the cells of the game
  ArrayList<ArrayList<Cell>> board;

  // holds the dimensions of the game
  int dimension;

  // holds the current move of the current
  int currentMove;

  // holds the max amount of moves
  int totalMoves;

  // holds the minutes the game has been
  int minutes;

  // holds the seconds the game has been
  int seconds;

  // holds how many onticks have occurred during that time period
  int onTicks;

  // constructor where given the dimension, it makes the array and links them
  FloodIt(int dimension) {
    if (dimension < 2) {
      throw new IllegalArgumentException("Invalid number of dimensions!");
    }
    else {
      this.dimension = dimension;
      this.board = new ArrayList<ArrayList<Cell>>(); // sets the board to nothing
      // creates the cells
      this.createCellBoards(dimension, new Random());
      // links the cells
      this.linkCellBoard(this.board);
      this.findCell(0, 0).flooded = true;
      this.currentMove = 0;
      this.totalMoves = dimension * 2;
      this.minutes = 0;
      this.seconds = 0;
      this.onTicks = 0;
      Color tempColor = this.findCell(0, 0).color;
      for (ArrayList<Cell> arrList : this.board) {
        for (Cell c : arrList) {
          if (c.sameColor(tempColor) && (c.left != null && c.left.isFlooded()
              || c.right != null && c.right.isFlooded() 
              || c.top != null && c.top.isFlooded()
              || c.bottom != null && c.bottom.isFlooded())) {
            c.flooded = true;
          }
        }
      }
    }
  }

  // convenience constructor for FloodIt where the array is given and links them
  FloodIt(ArrayList<ArrayList<Cell>> arr) {
    if (arr.size() < 2) {
      throw new IllegalArgumentException("Invalid number of dimensions!");
    }
    else {
      this.dimension = arr.size();
      this.board = arr; // sets the board to nothing
      // links the cells
      this.linkCellBoard(this.board);
      this.findCell(0, 0).flooded = true;
      this.currentMove = 0;
      this.totalMoves = arr.size() * 2;
      this.minutes = 0;
      this.seconds = 0;
      this.onTicks = 0;
      Color tempColor = this.findCell(0, 0).color;
      for (ArrayList<Cell> arrList : this.board) {
        for (Cell c : arrList) {
          if (c.sameColor(tempColor) && (c.left != null && c.left.isFlooded()
              || c.right != null && c.right.isFlooded() 
              || c.top != null && c.top.isFlooded()
              || c.bottom != null && c.bottom.isFlooded())) {
            c.flooded = true;
          }
        }
      }
    }
  }

  // draws the game
  public WorldScene makeScene() {
    WorldScene background = new WorldScene(this.board.size() * 20, 
        this.board.size() * 20 + 30);
    WorldImage board = this.drawBoard();
    WorldImage guesses = new TextImage(
        String.valueOf(this.currentMove) 
        + "/" + String.valueOf(this.totalMoves), 20,
        FontStyle.BOLD, Color.black);
    WorldImage time = new TextImage(
        // " " don't forget this part
        "|" + String.valueOf(this.minutes) + ":" 
            + String.valueOf(this.seconds), 20, FontStyle.BOLD,
        Color.black);
    WorldImage guessesTime = new BesideImage(guesses, time);
    background.placeImageXY(board, this.board.size() * 10, 
        this.board.size() * 10); // places board
    background.placeImageXY(guessesTime, 60, 
        this.board.size() * 20 + 10); // places guesses and time
    background.placeImageXY(this.checkWinLose(), this.board.size() * 10, 
        this.board.size() * 10); // places win or lose
    return background;
  }

  public void onKeyEvent(String key) {
    // 'r' resets the board with the same dimensions
    if (key.equals("r")) {
      this.board = new ArrayList<ArrayList<Cell>>(); // sets the board to nothing
      // creates the cells
      this.createCellBoards(dimension, new Random());
      // links the cells
      this.linkCellBoard(this.board);
      this.findCell(0, 0).flooded = true;
      this.currentMove = 0;
      this.totalMoves = dimension * 2;
      this.minutes = 0;
      this.seconds = 0;
      this.onTicks = 0;
      Color tempColor = this.findCell(0, 0).color;
      for (ArrayList<Cell> arrList : this.board) {
        for (Cell c : arrList) {
          if (c.sameColor(tempColor) && (c.left != null && c.left.isFlooded()
              || c.right != null && c.right.isFlooded() 
              || c.top != null && c.top.isFlooded()
              || c.bottom != null && c.bottom.isFlooded())) {
            c.flooded = true;
          }
        }
      }
    }
  }

  // does something based on where the mouse was clicked
  public void onMouseClicked(Posn posn) {
    Cell clicked = this.findCell(posn.y / 20, 
        posn.x / 20); // must switch y and x cause row and col
    if (this.findCell(0, 0).sameColor(clicked.color) 
        || this.currentMove >= this.totalMoves) {
      return;
    }
    else {
      this.findCell(0, 0).color = clicked.color;
      this.floodCells(clicked.color);
      this.currentMove += 1;
    }
  }

  // does something each tick
  public void onTick() {
    Color givenColor = this.findCell(0, 0).color;
    ArrayList<Cell> floodedColor = new ArrayList<Cell>();
    floodedColor.add(this.findCell(0, 0));
    // loops through the list and finds the cells with the same color
    // and if they're flooded
    for (int row = 0; row < dimension; row += 1) {
      for (int col = 0; col < dimension; col += 1) {
        Cell temp = this.findCell(row, col);
        if (temp.flooded && temp.sameColor(givenColor)
            && (floodedColor.contains(temp.left) || floodedColor.contains(temp.right)
                || floodedColor.contains(temp.top) || floodedColor.contains(temp.bottom))) {
          floodedColor.add(temp);
        }
      }
    }

    // loops through the floodedColor that has the same color as
    // the top left cell and checks their neighbors for flooded
    for (Cell c : floodedColor) {
      if (c.left != null && c.left.isFlooded()) {
        c.left.changeColor(givenColor);
      }
      if (c.right != null && c.right.isFlooded()) {
        c.right.changeColor(givenColor);
      }
      if (c.top != null && c.top.isFlooded()) {
        c.top.changeColor(givenColor);
      }
      if (c.bottom != null && c.bottom.isFlooded()) {
        c.bottom.changeColor(givenColor);
      }
    }

    // we're assuming that the ontick for bigBang is .1
    this.onTicks += 1;
    if (this.onTicks == 10) {
      this.onTicks = 0;
      this.seconds += 1;
      if (this.seconds == 60) {
        this.seconds = 0;
        this.minutes += 1;
      }
    }
  }

  // ENHANCED GRAPHICS: COLLAPSES INWARDS FOR WATERFALL
  // -- UNCOMMENT TO USE --
  // does something each tick
  /*
  public void onTick() {
    Color givenColor = this.findCell(0, 0).color;
    ArrayList<Cell> floodedColor = new ArrayList<Cell>();
    // loops through the list and finds the cells with the same color
    // and if they're flooded
    for (int row = 0; row < dimension; row += 1) {
      for (int col = 0; col < dimension; col += 1) {
        Cell temp = this.findCell(row, col);
        if (temp.flooded && temp.sameColor(givenColor)) {
          floodedColor.add(temp);
        }
      }
    }
    // loops through the floodedColor that has the same color as
    // the top left cell and checks their neighbors for flooded
    for (Cell c : floodedColor) {
      if (c.left != null && c.left.isFlooded()) {
        c.left.changeColor(givenColor);
      }
      if (c.right != null && c.right.isFlooded()) {
        c.right.changeColor(givenColor);
      }
      if (c.top != null && c.top.isFlooded()) {
        c.top.changeColor(givenColor);
      }
      if (c.bottom != null && c.bottom.isFlooded()) {
        c.bottom.changeColor(givenColor);
      }
    }
    // we're assuming that the ontick for bigBang is .1
    this.onTicks += 1;
    if (this.onTicks == 10) {
      this.onTicks = 0;
      this.seconds += 1;
      if (this.seconds == 60) {
        this.seconds = 0;
        this.minutes += 1;
      }
    }
  }
  */

  // checks if we win or lose and returns an image
  WorldImage checkWinLose() {
    int floodedCount = 0;
    for (int row = 0; row < board.size(); row += 1) {
      for (int col = 0; col < board.size(); col += 1) {
        if (this.findCell(row, col).isFlooded()) {
          floodedCount += 1;
        }
      }
    }
    if (floodedCount >= this.board.size() * this.board.size()) {
      return new TextImage("YOU WIN!", 30, Color.white);
    }
    else if (this.currentMove == this.totalMoves) {
      return new TextImage("YOU LOSE!", 30, Color.white);
    }
    else {
      return new EmptyImage();
    }
  }

  // create a new board with cells with a seeded random
  void createCellBoards(int dimension, Random random) {
    // creates the cells
    for (int row = 0; row < dimension; row += 1) {
      ArrayList<Cell> yboard = new ArrayList<Cell>();
      for (int col = 0; col < dimension; col += 1) {
        yboard.add(new Cell(row, col, this.randomColor(random)));
      }
      this.board.add(yboard);
    }
  }

  // draws the board
  WorldImage drawBoard() {
    WorldImage boardDrawing = new EmptyImage();
    for (int row = 0; row < this.board.size(); row += 1) {
      WorldImage rowDrawing = new EmptyImage();
      for (int col = 0; col < this.board.size(); col += 1) {
        rowDrawing = new BesideImage(rowDrawing, this.findCell(row, col).drawCell());
      }
      boardDrawing = new AboveImage(boardDrawing, rowDrawing);
    }
    return boardDrawing;
  }

  // finds the cell at that coordinate and return Exception if wrong coordinate
  Cell findCell(int row, int col) {
    return this.board.get(row).get(col);
  }

  // loops through board and checks for new flooded and changes if it is
  void floodCells(Color color) {
    for (int row = 0; row < this.board.size(); row += 1) {
      for (int col = 0; col < this.board.size(); col += 1) {
        Cell temp = this.findCell(row, col);
        if (temp.flooded) {
          if (temp.left != null && temp.left.sameColor(color)) {
            temp.left.trueFlooded();
          }
          if (temp.right != null && temp.right.sameColor(color)) {
            temp.right.trueFlooded();
          }
          if (temp.top != null && temp.top.sameColor(color)) {
            temp.top.trueFlooded();
          }
          if (temp.bottom != null && temp.bottom.sameColor(color)) {
            temp.bottom.trueFlooded();
          }
        }
      }
    }
  }

  // links the cells in the board together
  void linkCellBoard(ArrayList<ArrayList<Cell>> arrArr) {
    for (int row = 0; row < arrArr.size(); row += 1) {
      for (int col = 0; col < arrArr.size(); col += 1) {
        if (row > 0) {
          this.findCell(row, col).setPosition(this.findCell(row - 1, col), "top");
        }
        if (row < arrArr.size() - 1) {
          this.findCell(row, col).setPosition(this.findCell(row + 1, col), "bottom");
        }
        if (col > 0) {
          this.findCell(row, col).setPosition(this.findCell(row, col - 1), "left");
        }
        if (col < arrArr.size() - 1) {
          this.findCell(row, col).setPosition(this.findCell(row, col + 1), "right");
        }
      }
    }
  }

  // returns a random color from the list
  Color randomColor(Random random) {
    return this.colorList.get(random.nextInt(colorList.size()));
  }
}

// Examples

class ExamplesFloodIt {
  Cell cell00;
  Cell cell01;
  Cell cell10;
  Cell cell11;
  Cell newCell00;
  Cell newCell01;
  Cell newCell10;
  Cell newCell11;
  ArrayList<Cell> row0;
  ArrayList<Cell> row1;
  ArrayList<Cell> newRow0;
  ArrayList<Cell> newRow1;
  ArrayList<ArrayList<Cell>> arrArr;
  ArrayList<ArrayList<Cell>> newArrArr;
  FloodIt flood2;
  FloodIt flood5;

  void initData() {
    this.cell00 = new Cell(0, 0, Color.red);
    this.cell01 = new Cell(0, 1, Color.orange);
    this.cell10 = new Cell(1, 0, Color.yellow);
    this.cell11 = new Cell(1, 1, Color.green);
    this.newCell00 = new Cell(0, 0, Color.red);
    this.newCell01 = new Cell(0, 1, Color.orange);
    this.newCell10 = new Cell(1, 0, Color.yellow);
    this.newCell11 = new Cell(1, 1, Color.green);

    this.row0 = new ArrayList<Cell>(Arrays.asList(this.cell00, this.cell01));
    this.row1 = new ArrayList<Cell>(Arrays.asList(this.cell10, this.cell11));
    this.newRow0 = new ArrayList<Cell>(Arrays.asList(this.newCell00, this.newCell01));
    this.newRow1 = new ArrayList<Cell>(Arrays.asList(this.newCell10, this.newCell11));

    this.arrArr = new ArrayList<ArrayList<Cell>>(Arrays.asList(this.row0, this.row1));
    this.newArrArr = new ArrayList<ArrayList<Cell>>(Arrays.asList(this.newRow0, this.newRow1));

    this.flood2 = new FloodIt(this.arrArr);
    this.flood5 = new FloodIt(5);
  }

  // Cell
  // tests for changeColor(Color)
  void testChangeColor(Tester t) {
    this.initData();
    t.checkExpect(this.cell00.color, Color.red);
    this.cell00.changeColor(Color.orange);
    t.checkExpect(this.cell00.color, Color.orange);
  }

  // tests for drawCell()
  void testDrawCell(Tester t) {
    this.initData();
    t.checkExpect(this.cell00.drawCell(), new RectangleImage(20, 20, "solid", Color.red));
    t.checkExpect(this.cell01.drawCell(), new RectangleImage(20, 20, "solid", Color.orange));
    t.checkExpect(this.cell10.drawCell(), new RectangleImage(20, 20, "solid", Color.yellow));
    t.checkExpect(this.cell11.drawCell(), new RectangleImage(20, 20, "solid", Color.green));
  }

  // tests for isFlooded()
  void testIsFlooded(Tester t) {
    this.initData();
    t.checkExpect(this.cell00.isFlooded(), true);
    t.checkExpect(this.cell01.isFlooded(), false);
    this.flood2.onMouseClicked(new Posn(30, 10));
    t.checkExpect(this.cell00.isFlooded(), true);
    t.checkExpect(this.cell01.isFlooded(), true);
  }

  // tests for sameColor(Color)
  void testSameColor(Tester t) {
    this.initData();
    t.checkExpect(this.cell00.sameColor(Color.black), false);
    t.checkExpect(this.cell00.sameColor(Color.red), true);
  }

  // tests for setPosition(Cell, String)
  void testSetPosition(Tester t) {
    this.initData();
    t.checkExpect(this.newCell00.right, null);
    this.newCell00.setPosition(this.newCell01, "right");
    t.checkExpect(this.newCell00.right, this.newCell01);

    t.checkExpect(this.newCell10.top, null);
    this.newCell10.setPosition(this.newCell00, "top");
    t.checkExpect(this.newCell10.top, this.newCell00);

    t.checkExpect(this.newCell01.left, null);
    this.newCell01.setPosition(this.newCell00, "left");
    t.checkExpect(this.newCell01.left, this.newCell00);

    t.checkExpect(this.newCell00.bottom, null);
    this.newCell00.setPosition(this.newCell10, "bottom");
    t.checkExpect(this.newCell00.bottom, this.newCell10);
  }

  // tests for trueFlooded()
  void testTrueFlooded(Tester t) {
    this.initData();
    t.checkExpect(this.cell00.flooded, true);
    this.cell00.trueFlooded();
    t.checkExpect(this.cell00.flooded, true);
    t.checkExpect(this.cell01.flooded, false);
    this.cell01.trueFlooded();
    t.checkExpect(this.cell01.flooded, true);
  }

  // FloodIt
  // tests the construction of FloodIt
  void testFloodItConstructor(Tester t) {
    this.initData();
    t.checkConstructorException(new IllegalArgumentException("Invalid number of dimensions!"),
        "FloodIt", 1);
    t.checkConstructorException(new IllegalArgumentException("Invalid number of dimensions!"),
        "FloodIt", 0);
    t.checkConstructorException(new IllegalArgumentException("Invalid number of dimensions!"),
        "FloodIt", -1);
    t.checkConstructorException(new IllegalArgumentException("Invalid number of dimensions!"),
        "FloodIt", new ArrayList<ArrayList<Cell>>());
  }

  // tests for makeScene()
  void testMakeScene(Tester t) {
    this.initData();
    WorldScene background = new WorldScene(40, 70);
    WorldImage board = new AboveImage(
        new AboveImage(new EmptyImage(),
            new BesideImage(
                new BesideImage(new EmptyImage(), new RectangleImage(20, 20, "solid", Color.red)),
                new RectangleImage(20, 20, "solid", Color.orange))),
        new BesideImage(
            new BesideImage(new EmptyImage(), new RectangleImage(20, 20, "solid", Color.yellow)),
            new RectangleImage(20, 20, "solid", Color.green)));
    WorldImage guesses = new TextImage(String.valueOf(0) + "/" + String.valueOf(4), 20,
        FontStyle.BOLD, Color.black);
    WorldImage time = new TextImage("|" + String.valueOf(0) + ":" + String.valueOf(0), 20,
        FontStyle.BOLD, Color.black);
    WorldImage guessesTime = new BesideImage(guesses, time);
    background.placeImageXY(board, 20, 20); // places board
    background.placeImageXY(guessesTime, 60, 50); // places guesses and time
    background.placeImageXY(new EmptyImage(), 20, 20);
    t.checkExpect(this.flood2.makeScene(), background);
  }

  // tests for onKeyEvent(String) or (String, Random)
  void testOnKeyEvent(Tester t) {
    this.initData();
    t.checkExpect(this.flood2.board, this.arrArr);

    this.flood2.onKeyEvent("r");
    this.flood2.board = new ArrayList<ArrayList<Cell>>(); // sets the board to nothing
    // creates the cells
    this.flood2.createCellBoards(2, new Random());
    // links the cells
    this.flood2.linkCellBoard(this.flood2.board);
    this.flood2.findCell(0, 0).flooded = true;
    Color tempColor = this.flood2.findCell(0, 0).color;
    for (ArrayList<Cell> arrList : this.flood2.board) {
      for (Cell c : arrList) {
        if (c.sameColor(tempColor) && (c.left != null && c.left.isFlooded()
            || c.right != null && c.right.isFlooded() || c.top != null && c.top.isFlooded()
            || c.bottom != null && c.bottom.isFlooded())) {
          c.flooded = true;
        }
      }
    }
    ArrayList<ArrayList<Cell>> randomBoard = this.flood2.board;

    t.checkOneOf(this.flood2.board, randomBoard);
  }

  // tests for onMouseClicked(Posn)
  void testOnMouseClicked(Tester t) {
    this.initData();
    ArrayList<Cell> row0 = new ArrayList<Cell>(
        Arrays.asList(new Cell(0, 0, Color.orange), new Cell(0, 1, Color.orange)));
    ArrayList<Cell> row1 = new ArrayList<Cell>(
        Arrays.asList(new Cell(1, 0, Color.yellow), new Cell(1, 1, Color.green)));
    ArrayList<ArrayList<Cell>> arr = new ArrayList<ArrayList<Cell>>(Arrays.asList(row0, row1));
    FloodIt flood = new FloodIt(arr);
    flood.linkCellBoard(flood.board);
    flood.findCell(0, 1).flooded = true;

    t.checkExpect(this.flood2.board, this.flood2.board);
    t.checkExpect(this.flood2.currentMove, 0);

    this.flood2.onMouseClicked(new Posn(10, 10));
    t.checkExpect(this.flood2.board, this.flood2.board);
    t.checkExpect(this.flood2.currentMove, 0);

    this.flood2.onMouseClicked(new Posn(30, 10));
    t.checkExpect(this.flood2.board, flood.board);
    t.checkExpect(this.flood2.currentMove, 1);
  }

  // tests for onTick()
  void testOnTick(Tester t) {
    this.initData();
    t.checkExpect(this.flood2.board, this.arrArr);
    this.flood2.onTick();
    this.cell00.trueFlooded();
    this.cell01.trueFlooded();
    this.cell10.trueFlooded();
    this.cell11.trueFlooded();
    t.checkExpect(this.flood2.board, this.arrArr);
  }

  // tests for checkWinLose()
  void testCheckWinLose(Tester t) {
    this.initData(); // Winning the game
    t.checkExpect(this.flood2.currentMove, 0);
    t.checkExpect(this.flood2.checkWinLose(), new EmptyImage());
    this.flood2.onMouseClicked(new Posn(30, 10));
    t.checkExpect(this.flood2.currentMove, 1);
    t.checkExpect(this.flood2.checkWinLose(), new EmptyImage());
    this.flood2.onMouseClicked(new Posn(10, 30));
    t.checkExpect(this.flood2.currentMove, 2);
    t.checkExpect(this.flood2.checkWinLose(), new EmptyImage());
    this.flood2.onMouseClicked(new Posn(30, 30));
    t.checkExpect(this.flood2.currentMove, 3);
    t.checkExpect(this.flood2.checkWinLose(), new TextImage("YOU WIN!", 30, Color.white));

    this.initData(); // Losing the game
    t.checkExpect(this.flood2.totalMoves, 4);
    t.checkExpect(this.flood2.currentMove, 0);
    t.checkExpect(this.flood2.checkWinLose(), new EmptyImage());
    this.flood2.onMouseClicked(new Posn(30, 10));
    t.checkExpect(this.flood2.currentMove, 1);
    t.checkExpect(this.flood2.checkWinLose(), new EmptyImage());
    this.flood2.onMouseClicked(new Posn(10, 30));
    t.checkExpect(this.flood2.currentMove, 2);
    t.checkExpect(this.flood2.checkWinLose(), new EmptyImage());
    this.flood2.onMouseClicked(new Posn(30, 10));
    t.checkExpect(this.flood2.currentMove, 3);
    t.checkExpect(this.flood2.checkWinLose(), new EmptyImage());
    this.flood2.onMouseClicked(new Posn(10, 30));
    t.checkExpect(this.flood2.currentMove, 4);
    t.checkExpect(this.flood2.checkWinLose(), new TextImage("YOU LOSE!", 30, Color.white));
  }

  // tests for createCellBoards(int)
  void testCreateCellBoards(Tester t) {
    this.initData();
    FloodIt newFlood2 = new FloodIt(this.newArrArr);
    newFlood2.board = new ArrayList<ArrayList<Cell>>();
    t.checkExpect(newFlood2.board.size(), 0);
    newFlood2.createCellBoards(2, new Random(1));
    t.checkExpect(newFlood2.board.size(), 2);
  }

  // tests for drawBoard()
  void testDrawBoard(Tester t) {
    this.initData();
    // 2x2
    WorldImage flood2Row0 = new BesideImage(
        new BesideImage(new EmptyImage(), new RectangleImage(20, 20, "solid", Color.red)),
        new RectangleImage(20, 20, "solid", Color.orange));
    WorldImage flood2Row1 = new BesideImage(
        new BesideImage(new EmptyImage(), new RectangleImage(20, 20, "solid", Color.yellow)),
        new RectangleImage(20, 20, "solid", Color.green));
    WorldImage flood2Board = new AboveImage(new AboveImage(new EmptyImage(), flood2Row0),
        flood2Row1);
    t.checkExpect(this.flood2.drawBoard(), flood2Board);
  }

  // tests for findCell(int, int)
  void testFindCell(Tester t) {
    this.initData();
    t.checkExpect(this.flood2.findCell(0, 0), this.cell00);
    t.checkExpect(this.flood2.findCell(1, 0), this.cell10);
    t.checkExpect(this.flood2.findCell(0, 1), this.cell01);
    t.checkExpect(this.flood2.findCell(1, 1), this.cell11);
  }

  // tests for floodCells(Color)
  void testFloodCells(Tester t) {
    this.initData();
    t.checkExpect(this.flood2.board, this.flood2.board);
    this.flood2.floodCells(Color.red);
    t.checkExpect(this.flood2.board, this.flood2.board);

    ArrayList<Cell> row0 = new ArrayList<Cell>(
        Arrays.asList(new Cell(0, 0, Color.red), new Cell(0, 1, Color.orange)));
    ArrayList<Cell> row1 = new ArrayList<Cell>(
        Arrays.asList(new Cell(1, 0, Color.yellow), new Cell(1, 1, Color.green)));
    ArrayList<ArrayList<Cell>> arr = new ArrayList<ArrayList<Cell>>(Arrays.asList(row0, row1));
    FloodIt flood = new FloodIt(arr);
    flood.linkCellBoard(flood.board);
    flood.findCell(0, 1).flooded = true;

    t.checkExpect(this.flood2.board, this.flood2.board);
    this.flood2.floodCells(Color.orange);
    t.checkExpect(this.flood2.board, flood.board);

  }

  // tests to see if the cells are correctly linked in the board
  void testLinkCellBoards(Tester t) {
    this.initData();
    t.checkExpect(this.newCell01.right, null);
    t.checkExpect(this.newCell00.left, null);
    t.checkExpect(this.newCell00.top, null);
    t.checkExpect(this.newCell10.bottom, null);
    new FloodIt(this.newArrArr).linkCellBoard(this.newArrArr);
    t.checkExpect(this.newCell00.right, this.newCell01);
    t.checkExpect(this.newCell01.left, this.newCell00);
    t.checkExpect(this.newCell10.top, this.newCell00);
    t.checkExpect(this.newCell00.bottom, this.newCell10);
  }

  // tests for randomColor(Random)
  void testRandomColor(Tester t) {
    this.initData();
    t.checkExpect(this.flood2.randomColor(new Random(1)), Color.green);
    t.checkExpect(this.flood2.randomColor(new Random(2)), Color.blue);
    t.checkExpect(this.flood5.randomColor(new Random(2)), Color.blue);
  }

  void testBigBang(Tester t) {
    FloodIt w = new FloodIt(7);
    int width = w.board.size() * 20;
    int height = w.board.size() * 20 + 20;
    w.bigBang(width, height, .1);
  }
}