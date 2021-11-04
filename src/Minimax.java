import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Minimax {
    class Path {
        Piece currentPiece;
        Point currentTarget;
        List<Piece> skipped;

        public Path(Piece currentPiece, Point currentTarget, List<Piece> skipped) {
            this.currentPiece = currentPiece;
            this.currentTarget = currentTarget;
            this.skipped = skipped;
        }
    }
    public HashMap<Board, Path> getAllMoves(Board position, String color){
        HashMap<Board, Path> allMoves = new HashMap<>();
        //List<Board> result = new ArrayList<>();
        // first get all possible Pieces
        List<Piece> possiblePieces = position.getAllPieces(color);
        //2. get all possible move
        for(Piece currentPiece : possiblePieces){
            HashMap<Point, PieceList> moves = position.validMoves(currentPiece);
            Iterator<Map.Entry<Point, PieceList>> iterator = moves.entrySet().iterator();
            while(iterator.hasNext()){
                Board temp = position.generateCopy(position);
                Map.Entry<Point, PieceList> entry = iterator.next();
                Point currentTarget = entry.getKey();
                List<Piece> skipped = entry.getValue().skipped;
                List<Piece> stopped = entry.getValue().stopped;
                simulateMove(currentTarget, currentPiece, temp, skipped);
                allMoves.put(temp, new Path(currentPiece, currentTarget, stopped));

            }
        }
        return allMoves;
    }
    public Board simulateMove(Point currentTarget, Piece currentPiece, Board temp, List<Piece> skipped){
        temp.move(currentPiece, currentTarget.x, currentTarget.y);
        temp.removePieces(skipped);
        return temp;
    }
    public Path alphaBeta(Board position, int depth, int SearchDepth, String myturn){
        String maxColor;
        String minColor;
        if(myturn.equals("BLACK")){
            maxColor = "BLACK";
            minColor = "WHITE";
        }else{
            maxColor = "WHITE";
            minColor = "BLACK";
        }
        Path[] bestMove = new Path[1];
        MaxValue(position, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, SearchDepth, maxColor, minColor, bestMove);
        return bestMove[0];
    }
    private double MaxValue(Board position, double alpha, double beta, int depth, int SearchDepth, String maxColor, String minColor, Path[] bestMove) {
        if(depth == 0 || position.winner() != null){
            return position.evaluate();
        }
        double v = Integer.MIN_VALUE;
        //each a in action(state)
        HashMap<Board, Path> possibleJump = new HashMap<>();
        HashMap<Board, Path> allMoves = getAllMoves(position, maxColor);
        Iterator<Map.Entry<Board, Path>> iterator = allMoves.entrySet().iterator();
        while (iterator.hasNext()) {
            //result(s,a)
            Map.Entry<Board, Path> entry = iterator.next();
            if (entry.getValue().skipped.size() > 0) {
                possibleJump.put(entry.getKey(), entry.getValue());
            }
        }
        if(possibleJump.size() > 0){
            allMoves = possibleJump;
        }
        Iterator<Map.Entry<Board, Path>> iterator2 = allMoves.entrySet().iterator();
        while (iterator2.hasNext()) {
            Map.Entry<Board, Path> entry = iterator2.next();
            double evalScore = MinValue(entry.getKey(), alpha, beta, depth - 1, SearchDepth, maxColor, minColor, bestMove);
            v = Math.max(v, evalScore);
            if(v >= beta){
                return v;
            }
            alpha = Math.max(alpha, v);
            if(alpha == evalScore && depth == SearchDepth){
                bestMove[0] = entry.getValue();
            }
        }
        return v;
    }
    private double MinValue(Board position, double alpha, double beta, int depth, int SearchDepth, String maxColor, String minColor, Path[] bestMove){
        if(depth == 0 || position.winner() != null){
            return position.evaluate();
        }
        double v = Integer.MAX_VALUE;
        HashMap<Board, Path> possibleJump = new HashMap<>();
        HashMap<Board, Path> allMoves = getAllMoves(position, minColor);
        Iterator<Map.Entry<Board, Path>> iterator = allMoves.entrySet().iterator();
        while (iterator.hasNext()) {
            //result(s,a)
            Map.Entry<Board, Path> entry = iterator.next();
            if (entry.getValue().skipped.size() > 0) {
                possibleJump.put(entry.getKey(), entry.getValue());
            }
        }
        if(possibleJump.size() > 0){
            allMoves = possibleJump;
        }
        Iterator<Map.Entry<Board, Path>> iterator2 = allMoves.entrySet().iterator();
        while (iterator2.hasNext()) {
            //result(s,a)
            Map.Entry<Board, Path> entry = iterator2.next();
            double evalScore = MaxValue(entry.getKey(), alpha, beta, depth - 1, SearchDepth, maxColor, minColor, bestMove);
            v = Math.min(v, evalScore);
            if(v <= alpha){
                return v;
            }
            beta = Math.min(beta, v);
        }
        return v;

    }
}
