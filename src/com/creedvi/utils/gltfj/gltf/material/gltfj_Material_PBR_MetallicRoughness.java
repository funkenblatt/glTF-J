package com.creedvi.utils.gltfj.gltf.material;

import com.creedvi.utils.gltfj.gltf.gltfj_Extras;
import com.creedvi.utils.gltfj.gltf.texture.gltfj_TextureView;

public class gltfj_Material_PBR_MetallicRoughness {

    public gltfj_TextureView baseColorTexture;
    public gltfj_TextureView metallicRoughnessTexture;

    public double[] baseColorFactor;
    public double metallicFactor, roughnessFactor;
     
    public gltfj_Extras extras;

    public gltfj_Material_PBR_MetallicRoughness() {
        baseColorTexture = new gltfj_TextureView();
        metallicRoughnessTexture = new gltfj_TextureView();
        baseColorFactor = new double[4];
    }

}
