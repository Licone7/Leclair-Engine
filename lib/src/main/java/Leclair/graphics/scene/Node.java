package Leclair.graphics.scene;

import java.util.ArrayList;
import java.util.List;

import Leclair.graphics.light.AmbientLight;
import Leclair.graphics.light.Light;
import Leclair.math.Color;

/**
 * @since v1
 * @author Kane Burnett
 */
public class Node {

    public List<Mesh> meshes = new ArrayList<Mesh>();
    public List<Light> lights = new ArrayList<Light>();
    public List<Node> nodes = new ArrayList<Node>();

    public Node() {
        AmbientLight ambientLight = new AmbientLight(new Color(1, 0, 0, 0));
        lights.add(ambientLight);
    }

    public void addMesh(Mesh mesh) {
        meshes.add(mesh);
    }

    public void removeMesh(Mesh mesh) {
        meshes.remove(mesh);
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public void removeLight(Light light) {
        lights.remove(light);
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void removeNode(Node node) {
        nodes.remove(node);
    }
}
