package com.creedvi.utils.gltfj.gltf;

import java.util.ArrayList;

public class gltfj_Node {

    public String name;

    public int skin;
    public int mesh;
    public int camera;
    public int light;
    public ArrayList<gltfj_Extension> extensions;

    public double[] rotation, scale, translation, matrix, weights;
    public int[] children;
    public int parent, childrenCount, weightsCount, extensionsCount;

    public gltfj_Extras extras;

    public gltfj_Node() {
        extensions = new ArrayList<>();

        rotation = new double[4];
        scale = new double[3];
        translation = new double[3];
        matrix = new double[16];

        extras = new gltfj_Extras();
    }

}
