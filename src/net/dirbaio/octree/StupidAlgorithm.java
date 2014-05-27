package net.dirbaio.octree;

import com.sun.org.apache.regexp.internal.recompile;

public class StupidAlgorithm extends Algorithm
{
    public StupidAlgorithm(Octree o) {
        super(o);
    }

    @Override
    public int renderPixel(Vector3D rayStart, Vector3D rayDirection)
    {
        double step = 0.1;
        int iters = 0;

        rayDirection.mult(step);
        while(iters < 100)
        {
            rayStart.add(rayDirection);
            int val = o.get((int)Math.floor(rayStart.x), (int)Math.floor(rayStart.y), (int)Math.floor(rayStart.z));
            if(val != 0)
                return val;

            iters++;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Stupid";
    }
}
