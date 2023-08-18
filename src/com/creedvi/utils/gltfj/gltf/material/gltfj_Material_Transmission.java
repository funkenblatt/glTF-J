package com.creedvi.utils.gltfj.gltf.material;

import com.creedvi.utils.gltfj.gltf.texture.gltfj_TextureView;

public class gltfj_Material_Transmission {

    public gltfj_TextureView transmissionTexture;
    public double transmissionFactor;

    public gltfj_Material_Transmission() {
        transmissionTexture = new gltfj_TextureView();
    }
}
