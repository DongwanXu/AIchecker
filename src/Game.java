import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
public class Game {
    public String gameType;
    Board board;
    public Game(Board board, String gameType){
        this.board = board;
        this.gameType = gameType;
    }
    public void outputSingle(Map.Entry<Point, PieceList> entry, Piece currentPiece) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream("output.txt");
        PrintWriter pw = new PrintWriter(fos);
        if(entry.getValue() == null || entry.getValue().stopped.size() == 0) {
            pw.print("E" + " ");
            pw.print(currentPiece.toString() + " ");
            pw.print(entry.getKey().toString());
            pw.close();
        }
        if(entry.getValue().stopped.size() > 0){
            pw.print("J" + " ");
            pw.print(currentPiece.toString() + " ");
            pw.println(entry.getValue().stopped.get(0).toString());
            for(int i = 1; i < entry.getValue().stopped.size(); i ++){
                pw.print("J" + " ");
                pw.print(entry.getValue().stopped.get(i-1).toString() + " ");
                pw.println(entry.getValue().stopped.get(i).toString());
            }
            pw.close();
        }
    }
    public void outputGame(Minimax.Path currentPath) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream("output.txt");
        PrintWriter pw = new PrintWriter(fos);
        List<Piece> stopped = currentPath.skipped;
        Piece currentPiece = currentPath.currentPiece;
        Point currentTarget = currentPath.currentTarget;
        if(stopped == null ||stopped.size() == 0) {
            pw.print("E" + " ");
            pw.print(currentPiece.toString() + " ");
            pw.print(currentTarget.toString());
            pw.close();
        }
        if(stopped.size() > 0){
            pw.print("J" + " ");
            pw.print(currentPiece.toString() + " ");
            pw.println(stopped.get(0).toString());
            for(int i = 1; i < stopped.size(); i ++){
                pw.print("J" + " ");
                pw.print(stopped.get(i-1).toString() + " ");
                pw.println(stopped.get(i).toString());
            }
            pw.close();
        }
    }
}
