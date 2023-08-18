package com.creedvi.utils.gltfj.gltf.material;

import com.creedvi.utils.gltfj.gltf.texture.gltfj_TextureView;

public class gltfj_Material_ClearCoat {

    public gltfj_TextureView clearCoatTexture, clearCoatRoughnessTexture, clearCoatNormalTexture;

    public double[] diffuseFactor, specularFactor;
    public double glossinessFactor;

    public gltfj_Material_ClearCoat() {
        clearCoatTexture = new gltfj_TextureView();
        clearCoatRoughnessTexture = new gltfj_TextureView();
        clearCoatNormalTexture = new gltfj_TextureView();

        diffuseFactor = new double[4];
        specularFactor = new double[3];
    }
}
