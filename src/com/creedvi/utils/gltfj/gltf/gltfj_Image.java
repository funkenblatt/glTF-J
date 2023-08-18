package com.creedvi.utils.gltfj.gltf;

import java.util.ArrayList;

public class gltfj_Image {

    public String name, uri, mimeType;
    public int bufferView, extensionsCount;
    public gltfj_Extras extras;
    public ArrayList<gltfj_Extension> extensions;

    public gltfj_Image() {
        extras = new gltfj_Extras();
        extensions = new ArrayList<>();
    }

}
