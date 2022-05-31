package Leclair.graphics.scene;

import static Leclair.graphics.GraphicsInfo.getRenderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import Leclair.graphics.material.Material;
import Leclair.math.Vector3;
import org.joml.Matrix4f;

/**
 * @since v1
 * @author Kane Burnett
 */
public class Mesh {

    public static List<Mesh> meshes = new ArrayList<>();

    private int vertexNumber;
    private FloatBuffer data;
    public int index;
    public Material material;
    public Matrix4f transMat;
    public Vector3 position;
    byte state = RenderStates.STATE_UNPROCESSED;

    public Mesh(FloatBuffer vertices, Material material, Vector3 pos, boolean process) {
        this.vertexNumber = vertices.capacity();
        this.data = vertices;
        this.material = material;
        this.position = pos;
        transMat = new Matrix4f().identity().translateLocal(position.getX(), position.getY(), position.getZ());
        // .rotate(1, 1, 1, 1).scale(1);
        if (process) {
            process();
        }
    }

    // public Mesh(int vertexNumber) {
    //     meshes.add(this);
    //     this.index = meshes.indexOf(this);
    //     this.vertexNumber = vertexNumber;
    // }

    public void process() {
        meshes.add(this);
        this.index = meshes.indexOf(this);
        getRenderer().processMesh(this);
        setState(RenderStates.STATE_PROCESS);
    }

    public void render() {
        getRenderer().renderMesh(this);
        setState(RenderStates.STATE_RENDER);
    }

    public void remove() {
        getRenderer().removeMesh(this);
        setState(RenderStates.STATE_REMOVE);
    }

    public void delete() {
        getRenderer().deleteMesh(this);
        setState(RenderStates.STATE_DELETE);
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

    public void setState(byte state) {
        this.state = state;
    }

    public byte getState() {
        return this.state;
    }
}
