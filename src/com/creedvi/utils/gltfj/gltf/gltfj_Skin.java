package com.creedvi.utils.gltfj.gltf;

import java.util.ArrayList;

public class gltfj_Skin {

    public String name;

    public int[] joints;
    public int inverseBindMatrices, skeleton;
    public ArrayList<gltfj_Extension> extensions;

    public int jointsCount, extensionsCount;

    public gltfj_Extras extras;

    public gltfj_Skin() {
        extensions = new ArrayList<>();

        extras = new gltfj_Extras();
    }
}
