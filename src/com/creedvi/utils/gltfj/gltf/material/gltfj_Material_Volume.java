package com.creedvi.utils.gltfj.gltf.material;

import com.creedvi.utils.gltfj.gltf.texture.gltfj_TextureView;

public class gltfj_Material_Volume {

    public gltfj_TextureView thicknessTexture;

    public double[] attenuationColor;
    public double thicknessFactor, attenuationDistance;

    public gltfj_Material_Volume() {
        thicknessTexture = new gltfj_TextureView();

        attenuationColor = new double[3];
    }
}
