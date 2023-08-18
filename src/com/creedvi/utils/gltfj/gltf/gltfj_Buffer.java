package com.creedvi.utils.gltfj.gltf;

import java.util.ArrayList;

public class gltfj_Buffer {

    public String name, uri;
    public int size, extensionsCount;
    public byte[] data;
    public gltfj_Extras extras;
    public ArrayList<gltfj_Extension> extensions;

    public gltfj_Buffer() {
        extras = new gltfj_Extras();
        extensions = new ArrayList<>();
    }
}
