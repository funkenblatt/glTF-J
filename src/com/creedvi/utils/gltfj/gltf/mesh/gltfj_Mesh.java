package com.creedvi.utils.gltfj.gltf.mesh;

import com.creedvi.utils.gltfj.gltf.gltfj_Extension;
import com.creedvi.utils.gltfj.gltf.gltfj_Extras;

import java.util.ArrayList;

public class gltfj_Mesh {

    public String name;
    public String[] targetNames;

    public double[] weights;
    public ArrayList<gltfj_Primitive> primitives;
    public ArrayList<gltfj_Extension> extensions;

    public int primitivesCount, weightsCount, targetNamesCount, extensionsCount;

    public gltfj_Extras extras;


    public gltfj_Mesh() {
        primitives = new ArrayList<>();

        extras = new gltfj_Extras();
        extensions = new ArrayList<>();
    }
}
