package com.creedvi.utils.gltfj.gltf.texture;

import com.creedvi.utils.gltfj.gltf.gltfj_Extension;
import com.creedvi.utils.gltfj.gltf.gltfj_Extras;

import java.util.ArrayList;

public class gltfj_Sampler {

    public String name;
    public int magFilter, minFilter, sWrap, tWrap, extensionsCount;
    public gltfj_Extras extras;
    public ArrayList<gltfj_Extension> extensions;

    public gltfj_Sampler() {
        extras = new gltfj_Extras();
        extensions = new ArrayList<>();
    }


}
