import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
public class Homework {
    public static void main(String[] args) throws FileNotFoundException{
        FileInputStream file = new FileInputStream("input.txt");
        Scanner scanner = new Scanner(file);
        String GameType = scanner.nextLine();
        Board board = new Board(scanner);
        Game game = new Game(board, GameType);
        if(GameType.equals("SINGLE")){
            List<Piece> possiblePieces = board.getAllPieces(board.myColor);
            Piece outPiece = null;
            Map.Entry<Point, PieceList> outentry = null;
            for(Piece currentPiece : possiblePieces){
                HashMap<Point, PieceList> possibleMove = board.validMoves(currentPiece);
                Iterator<Map.Entry<Point, PieceList>> iterator = possibleMove.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<Point, PieceList> entry = iterator.next();
                    if(outPiece == null && outentry == null) {
                        outPiece = currentPiece;
                        outentry = entry;
                    }
                    if(entry.getValue().stopped.size() > 0){
                        outPiece = currentPiece;
                        outentry = entry;
                        break;
                    }
                }
            }
            game.outputSingle(outentry, outPiece);
        }
        if(GameType.equals("GAME")){
            int depth = 4;
//            if(Float.parseFloat(board.time) < 4){
//                depth = 3;
//            }else if(board.blackRow == 8 || board.whiteRow == 8){
//                depth = 3;
//            }else if(board.evaluate() < 0){
//                depth = 6;
//            }
            Minimax minimax = new Minimax();
            //long start  = System.currentTimeMillis();
            Minimax.Path newState = minimax.alphaBeta(board, depth,depth, board.myColor);
            game.outputGame(newState);
            //long time = System.currentTimeMillis();
            //long time2 = time - start;
            //System.out.println(time2);
        }
    }
}
