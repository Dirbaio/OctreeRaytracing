package net.dirbaio.octree;

/**
 * A class to describe a two or three dimensional vector.
 * <br>
 * Created for use in examples from the Nature of Code course at ITP.
 * <p>
 * <a href="http://www.shiffman.net">http://www.shiffman.net/</a>
 * <br>
 * <a href="http://www.shiffman.net/teaching/the-nature-of-code">http://www.shiffman.net/teaching/the-nature-of-code</a>
 */

public class Vector3D {
    /**
     * The x component of the vector.
     */
    public double x;
    /**
     * The y component of the vector.
     */
    public double y;
    /**
     * The z component of the vector.
     */
    public double z;

    /**
     * Constructor for a 3D vector.
     *
     * @param  x_ the x coordinate.
     * @param  y_ the y coordinate.
     * @param  z_ the y coordinate.
     */

    public Vector3D(double x_, double y_, double z_) {
        x = x_; y = y_; z = z_;
    }

    /**
     * Constructor for a 2D vector: z coordinate is set to 0.
     *
     * @param  x_ the x coordinate.
     * @param  y_ the y coordinate.
     */

    public Vector3D(double x_, double y_) {
        x = x_; y = y_; z = 0f;
    }

    /**
     * Constructor for an empty vector: x, y, and z are set to 0.
     */

    public Vector3D() {
        x = 0f; y = 0f; z = 0f;
    }

    /**
     * Set the x coordinate.
     *
     *  @param  x_ the x coordinate.
     */

    public void setX(double x_) {
        x = x_;
    }

    /**
     * Set the y coordinate.
     *
     *  @param  y_ the y coordinate.
     */
    public void setY(double y_) {
        y = y_;
    }

    /**
     * Set the z coordinate.
     *
     *  @param  z_ the z coordinate.
     */
    public void setZ(double z_) {
        z = z_;
    }

    /**
     * Set x,y, and z coordinates.
     *
     *  @param  x_ the x coordinate.
     *  @param  y_ the y coordinate.
     *  @param  z_ the z coordinate.
     */
    public void setXYZ(double x_, double y_, double z_) {
        x = x_;
        y = y_;
        z = z_;
    }

