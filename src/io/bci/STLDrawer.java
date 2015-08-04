package io.bci;

//copyright jasen ;)
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class STLDrawer {
	private FileChannel ch;
	private ByteBuffer bb;
	private int numFaces;
	private RandomAccessFile newFile;
	
	public STLDrawer(String stlFilename) throws FileNotFoundException
	{
		newFile = new RandomAccessFile(stlFilename,"rw");
		ch =newFile.getChannel();
		bb=  ByteBuffer.allocate(10000).order(ByteOrder.LITTLE_ENDIAN);		
		numFaces = 0;
		addHeader();
	}
	
	public void closeFile()
	{
		try {
			newFile.close();
			ch.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
        
        public String getFilename() throws IOException {
            return newFile.getFD().toString();
        }
	
	private void addHeader()
	{
		byte[] headerArray = new byte[80];
		bb.put(headerArray);
		bb.putInt(0); // maybe have to come back and alter retroactively? Number of faces..		
	}

	public void resizeNumTriangles()
	{
		ByteBuffer minibuffer =  ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
		minibuffer.putInt(numFaces); // maybe have to come back and alter retroactively? Number of faces..	
		minibuffer.flip();
		try {
			ch.position(80);
			ch.write(minibuffer);
			minibuffer.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	// only draw Triangles in STLs, all drawing methods should call this one
	public void drawTriangle(float[] pofloat1, float[] pofloat2, float[] pofloat3)
	{
		float[] vec1 = new float[3];
		float[] vec2 = new float[3];
		float[] normal = new float[3];
		short emptybyte = 0;
		vec1 = getVector(pofloat2,pofloat1);
		vec2 = getVector(pofloat3,pofloat1);
		
		normal = crossProduct(vec1,vec2);
		bb.putFloat(normal[0]).putFloat(normal[1]).putFloat(normal[2]);
		bb.putFloat(pofloat1[0]).putFloat(pofloat1[1]).putFloat(pofloat1[2]);
		bb.putFloat(pofloat2[0]).putFloat(pofloat2[1]).putFloat(pofloat2[2]);
		bb.putFloat(pofloat3[0]).putFloat(pofloat3[1]).putFloat(pofloat3[2]);
		bb.putShort(emptybyte);
		bb.flip();
		try {
			ch.write(bb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bb.clear();		
		
		numFaces++;
	}
	
	
	public void drawSquare(float[] pofloat1, float[] pofloat2, float[] pofloat3,float[] pofloat4)
	{
		drawTriangle(pofloat1,pofloat2,pofloat3);
		drawTriangle(pofloat3,pofloat4,pofloat1);
	}
	
	public void drawCube(float[] pofloat1, float[] pofloat2, float[] pofloat3,float[] pofloat4,float depth)
	{
		drawTop(pofloat1, pofloat2, pofloat3,pofloat4,depth);
		drawBack(pofloat1, pofloat2, pofloat3,pofloat4,depth);
		drawFront(pofloat1, pofloat2, pofloat3,pofloat4,depth);
		drawLeft(pofloat1, pofloat2, pofloat3,pofloat4,depth);
		drawRight(pofloat1, pofloat2, pofloat3,pofloat4,depth);
		drawBottom(pofloat1, pofloat2, pofloat3,pofloat4,depth);
		
	}
	
	public void drawTop(float[] pofloat1, float[] pofloat2, float[] pofloat3,float[] pofloat4,float depth)
	{
		drawSquare(pofloat3,pofloat4,addSingleValue(pofloat4,'z',depth),addSingleValue(pofloat3,'z',depth)); //top		
	}
	public void drawBack(float[] pofloat1, float[] pofloat2, float[] pofloat3,float[] pofloat4,float depth)
	{
		drawSquare(addSingleValue(pofloat3,'z',depth),addSingleValue(pofloat4,'z',depth),addSingleValue(pofloat1,'z',depth),addSingleValue(pofloat2,'z',depth)); //back
	}
	public void drawFront(float[] pofloat1, float[] pofloat2, float[] pofloat3,float[] pofloat4,float depth)
	{
		drawSquare(pofloat4,pofloat3,pofloat2,pofloat1);//front
	}
	public void drawLeft(float[] pofloat1, float[] pofloat2, float[] pofloat3,float[] pofloat4,float depth)
	{
		drawSquare(pofloat4,pofloat1,addSingleValue(pofloat1,'z',depth),addSingleValue(pofloat4,'z',depth)); //left
	}
	public void drawRight(float[] pofloat1, float[] pofloat2, float[] pofloat3,float[] pofloat4,float depth)
	{
		drawSquare(pofloat2,pofloat3,addSingleValue(pofloat3,'z',depth),addSingleValue(pofloat2,'z',depth)); // right
	}
	public void drawBottom(float[] pofloat1, float[] pofloat2, float[] pofloat3,float[] pofloat4,float depth)
	{
		drawSquare(pofloat1,pofloat2,addSingleValue(pofloat2,'z',depth),addSingleValue(pofloat1,'z',depth)); //bottom
	}
		
	
	private float[] addSingleValue(float[] point,char dimension, float depth)
	{
		float[] temp = new float[3];
		
		temp[0] = point[0];
		temp[1] = point[1];
		temp[2] = point[2];
		
		if (dimension == 'x')
			temp[0] += depth;
		else if (dimension == 'y')
			temp[1] += depth;
			else
				temp[2] += depth;
		
		return temp;
	}
	
	//simple point subtraction
	private  float[] getVector(float[] p1, float[] p2)
	{
		float[] vector =  new float[3];
		
		for (int ii = 0; ii <3; ii++)
			vector[ii] = p1[ii] - p2[ii];
		
		return vector;
		
	}
	
	//used for calculating normal
	private float[] crossProduct(float[] v1, float[] v2)
	{
		float[] dotProd = new float[3];
		
		dotProd[0] = v1[1]*v2[2] - v1[2]*v2[1];
		dotProd[1] = v1[2]*v2[0]- v1[0]*v2[2];
		dotProd[2] = v1[0]*v2[1]- v1[1]*v2[0];
		
		return dotProd;
	}
}
