package net.dirbaio.octree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class Main extends JComponent implements MouseListener, KeyListener
{
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;

	public static final int STUPID = 0;
	public static final int GRIDWALK = 1;
	public static final int OCTREEWALK = 2;
	public static final String[] ALGORITHM_NAMES = {"Stupid", "Grid walk", "Octree walk"};
	int algorithm = GRIDWALK;

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


		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		reRender();

		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
	}

	void reRender()
	{
		long time = System.nanoTime();
		render();
		time = System.nanoTime()-time;
		rendertime = time/1000000;

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

	int renderPixel(double px, double py)
	{
		//Pos y dir del rayo
		double x = 4;
		double y = 4;
		double z = -5;

		double dx = px;
		double dy = py;
		double dz = 1;

		if(algorithm == STUPID)
		{
			double step = 0.1;

			while(z < 10)
			{
				x += dx*step;
				y += dy*step;
				z += dz*step;
				int val = o.get((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
				if(val != 0)
					return val;
			}
			return 0;
		}
		else if(algorithm == GRIDWALK)
		{
			int stepX = (dx > 0) ? 1 : -1;
			int stepY = (dy > 0) ? 1 : -1;
			int stepZ = (dz > 0) ? 1 : -1;

			int vx = (int)Math.floor(x);
			int vy = (int)Math.floor(y);
			int vz = (int)Math.floor(z);

			double tDeltaX = dx == 0 ? Double.POSITIVE_INFINITY : Math.abs(1/dx);
			double tDeltaY = dy == 0 ? Double.POSITIVE_INFINITY : Math.abs(1/dy);
			double tDeltaZ = dz == 0 ? Double.POSITIVE_INFINITY : Math.abs(1/dz);
			double tMaxX = dx == 0 ? Double.POSITIVE_INFINITY : ((dx > 0) ? (1 + vx - x)*tDeltaX :(x - vx)*tDeltaX);
			double tMaxY = dy == 0 ? Double.POSITIVE_INFINITY : ((dy > 0) ? (1 + vy - y)*tDeltaY :(y - vy)*tDeltaY);
			double tMaxZ = dz == 0 ? Double.POSITIVE_INFINITY : ((dz > 0) ? (1 + vz - z)*tDeltaZ :(z - vz)*tDeltaZ);


			int iters = 0;
			int res = 0;
			while(vz < 10 && res == 0)
			{
				iters++;
				res = o.get(vx, vy, vz);

				if(tMaxX < tMaxY && tMaxX < tMaxZ) //Avanzar x
				{
					tMaxX += tDeltaX;
					vx += stepX;
				}
				else if(tMaxY < tMaxZ)
				{
					tMaxY += tDeltaY;
					vy += stepY;
				}
				else
				{
					tMaxZ += tDeltaZ;
					vz += stepZ;
				}
			}
			return res; //scale(iters*10);
		}
		else
			return 0xFF0000;
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
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.drawImage(img, 0, 0, null);
		g.setColor(Color.WHITE);
		g.drawString("Algorithm: "+ ALGORITHM_NAMES[algorithm], 10, 20);
		g.drawString("Rendered in " + rendertime + " ms.", 10, 35);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{

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
			algorithm = STUPID;
		if(e.getKeyChar() == '2')
			algorithm = GRIDWALK;
		if(e.getKeyChar() == '3')
			algorithm = OCTREEWALK;

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
}