    /**
     * Set x,y, and z coordinates from a Vector3D object.
     *
     *  @param  v the Vector3D object to be copied
     */
    public void setXYZ(Vector3D v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    /**
     * Calculate the magnitude (length) of the vector
     * @return      the magnitude of the vector    
     */
    public double magnitude() {
        return (double) Math.sqrt(x*x + y*y + z*z);
    }

    /**
     * Copy the vector
     * @return      a copy of the vector   
     */
    public Vector3D copy() {
        return new Vector3D(x,y,z);
    }

    /**
     * Copy the vector
     * @param      v the vector to be copied   
     * @return      a copy of the vector   
     */
    public static Vector3D copy(Vector3D v) {
        return new Vector3D(v.x, v.y,v.z);
    }

    /**
     * Add a vector to this vector
     * @param      v the vector to be added  
     */
    public void add(Vector3D v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    /**
     * Subtract a vector from this vector
     * @param      v the vector to be subtracted  
     */
    public void sub(Vector3D v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    /**
     * Multiply this vector by a scalar
     * @param      n the value to multiply by 
     */
    public void mult(double n) {
        x *= n;
        y *= n;
        z *= n;
    }

    /**
     * Divide this vector by a scalar
     * @param      n the value to divide by 
     */
    public void div(double n) {
        x /= n;
        y /= n;
        z /= n;
    }


    /**
     * Calculate the dot product with another vector
     * @return  the dot product
     */
    public double dot(Vector3D v) {
        double dot = x*v.x + y*v.y + z*v.z;
        return dot;
    }

    /**
     * Calculate the cross product with another vector
     * @return  the cross product
     */
    public Vector3D cross(Vector3D v) {
        double crossX = y * v.z - v.y * z;
        double crossY = z * v.x - v.z * x;
        double crossZ = x * v.y - v.x * y;
        return(new Vector3D(crossX,crossY,crossZ));
    }

    /**
     * Normalize the vector to length 1 (make it a unit vector)
     */
    public void normalize() {
        double m = magnitude();
        if (m > 0) {
            div(m);
        }
    }

    /**
     * Limit the magnitude of this vector
     * @param max the maximum length to limit this vector
     */
    public void limit(double max) {
        if (magnitude() > max) {
            normalize();
            mult(max);
        }
    }

    /**
     * Calculate the angle of rotation for this vector (only 2D vectors)
     * @return the angle of rotation
     */
    public double heading2D() {
        double angle = (double) Math.atan2(-y, x);
        return -1*angle;
    }

    /**
     * Rotates a 2D Vector
     * @param theta, angle in radians to rotate vector
     */
    public void rotate2D(double theta) {
        double currentTheta = heading2D();
        double mag = magnitude();
        currentTheta += theta;
        x = (double) (mag*Math.cos(currentTheta));
        y = (double) (mag*Math.sin(currentTheta));
    }


    /**
     * Add two vectors
     * @param      v1 a vector
     * @param v2 another vector   
     * @return a new vector that is the sum of v1 and v2  
     */
    public static Vector3D add(Vector3D v1, Vector3D v2) {
        Vector3D v = new Vector3D(v1.x + v2.x,v1.y + v2.y, v1.z + v2.z);
        return v;
    }

    /**
     * Subtract one vector from another
     * @param      v1 a vector
     * @param v2 another vector   
     * @return a new vector that is v1 - v2 
     */
    public static Vector3D sub(Vector3D v1, Vector3D v2) {
        Vector3D v = new Vector3D(v1.x - v2.x,v1.y - v2.y,v1.z - v2.z);
        return v;
    }

    /**
     * Divide a vector by a scalar
     * @param      v1 a vector
     * @param n scalar 
     * @return a new vector that is v1 / n
     */
    public static Vector3D div(Vector3D v1, double n) {
        Vector3D v = new Vector3D(v1.x/n,v1.y/n,v1.z/n);
        return v;
    }

    /**
     * Multiply a vector by a scalar
     * @param      v1 a vector
     * @param n scalar 
     * @return a new vector that is v1 * n
     */
    public static Vector3D mult(Vector3D v1, double n) {
        Vector3D v = new Vector3D(v1.x*n,v1.y*n,v1.z*n);
        return v;
    }

    /**
     * Rotates a 2D Vector
     * @param theta, angle in radians to rotate vector
     * @return a new Vector object, rotated by theta
     */
    public static Vector3D rotate2D(Vector3D v, double theta) {
        // What is my current heading
        double currentTheta = v.heading2D();
        // What is my current speed
        double mag = v.magnitude();
        // Turn me
        currentTheta += theta;
        // Look, polar coordinates to cartesian!!
        Vector3D newV = new Vector3D((double) (mag*Math.cos(currentTheta)),(double) (mag*Math.cos(currentTheta)));
        return  newV;
    }

    /**
     * Calculate the Euclidean distance between two points (considering a point as a vector object)
     * @param      v1 a vector
     * @param v2 another vector
     * @return the Euclidean distance between v1 and v2
     */
    public static double distance (Vector3D v1, Vector3D v2) {
        double dx = v1.x - v2.x;
        double dy = v1.y - v2.y;
        double dz = v1.z - v2.z;
        return (double) Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    /**
     * Calculate the angle between two vectors, using the dot product
     * @param      v1 a vector
     * @param v2 another vector
     * @return the angle between the vectors
     */
    public static double angleBetween(Vector3D v1, Vector3D v2) {
        double dot = v1.dot(v2);
        double theta = (double) Math.acos(dot / (v1.magnitude() * v2.magnitude()));
        return theta;
    }

}

