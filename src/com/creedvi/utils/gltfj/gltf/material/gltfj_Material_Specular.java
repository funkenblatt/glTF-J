package com.creedvi.utils.gltfj.gltf.material;

import com.creedvi.utils.gltfj.gltf.texture.gltfj_TextureView;

public class gltfj_Material_Specular {

    public gltfj_TextureView specularTexture, specularColorTexture;

    public double[] specularColorFactor;
    public double specularFactor;

    public gltfj_Material_Specular() {
        specularTexture = new gltfj_TextureView();

        specularColorFactor = new double[3];
    }
}
