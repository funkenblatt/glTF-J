package com.creedvi.utils.gltfj.gltf.texture;

import com.creedvi.utils.gltfj.gltf.gltfj_Extension;
import com.creedvi.utils.gltfj.gltf.gltfj_Extras;
import com.creedvi.utils.gltfj.gltf.gltfj_Image;

import java.util.ArrayList;

public class gltfj_Texture {

    public String name;
    public int image, basisuImage, sampler, extensionsCount;

    public gltfj_Extras extras;
    public ArrayList<gltfj_Extension> extensions;

    public gltfj_Texture() {
        extras = new gltfj_Extras();
        extensions = new ArrayList<>();
    }

}
