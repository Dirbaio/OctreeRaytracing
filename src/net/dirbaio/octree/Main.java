package net.dirbaio.octree;

public class Main
{
	public static void main(String[] args)
	{
		//8x8x8 octree filled with 0's
		Octree o = new Octree(3, 0);

		o.set(10, 0, 0, 0);
		o.set(1234, 0, 0, 1);
		System.out.println(o.get(0, 0, 0));
		System.out.println(o.get(0, 0, 1));
		System.out.println(o.get(0, 0, 2));
	}
}
