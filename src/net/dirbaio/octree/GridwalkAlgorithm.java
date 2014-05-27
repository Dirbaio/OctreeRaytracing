package net.dirbaio.octree;

public class GridwalkAlgorithm extends Algorithm
{
    public GridwalkAlgorithm(Octree o) {
        super(o);
    }

    @Override
    public int renderPixel(Vector3D rayStart, Vector3D rayDirection)
    {
        int stepX = (rayDirection.x > 0) ? 1 : -1;
        int stepY = (rayDirection.y > 0) ? 1 : -1;
        int stepZ = (rayDirection.z > 0) ? 1 : -1;

        int vx = (int)Math.floor(rayStart.x);
        int vy = (int)Math.floor(rayStart.y);
        int vz = (int)Math.floor(rayStart.z);

        double tDeltaX = rayDirection.x == 0 ? Double.POSITIVE_INFINITY : Math.abs(1/rayDirection.x);
        double tDeltaY = rayDirection.y == 0 ? Double.POSITIVE_INFINITY : Math.abs(1/rayDirection.y);
        double tDeltaZ = rayDirection.z == 0 ? Double.POSITIVE_INFINITY : Math.abs(1/rayDirection.z);
        double tMaxX = rayDirection.x == 0 ? Double.POSITIVE_INFINITY : ((rayDirection.x > 0) ? (1 + vx - x)*tDeltaX :(x - vx)*tDeltaX);
        double tMaxY = rayDirection.y == 0 ? Double.POSITIVE_INFINITY : ((rayDirection.y > 0) ? (1 + vy - y)*tDeltaY :(y - vy)*tDeltaY);
        double tMaxZ = rayDirection.z == 0 ? Double.POSITIVE_INFINITY : ((rayDirection.z > 0) ? (1 + vz - z)*tDeltaZ :(z - vz)*tDeltaZ);


        int iters = 0;
        int res = 0;
        while(res == 0 && iters < 30)
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

    @Override
    public String toString() {
        return "Gridwalk";
    }
}
