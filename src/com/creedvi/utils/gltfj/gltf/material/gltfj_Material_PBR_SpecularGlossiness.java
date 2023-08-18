package com.creedvi.utils.gltfj.gltf.material;

import com.creedvi.utils.gltfj.gltf.texture.gltfj_TextureView;

public class gltfj_Material_PBR_SpecularGlossiness {

    public gltfj_TextureView diffuseTexture, specularGlossinessTexture;

    public double[] diffuseFactor, specularFactor;
    public double glossinessFactor;

    public gltfj_Material_PBR_SpecularGlossiness() {
        diffuseTexture = new gltfj_TextureView();
        specularGlossinessTexture = new gltfj_TextureView();

        diffuseFactor = new double[4];
        specularFactor = new double[3];
    }
}
