
public class Piece {
	public static int pieceIndex;
	public static byte[] actualPiece;
	public static int hasPiece=0;
	public static String fromWho;
	
	public Piece(int pieceIndex, byte[] actualPiece)
	{
		this.pieceIndex=pieceIndex;
		this.actualPiece=actualPiece;
	}
	
	public Piece()
	{
		pieceIndex=-1;
		actualPiece=new byte[Configuration.CommonProperties.PieceSize];
		fromWho= null;
	}
	
	public int isThere()
	{
		return hasPiece;
	}
	
	public void setIsThere(int pieceIndex)
	{
		if(this.pieceIndex==pieceIndex)
			hasPiece = 1;
	}

}
