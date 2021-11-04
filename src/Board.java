import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;
public class Board {
    int row = 8;
    int col = 8;
    Piece[][] position;
    int whitePieces;
    int blackPieces;
    int whiteKing;
    int blackKing;
    String myColor;
    String time;
    int blackRow;
    int whiteRow;
    public Board(Scanner scanner){
        myColor = scanner.nextLine();
        time = scanner.nextLine();
        position = new Piece[row][col];
        for(int i = 0; i < 8; i ++) {
            String reading = scanner.nextLine();
            for (int j = 0; j < 8; j++) {
                position[i][j] = new Piece(i, j, reading.charAt(j));
                if (reading.charAt(j) == 'b') {
                    blackPieces++;
                } else if (reading.charAt(j) == 'w') {
                    whitePieces++;
                } else if (reading.charAt(j) == 'B') {
                    blackKing++;
                } else if (reading.charAt(j) == 'W') {
                    whiteKing++;
                }
                if((i == 0 || i == 1 ) && reading.charAt(j) == 'b'){
                    blackRow ++;
                }
                if((i == 7 || i == 6 ) && reading.charAt(j) == 'w'){
                    whiteRow ++;
                }
            }
        }
    }
    public Board(Piece[][] position, int whitePieces, int whiteKing, int blackKing, int blackPieces, String mycolor){
        this.position = position;
        this.whitePieces = whitePieces;
        this.whiteKing = whiteKing;
        this.blackKing = blackKing;
        this.blackPieces = blackPieces;
        this.myColor = mycolor;
    }
    public Board generateCopy(Board original){
        Piece[][] copyboard = new Piece[8][8];
        for(int i = 0; i < 8; i ++){
            for(int j = 0; j < 8; j ++){
                copyboard[i][j] = original.position[i][j].deepCopy();
            }
        }
        Board copy = new Board(copyboard, original.whitePieces, original.whiteKing, original.blackKing, original.blackPieces, original.myColor);
        return copy;
    }
    public double evaluate(){
        if(myColor.equals("BLACK")){
            return this.blackPieces - this.whitePieces + 0.5 * (this.blackKing - this.whiteKing);
        }
        return this.whitePieces - this.blackPieces + 0.5 * (this.whiteKing - this.blackKing);
    }
    public String winner(){
        String result = null;
        if (whitePieces <= 0 && whiteKing <= 0){
            result = "black";
        }else if(blackPieces <= 0 && blackKing <= 0){
            result = "white";
        }
        return result;
    }
    public List<Piece> getAllPieces(String myColor){
        List<Piece> result = new ArrayList<>();
        for(Piece[] row : position){
            for(Piece currentPiece : row){
                if(currentPiece.color.equals(myColor)){
                    result.add(currentPiece);
                }
            }
        }
        return result;
    }
    public HashMap<Point,PieceList> validMoves(Piece piece) {
        HashMap<Point, PieceList> leftresult = new HashMap<>();
        HashMap<Point, PieceList> rightresult = new HashMap<>();
        HashMap<Point, PieceList> result = new HashMap<>();
        List<Piece> leftskipped = new ArrayList<>();
        List<Piece> leftstopped = new ArrayList<>();
        int[] leftglobalMax = new int[]{0};
        List<Piece> rightskipped = new ArrayList<>();
        List<Piece> rightstopped = new ArrayList<>();
        int[] rightglobalMax =  new int[]{0};
        if (piece.sign == 'b' || piece.king) {
            blackleftMove(true, true,piece, piece.row, piece.col, leftskipped, leftstopped, leftglobalMax, leftresult);
            blackrightMove(true,true,piece, piece.row, piece.col, rightskipped, rightstopped, rightglobalMax, rightresult);
        }
        if (piece.sign == 'w' || piece.king) {
            whiteleftMove(true, true,piece, piece.row, piece.col, leftskipped,leftstopped, leftglobalMax, leftresult);
            whiterightMove(true,true,piece, piece.row, piece.col, rightskipped,rightstopped, rightglobalMax, rightresult);

        }
        if ((leftglobalMax[0] == 0 && rightglobalMax[0] == 0) || (leftglobalMax[0] > 0 && rightglobalMax[0] > 0)) {
            result.putAll(leftresult);
            result.putAll(rightresult);
        }else if(leftglobalMax[0] > 0){
            return leftresult;
        }else {
            return rightresult;
        }
        return result;
    }
    private boolean whiteleftMove(boolean left, boolean right, Piece piece, int currentRow, int currentCol, List<Piece> skipped, List<Piece> stopped, int[] globalMax, HashMap<Point, PieceList> map) {
        int possibleX = currentRow - 1;
        int possibleY = currentCol - 1;
        if (inBoundary(possibleX, possibleY)) {
            if (!position[possibleX][possibleY].color.equals(piece.color) && position[possibleX][possibleY].sign != '.'){
                int jumpX = possibleX - 1;
                int jumpY = possibleY - 1;
                if (inBoundary(jumpX, jumpY) && position[jumpX][jumpY].sign == '.') {
                    skipped.add(position[possibleX][possibleY]);
                    stopped.add(position[jumpX][jumpY]);
                    globalMax[0] = Math.max(globalMax[0], skipped.size());
                    left = whiteleftMove(left, right,piece, jumpX, jumpY, skipped, stopped, globalMax, map);
                    right = whiterightMove(left, right,piece, jumpX, jumpY, skipped, stopped, globalMax, map);
                    skipped.remove(position[possibleX][possibleY]);
                    stopped.remove(position[jumpX][jumpY]);
                }
            }
            if (position[possibleX][possibleY].sign != '.') {
                left = false;
                if (globalMax[0] > 0 && globalMax[0] == skipped.size() && left == false && right == false) {
                    Point target = new Point(currentRow, currentCol);
                    if (!map.containsKey(target)) {
                        map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                    }
                }
                return left;
            }
            if(position[possibleX][possibleY].sign == '.'){
                left = false;
                if (globalMax[0] == 0) {
                    Point target = new Point(possibleX, possibleY);
                    if (!map.containsKey(target)) {
                        map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                    }
                    return left;
                } else if ( globalMax[0] == skipped.size() && left == false && right == false) {
                    Point target = new Point(currentRow, currentCol);
                    if (!map.containsKey(target)) {
                        map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                    }
                    return left;
                }
            }
        }else if(!inBoundary(possibleX,possibleY)){
            left = false;
            if ( globalMax[0] == skipped.size() && left == false && right == false) {
                Point target = new Point(currentRow, currentCol);
                if (!map.containsKey(target)) {
                    map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                }
                return left;
            }
        }
        return left;
    }
    private boolean whiterightMove(boolean left, boolean right, Piece piece, int currentRow, int currentCol, List<Piece> skipped, List<Piece> stopped, int[] globalMax, HashMap<Point, PieceList> map) {
        int possibleX = currentRow - 1;
        int possibleY = currentCol + 1;
        // first check if I can jump
        if (inBoundary(possibleX, possibleY) && !position[possibleX][possibleY].color.equals(piece.color) && position[possibleX][possibleY].sign != '.'){
            int jumpX = possibleX - 1;
            int jumpY = possibleY + 1;
            if (inBoundary(jumpX, jumpY) && position[jumpX][jumpY].sign == '.') {
                skipped.add(position[possibleX][possibleY]);
                stopped.add(position[jumpX][jumpY]);
                globalMax[0] = Math.max(globalMax[0], skipped.size());
                left = whiteleftMove(left, right,piece, jumpX, jumpY, skipped, stopped, globalMax, map);
                right = whiterightMove(left,right,piece, jumpX, jumpY, skipped, stopped, globalMax, map);
                skipped.remove(position[possibleX][possibleY]);
                stopped.remove(position[jumpX][jumpY]);
            }
        }
        if (inBoundary(possibleX, possibleY) &&position[possibleX][possibleY].sign != '.') {
            right = false;
            if (globalMax[0] > 0 && globalMax[0] == skipped.size() && left == false && right== false) {
                Point target = new Point(currentRow, currentCol);
                if (!map.containsKey(target)) {
                    map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                }
            }
            return right;
        }
        if(inBoundary(possibleX, possibleY) &&position[possibleX][possibleY].sign == '.'){
            right = false;
            if (globalMax[0] == 0) {
                Point target = new Point(possibleX, possibleY);
                if (!map.containsKey(target)) {
                    map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                }
                return right;
            } else if ( globalMax[0] == skipped.size() && left == false && right == false) {
                Point target = new Point(currentRow, currentCol);
                if (!map.containsKey(target)) {
                    map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                }
                return right;
            }
        }
        if(!inBoundary(possibleX,possibleY)) {
            right = false;
            if (globalMax[0] == skipped.size() && left == false && right == false) {
                Point target = new Point(currentRow, currentCol);
                if (!map.containsKey(target)) {
                    map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                }
                return right;
            }
        }
        return right;
    }
    private boolean blackleftMove(boolean left, boolean right, Piece piece, int currentRow, int currentCol, List<Piece> skipped, List<Piece> stopped, int[] globalMax, HashMap<Point, PieceList> map) {
        int possibleX = currentRow + 1;
        int possibleY = currentCol - 1;
        if (inBoundary(possibleX, possibleY)) {
            if (!position[possibleX][possibleY].color.equals(piece.color) && position[possibleX][possibleY].sign != '.'){
                int jumpX = possibleX + 1;
                int jumpY = possibleY - 1;
                if (inBoundary(jumpX, jumpY) && position[jumpX][jumpY].sign == '.') {
                    skipped.add(position[possibleX][possibleY]);
                    stopped.add(position[jumpX][jumpY]);
                    globalMax[0] = Math.max(globalMax[0], skipped.size());
                    left = blackleftMove(left, right,piece, jumpX, jumpY, skipped, stopped, globalMax, map);
                    right = blackrightMove(left, right,piece, jumpX, jumpY, skipped, stopped, globalMax, map);
                    skipped.remove(position[possibleX][possibleY]);
                    stopped.remove(position[jumpX][jumpY]);
                }
            }
            if (position[possibleX][possibleY].sign != '.') {
                left = false;
                if (globalMax[0] > 0 && globalMax[0] == skipped.size() && left == false && right == false) {
                    Point target = new Point(currentRow, currentCol);
                    if (!map.containsKey(target)) {
                        map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                    }
                }
                return left;
            }
            if(position[possibleX][possibleY].sign == '.'){
                left = false;
                if (globalMax[0] == 0) {
                    Point target = new Point(possibleX, possibleY);
                    if (!map.containsKey(target)) {
                        map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                    }
                    return left;
                } else if ( globalMax[0] == skipped.size() && left == false && right == false) {
                    Point target = new Point(currentRow, currentCol);
                    if (!map.containsKey(target)) {
                        map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                    }
                    return left;
                }
            }
        }else if(!inBoundary(possibleX,possibleY)){
            left = false;
            if ( globalMax[0] == skipped.size() && left == false && right == false) {
                Point target = new Point(currentRow, currentCol);
                if (!map.containsKey(target)) {
                    map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                }
                return left;
            }
        }
        return left;
    }
    private boolean blackrightMove(boolean left, boolean right, Piece piece, int currentRow, int currentCol, List<Piece> skipped, List<Piece> stopped,int[] globalMax, HashMap<Point, PieceList> map) {
        int possibleX = currentRow + 1;
        int possibleY = currentCol + 1;
        if (inBoundary(possibleX, possibleY)) {
            // first check if I can jump
            if (!position[possibleX][possibleY].color.equals(piece.color) && position[possibleX][possibleY].sign != '.'){
                int jumpX = possibleX + 1;
                int jumpY = possibleY + 1;
                if (inBoundary(jumpX, jumpY) && position[jumpX][jumpY].sign == '.') {
                    skipped.add(position[possibleX][possibleY]);
                    stopped.add(position[jumpX][jumpY]);
                    globalMax[0] = Math.max(globalMax[0], skipped.size());
                    left = blackleftMove(left, right,piece, jumpX, jumpY, skipped, stopped, globalMax, map);
                    right = blackrightMove(left,right,piece, jumpX, jumpY, skipped, stopped, globalMax, map);
                    skipped.remove(position[possibleX][possibleY]);
                    stopped.remove(position[jumpX][jumpY]);
                }
            }
            if (position[possibleX][possibleY].sign != '.') {
                right = false;
                if (globalMax[0] > 0 && globalMax[0] == skipped.size() && left == false && right== false) {
                    Point target = new Point(currentRow, currentCol);
                    if (!map.containsKey(target)) {
                        map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                    }
                }
                return right;
            }
            if(position[possibleX][possibleY].sign == '.'){
                right = false;
                if (globalMax[0] == 0) {
                    Point target = new Point(possibleX, possibleY);
                    if (!map.containsKey(target)) {
                        map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                    }
                    return right;
                } else if ( globalMax[0] == skipped.size() && left == false && right == false) {
                    Point target = new Point(currentRow, currentCol);
                    if (!map.containsKey(target)) {
                        map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                    }
                    return right;
                }
            }
        }else if(!inBoundary(possibleX,possibleY)){
            right = false;
            if ( globalMax[0] == skipped.size() && left == false && right == false) {
                Point target = new Point(currentRow, currentCol);
                if (!map.containsKey(target)) {
                    map.put(target, new PieceList(new ArrayList<>(skipped), new ArrayList<>(stopped)));
                }
                return right;
            }
        }
        return right;
    }
    private boolean inBoundary(int possibleX, int possibleY){
        if(possibleX >= 0 && possibleX < row && possibleY >= 0 && possibleY < col){
            return true;
        }
        return false;
    }
    public void move(Piece piece, int newRow, int newCol){
        position[newRow][newCol].sign = position[piece.row][piece.col].sign;
        position[newRow][newCol].color = position[piece.row][piece.col].color;
        position[piece.row][piece.col].sign = '.';
        position[piece.row][piece.col].color = ".";
        if(newRow == 7|| newRow == 0){
            if(piece.color.equals("BLACK")){
                position[newRow][newCol].sign = 'B';
                this.blackKing += 1;
            }else{
                position[newRow][newCol].sign = 'W';
                this.whiteKing += 1;
            }
        }
        return;
    }
    public void removePieces(List<Piece> skipped){
        if(skipped == null || skipped.size() == 0){
            return;
        }
        for(Piece p : skipped){
            if(p.sign == 'b'){
                blackPieces--;
            }else if(p.sign == 'w'){
                whitePieces--;
            }else if(p.sign == 'W'){
                whiteKing --;
            }else if(p.sign == 'B'){
                blackKing--;
            }
            position[p.row][p.col].sign ='.';
            position[p.row][p.col].color = ".";
        }
    }

    public void printtoConsole(){
        for(Piece[] current : position){
            for(Piece i : current){
                System.out.print(i.sign);
            }
            System.out.println();
        }
        System.out.println();
    }
    public void print() throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream("input.txt");
        PrintWriter pw = new PrintWriter(fos);
        pw.println("GAME");
        if(myColor.equals("BLACK")){
            pw.println("WHITE");
        }else{
            pw.println("BLACK");
        }
        pw.println("100.0");
        for(Piece[] current :position){
            for(Piece i : current){
                pw.print(i.sign);
            }
            pw.println();
        }
        pw.close();
    }
}
