
public class Piece {
	public int pieceIndex;
	public byte[] actualPiece;
	public int hasPiece=0;
	public int fromWho;
	
	public Piece(int pieceIndex, byte[] actualPiece)
	{
		this.pieceIndex=pieceIndex;
		this.actualPiece=actualPiece;
	}
	
	public Piece()
	{
		pieceIndex=-1;
		actualPiece=new byte[Configuration.CommonProperties.PieceSize];
		fromWho= 0;
	}
	
	public static Piece receive(byte []payload)
	{
		Piece piece = new Piece();
		byte[] byteIndex = new byte[4];
		System.arraycopy(payload, 0, byteIndex, 0, 4);
		piece.pieceIndex = ActualMessage.byteArrayToInt(byteIndex, 0);
		piece.actualPiece = new byte[payload.length-4];
		System.arraycopy(payload, 4, piece.actualPiece, 0, payload.length-4);		
		return piece;
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
