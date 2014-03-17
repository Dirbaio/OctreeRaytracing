package net.dirbaio.octree;


public class Octree
{
	private OctreeNode root;
	private int size;

	public Octree(int size, int fill)
	{
		if(size == 0)
			throw new RuntimeException("Minimum octree size is 2x2");

		this.size = size;
		root = new OctreeNode(fill);
	}

	public int get(int x, int y, int z)
	{
		if(x < 0 || x >= (1<<size)) throw new RuntimeException("Coord out of bounds");
		if(y < 0 || y >= (1<<size)) throw new RuntimeException("Coord out of bounds");
		if(y < 0 || z >= (1<<size)) throw new RuntimeException("Coord out of bounds");
		return root.get(x, y, z, size-1);
	}
	public void set(int val, int x, int y, int z)
	{
		if(x < 0 || x >= (1<<size)) throw new RuntimeException("Coord out of bounds");
		if(y < 0 || y >= (1<<size)) throw new RuntimeException("Coord out of bounds");
		if(y < 0 || z >= (1<<size)) throw new RuntimeException("Coord out of bounds");
		root.set(val, x, y, z, size-1);
	}

	private static final class OctreeNode
	{
		private OctreeNode[] children;
		private int[] values;

		public OctreeNode(int val)
		{
			children = new OctreeNode[8];
			values = new int[8];
			for(int i = 0; i < 8; i++)
				values[i] = val;
		}

		public int get(int x, int y, int z, int size)
		{

			int xx = (x >> size) & 1;
			int yy = (y >> size) & 1;
			int zz = (z >> size) & 1;
			int ind = xx | (yy<<1) | (zz<<2);

			if(children[ind] != null)
				return children[ind].get(x, y, z, size-1);
			else
				return values[ind];
		}

		public void set(int val, int x, int y, int z, int size)
		{
			int xx = (x >> size) & 1;
			int yy = (y >> size) & 1;
			int zz = (z >> size) & 1;
			int ind = xx | (yy<<1) | (zz<<2);

			//If quadrants are 1x1 size, modify it directly.
			if(size == 0)
				values[ind] = val;

				//If quadrant is collapsed
			else if(children[ind] == null)
			{
				//Only need to modify if we're writing a different value.
				if(val != values[ind])
				{
					children[ind] = new OctreeNode(values[ind]);
					children[ind].set(val, x, y, z, size-1);

					//No need to check for collapsible, we know the child won't be collapsible.
				}
			}

			//If quadrant not collapsed
			else
			{
				//Modify it
				children[ind].set(val, x, y, z, size-1);

				//If we can collapse it, do it
				if(children[ind].isCollapsible())
				{
					values[ind] = children[ind].values[0];
					children[ind] = null;
				}
			}
		}

		private boolean isCollapsible()
		{
			for(int i = 0; i < 8; i++)
			{
				if(children[i] != null)
					return false;
				if(values[i] != values[0])
					return false;
			}
			return true;
		}
	}

}
