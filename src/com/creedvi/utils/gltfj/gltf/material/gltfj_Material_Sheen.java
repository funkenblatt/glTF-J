package com.creedvi.utils.gltfj.gltf.material;

import com.creedvi.utils.gltfj.gltf.texture.gltfj_TextureView;

public class gltfj_Material_Sheen {

    public gltfj_TextureView sheenColorTexture, sheenRoughnessTexture;

    public double[] sheenColorFactor;
    public double sheenRoughnessFactor;

    public gltfj_Material_Sheen() {
        sheenColorTexture = new gltfj_TextureView();
        sheenRoughnessTexture= new gltfj_TextureView();

        sheenColorFactor = new double[3];
    }
}
