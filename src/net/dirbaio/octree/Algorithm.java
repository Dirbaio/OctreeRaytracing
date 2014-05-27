package net.dirbaio.octree;

public abstract class Algorithm
{
    protected final Octree o;
    public Algorithm(Octree o)
    {
        this.o = o;
    }

    public abstract int renderPixel(Vector3D rayStart, Vector3D rayDirection);

}
