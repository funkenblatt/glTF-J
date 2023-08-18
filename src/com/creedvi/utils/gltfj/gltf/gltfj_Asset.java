package com.creedvi.utils.gltfj.gltf;

import java.util.ArrayList;

public class gltfj_Asset {
    public String version, generator, copyright, minVersion;
    int extensionsCount;

    ArrayList<gltfj_Extension> extensions;
    gltfj_Extras extras;

    public gltfj_Asset() {
        extensions =  new ArrayList<>();
        extras = new gltfj_Extras();
    }

}
