package Leclair.graphics.scene;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import Leclair.graphics.material.Material;
import Leclair.math.Vector3;
import org.joml.Matrix4f;

public class Mesh {

    public static List<Mesh> meshes = new ArrayList<>();

    private int vertexNumber;
    private FloatBuffer data;
    public int index;
    public Material material;
    public Matrix4f transMat;
    public Vector3 position;
    public boolean initialized = false;

    public Mesh(FloatBuffer vertices, Material material, Vector3 pos) {
        meshes.add(this);
        this.index = meshes.indexOf(this);
        this.vertexNumber = vertices.capacity();
        this.data = vertices;
        this.material = material;
        this.position = pos;
        transMat = new Matrix4f().identity().translateLocal(position.getX(), position.getY(), position.getZ());
        // .rotate(1, 1, 1, 1).scale(1);
    }

    public Mesh(int vertexNumber) {
        meshes.add(this);
        this.index = meshes.indexOf(this);
        this.vertexNumber = vertexNumber;
    }

    public int getVertexNumber() {
        return this.vertexNumber;
    }

    public FloatBuffer getData() {
        return this.data;
    }

    public static List<Mesh> getMeshes() {
        return meshes;
    }
}
