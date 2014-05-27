package net.dirbaio.octree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Main extends JComponent implements MouseListener, MouseMotionListener, KeyListener
{
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;

    Algorithm algo;

	Octree o;
	BufferedImage img;
	long rendertime;

	public Main()
	{

		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setSize(getPreferredSize());

		//8x8x8 octree filled with 0's
		o = new Octree(3, 0);

		for(int x = 0; x < 8; x++)
			for(int y = 0; y < 8; y++)
				for(int z = 0; z < 8; z++)
					if(Math.random() < 0.3)
						o.set(x, y, z, (int)(Math.random()*100000000));

        algo = new StupidAlgorithm(o);

		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		reRender();

		addMouseListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
		setFocusable(true);
	}

	boolean dirty = true;
	void reRender()
	{
		dirty = true;
		repaint();
	}

	int fromRGB(int r, int g, int b)
	{
		if(r < 0) r = 0;
		if(r > 255) r = 255;
		if(g < 0) g = 0;
		if(g > 255) g = 255;
		if(b < 0) b = 0;
		if(b > 255) b = 255;
		return b | (g << 8) | (r << 16);
	}

	int scale(int val)
	{
		if(val < 256)
			return fromRGB(0, 0, val);
		else if(val < 256*2)
			return fromRGB(0, val-256, 255);
		else
			return fromRGB(val-512, 255, 255);
	}

	double rotx = 0;
	double roty = 0;

	double viewX;
	double viewY;
	double viewZ;
	double viewXTop;
	double viewYTop;
	double viewZTop;
	double viewXRight;
	double viewYRight;
	double viewZRight;

	double len(double x, double y, double z)
	{
		return Math.sqrt(x*x+y*y+z*z);
	}
	void calcView()
	{
		viewX = Math.sin(rotx)*Math.cos(roty);
		viewZ = Math.cos(rotx)*Math.cos(roty);
		viewY = Math.sin(roty);

		double upX = 0;
		double upY = 1;
		double upZ = 0;

		//viewRight = view x up
		viewXRight = viewY * upZ - viewZ * upY;
		viewYRight = viewZ * upX - viewX * upZ;
		viewZRight = viewX * upY - viewY * upX;

		double rightLen = 1.0/len(viewXRight, viewYRight, viewZRight);
		viewXRight*= rightLen;
		viewYRight*= rightLen;
		viewZRight*= rightLen;

		//viewTop = viewRight x view
		viewXTop = viewYRight * viewZ - viewZRight * viewY;
		viewYTop = viewZRight * viewX - viewXRight * viewZ;
		viewZTop = viewXRight * viewY - viewYRight * viewX;

		double topLen = 1.0/len(viewXTop, viewYTop, viewZTop);
		viewXTop*= topLen;
		viewYTop*= topLen;
		viewZTop*= topLen;

	}
	int renderPixel(double px, double py)
	{
		calcView();

		//Pos y dir del rayo
		double dx = viewX + viewXRight * px + viewXTop * py;
		double dy = viewY + viewYRight * px + viewYTop * py;
		double dz = viewZ + viewZRight * px + viewZTop * py;

		double x = 4 - viewX*9;
		double y = 4 - viewY*9;
		double z = 4 - viewZ*9;

        return algo.renderPixel(new Vector3D(x, y, z), new Vector3D(dx, dy, dz));
	}

	void render()
	{
		for(int py = 0; py < HEIGHT; py++)
			for(int px = 0; px < WIDTH; px++)
			{
				double dx = ((double)(px*2-WIDTH)) / WIDTH;
				double dy = ((double)(HEIGHT-py*2)) / HEIGHT;
				img.setRGB(px, py, renderPixel(dx, dy));
			}
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		if(dirty)
		{
			dirty = false;

			long time = System.nanoTime();
			render();
			time = System.nanoTime()-time;
			rendertime = time/1000000;
		}
		g.drawImage(img, 0, 0, null);
		g.setColor(Color.WHITE);
		g.drawString("Algorithm: "+ algo, 10, 20);
		g.drawString("Rendered in " + rendertime + " ms.", 10, 35);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		omx = e.getX();
		omy = e.getY();

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	public static void main(String[] args) {
		System.out.println("Hello World!");

		JFrame f = new JFrame("Octree Test");
		f.add(new Main(), BorderLayout.CENTER);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		if(e.getKeyChar() == '1')
            algo = new StupidAlgorithm(o);
		if(e.getKeyChar() == '2')
            algo = new GridwalkAlgorithm(o);
		if(e.getKeyChar() == '3')
            algo = new OctreeAlgorithm(o);

		reRender();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{

	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	int omx = 0;
	int omy = 0;
	double rotspeed = 0.006;
	@Override
	public void mouseDragged(MouseEvent e)
	{

		rotx -= (e.getX()-omx)*rotspeed;
		roty -= (e.getY()-omy)*rotspeed;
		if(roty < -Math.PI*0.49) roty = -Math.PI*0.49;
		if(roty > Math.PI*0.49) roty = Math.PI*0.49;
		reRender();

		omx = e.getX();
		omy = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}
}
