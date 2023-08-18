package com.creedvi.utils.gltfj.gltf;

import java.util.ArrayList;

public class gltfj_Scene {

    public String name;

    public ArrayList<gltfj_Extension> extensions;

    public int[] nodes;
    public int nodeCount, extensionCount;

    public gltfj_Extras extras;

    public gltfj_Scene() {
        extensions = new ArrayList<>();
        extras = new gltfj_Extras();
    }

}
