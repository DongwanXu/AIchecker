public class Piece {
    public int row;
    public int col;
    public char sign;
    public String color;
    public boolean king;
    public Piece deepCopy(){
        Piece newPiece = new Piece(row, col, sign);
        return newPiece;
    }
    public Piece(int row, int col, char sign){
        this.row = row;
        this.col = col;
        this.sign = sign;
        if(sign == 'b' || sign == 'B'){
            color = "BLACK";
        }
        if(sign == 'w' || sign == 'W'){
            color = "WHITE";
        }
        if(sign == '.'){
            color = ".";
        }
        if(sign == 'W' || sign == 'B'){
            king = true;
        }
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append((char)('a' + col));
        sb.append(8 - row);
        return sb.toString();
    }

}
