package ijaproject23.position;

public final class Position extends Object{
    public int row; // y
    public int col; // x

    public Position(int row, int col){
        this.row = row;
        this.col = col;
    }

    // Returns coords
    public int getRow(){
        return this.row;
    }
    public int getCol(){
        return this.col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Position other = (Position) obj;
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public String toString(){
        return "Row: " + this.row + " Col: " + this.col;
    }
}
