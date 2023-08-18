package com.creedvi.utils.gltfj.gltf.material;

import com.creedvi.utils.gltfj.gltf.gltfj_Extension;
import com.creedvi.utils.gltfj.gltf.gltfj_Extras;
import com.creedvi.utils.gltfj.gltf.texture.gltfj_TextureView;

import java.util.ArrayList;

public class gltfj_Material {

    public enum AlphaMode {
        OPAQUE, MASK, BLEND
    }

    public String name;
    public gltfj_Material_PBR_MetallicRoughness metallicRoughness;
    public gltfj_Material_PBR_SpecularGlossiness specularGlossiness;
    public gltfj_Material_ClearCoat clearCoat;
    public gltfj_Material_IOR ior;
    public gltfj_Material_Specular specular;
    public gltfj_Material_Sheen sheen;
    public gltfj_Material_Transmission transmission;
    public gltfj_Material_Volume volume;

    public AlphaMode alphaMode;

    public boolean hasMetallicRoughness, hasSpecularGlossiness, hasClearCoat, hasIor, hasSpecular, hasSheen,
            hasTransmission, hasVolume, doubleSided, unlit;

    public gltfj_TextureView normalTexture, occlusionTexture, emissiveTexture;

    public int extensionsCount;

    public double[] emissiveFactor;
    public double alphaCutoff;

    public gltfj_Extras extras;
    public ArrayList<gltfj_Extension> extensions;

    public gltfj_Material() {
        metallicRoughness = new gltfj_Material_PBR_MetallicRoughness();
        specularGlossiness = new gltfj_Material_PBR_SpecularGlossiness();
        clearCoat = new gltfj_Material_ClearCoat();
        ior = new gltfj_Material_IOR();
        specular = new gltfj_Material_Specular();
        sheen = new gltfj_Material_Sheen();
        transmission = new gltfj_Material_Transmission();
        volume = new gltfj_Material_Volume();

        normalTexture = new gltfj_TextureView();
        occlusionTexture = new gltfj_TextureView();
        emissiveTexture = new gltfj_TextureView();

        emissiveFactor = new double[3];
    }
}
