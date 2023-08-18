package com.creedvi.utils.gltfj.gltf.texture;

public class gltfj_TextureTransform {

    public double[] offset, scale;
    public double rotation;
    public int textcoord;

    public gltfj_TextureTransform() {
        offset = new double[2];
        scale = new double[2];
        textcoord = -1;
    }

}
