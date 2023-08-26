package com.creedvi.utils.gltfj.gltf.texture;

import com.creedvi.utils.gltfj.gltf.gltfj_Extension;
import com.creedvi.utils.gltfj.gltf.gltfj_Extras;

import java.util.ArrayList;

public class gltfj_TextureView {

    public int texture, texcoord;
    public double scale;
    public gltfj_TextureTransform transform;
    public gltfj_Extras extras;
    public int extensionsCount;
    public ArrayList<gltfj_Extension> extensions;

    public gltfj_TextureView() {
        texture = -1;
        texcoord = -1;

        transform = new gltfj_TextureTransform();
        extras = new gltfj_Extras();
        extensions = new ArrayList<>();
    }

}
