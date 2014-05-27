package net.dirbaio.octree;

import java.util.Vector;

import static java.lang.Math.*;

public class OctreeAlgorithm extends Algorithm {
    public OctreeAlgorithm(Octree o) {
        super(o);
    }

    public static final int MAX_STACK_SIZE = 10;
    public static final int scaleMax = MAX_STACK_SIZE;
    public static final double minNormal = 0.0001f;
    public static final double epsilon = 0.00001f;
    public static final int maxLoops = (scaleMax + 1) * (scaleMax + 1) * 5;

    private static class StackElement {
        Octree.Node parentNode;
        double parentTMin;
        double parentTMax;
        Vector3D parentCenter;
    }


    @Override
    public int renderPixel(Vector3D rayOrigin, Vector3D rayDirection) {
       /* bool
        sample( __global const SvoNode* svo,
                double3 rayOrigin,
                double3 rayDirection,
        const double tScaleRatio,
        SampleResult* result)
        {*/

        double tMin, tMax;
        //if (!intersectAABB(rayOrigin, rayDirection, & tMin,&tMax)){
        //    return false;
        //}

        int scale = scaleMax - 1;
        double scale_exp2 = 0.5f;// exp2f(scale - s_max)

        StackElement[] stack = new StackElement[MAX_STACK_SIZE];

        // init stack with root node
        stack[scale].parentNode = o.root;
        stack[scale].parentTMin = tMin;
        stack[scale].parentTMax = tMax;
        stack[scale].parentCenter = new Vector3D(0, 0, 0);

        if (abs(rayDirection.x) < minNormal)
            rayDirection.x = minNormal * signum(rayDirection.x);
        if (abs(rayDirection.y) < minNormal)
            rayDirection.y = minNormal * signum(rayDirection.y);
        if (abs(rayDirection.z) < minNormal)
            rayDirection.z = minNormal * signum(rayDirection.z);

//  rayDirection = (abs(rayDirection.x) < epsilon) ? epsilon * sign(rayDirection.x) : rayDirection.x;


        // precalculate ray coefficients, tx(x) = "(1/dx)"x + "(-px/dx)"
        double dxReziprok = 1.0f / rayDirection.x;
        double minusPx_dx = -rayOrigin.x * dxReziprok;

        double dyReziprok = 1.0f / rayDirection.y;
        double minusPy_dy = -rayOrigin.y * dyReziprok;

        double dzReziprok = 1.0f / rayDirection.z;
        double minusPz_dz = -rayOrigin.z * dzReziprok;

        double childSizeHalf;

        StackElement parent = null;

        int whileCounter = 0;

/////////////////// LOOP ///////////////////////////////XS

        while (scale < scaleMax) {

            parent = stack[scale];
            scale_exp2 = pow(2.0, scale - scaleMax);
            childSizeHalf = scale_exp2 * 0.5;

            ++whileCounter;
            if (whileCounter == maxLoops) {
                result.hit = true;
                result.nodeIndex = parent.parentNodeIndex;
                result.depth = scaleMax - scale;
                result.t = parent.parentTMin;
                result.numWhileLoops = whileCounter;
                result.nodeCenter = parent.parentCenter;
                result.quality = scale_exp2 - (tScaleRatio * parent.parentTMin);
                return true;
            }


            // ### POP if parent is behind the camera
            if (parent.parentTMax < 0.0f) {
                ++scale;
                continue;
            }

            if (abs(parent.parentTMin - parent.parentTMax) > epsilon) {
                // childEntryPoint in parent voxel coordinates
                //double3 childEntryPoint = (rayOrigin + (parent.parentTMin + epsilon) * rayDirection) - parent.parentCenter;
                Vector3D a = rayDirection.copy();
                a.mult(parent.parentTMin + epsilon);
                Vector3D childEntryPoint = rayOrigin.copy();
                childEntryPoint.add(a);
                childEntryPoint.sub(parent.parentCenter);

                int childIdx = (int) (4 * (childEntryPoint.x > 0.0 ? 1:0)
                        + 2 * (childEntryPoint.y > 0.0 ? 1:0)
                        + (childEntryPoint.z > 0.0 ? 1:0));

                // childCenter in world coordinates
                double3 childCenter = (double3) (-0.5f + (bool) (childIdx & 4),
                -0.5f + (bool) (childIdx & 2),
                        -0.5f + (bool) (childIdx & 1))*scale_exp2 + parent.parentCenter;

                // tx(x) = (1/dx)x + (-px/dx)
                // dx...Direction of the Ray in x
                // px...Origin of the Ray in x
                double tx0 = dxReziprok * (childCenter.x - childSizeHalf) + minusPx_dx;
                double tx1 = dxReziprok * (childCenter.x + childSizeHalf) + minusPx_dx;
                swapToIncreasing( & tx0,&tx1);
                double ty0 = dyReziprok * (childCenter.y - childSizeHalf) + minusPy_dy;
                double ty1 = dyReziprok * (childCenter.y + childSizeHalf) + minusPy_dy;
                swapToIncreasing( & ty0,&ty1);
                double tz0 = dzReziprok * (childCenter.z - childSizeHalf) + minusPz_dz;
                double tz1 = dzReziprok * (childCenter.z + childSizeHalf) + minusPz_dz;
                swapToIncreasing( & tz0,&tz1);

                double tcMin = max(tx0, max(ty0, tz0)); // <- you can only enter once
                double tcMax = min(tx1, min(ty1, tz1)); // <- you can only leave once

                // if child is valid
                if (getValidMaskFlag(svo[parent.parentNodeIndex]._masks, childIdx)) {
                    // TERMINATE if voxel is a leaf
                    if (getLeafMaskFlag(svo[parent.parentNodeIndex]._masks, childIdx)) {
                        unsigned leafIndex = parent.parentNodeIndex + getNthchildIdx(svo[parent.parentNodeIndex]._masks,
                                svo[parent.parentNodeIndex]._firstchildIndex,
                                childIdx);

                        // else: return leaf
                        result.hit = true;
                        result.nodeIndex = leafIndex;
                        result.depth = scaleMax - scale;
                        result.t = parent.parentTMin;
                        result.numWhileLoops = whileCounter;
                        result.nodeCenter = childCenter;
                        result.quality = scale_exp2 - (tScaleRatio * tcMin);
                        return true;
                    } else {
                        // TERMINATE if voxel is small enough
                        if (tScaleRatio * tcMin > scale_exp2 || scaleMax - scale == MAX_SVO_RAYCAST_DEPTH) {
                            unsigned returnchildIdx = parent.parentNodeIndex + getNthchildIdx(svo[parent.parentNodeIndex]._masks,
                                    svo[parent.parentNodeIndex]._firstchildIndex,
                                    childIdx);
                            result.hit = true;
                            result.nodeIndex = returnchildIdx;
                            result.depth = scaleMax - scale + 1;
                            result.t = parent.parentTMin;
                            result.numWhileLoops = whileCounter;
                            result.nodeCenter = childCenter;
                            result.quality = 0.0f;
                            return true;
                        }

                        // update parent befor push
                        parent.parentTMin = tcMax;

                        // ### PUSH
                        --scale;
                        stack[scale].parentNodeIndex = parent.parentNodeIndex + getNthchildIdx(svo[parent.parentNodeIndex]._masks, // <- relative indexing
                                svo[parent.parentNodeIndex]._firstchildIndex,
                                childIdx);
                        stack[scale].parentTMin = tcMin;
                        stack[scale].parentTMax = tcMax;
                        stack[scale].parentCenter = childCenter;
                        continue;
                    }
                } else {
                    // ### ADVANCE
                    parent.parentTMin = tcMax;
                }
            } else {
                // POP
                ++scale;
                continue;
            }

        } // while


        result.hit = false;
        result.nodeIndex = 0;
        result.attribIndex = 0;
        result.depth = 0;
        result.t = tMax;
        result.numWhileLoops = whileCounter;


        return false;
    }

    @Override
    public String toString() {
        return "Octree";
    }
}
