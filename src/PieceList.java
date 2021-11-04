import java.util.ArrayList;

public class PieceList {
    ArrayList<Piece> skipped;
    ArrayList<Piece> stopped;
    public PieceList(ArrayList<Piece> skipped, ArrayList<Piece> stopped){
        this.skipped = skipped;
        this.stopped = stopped;
    }

}