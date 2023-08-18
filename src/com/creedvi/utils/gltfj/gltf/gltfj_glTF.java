package com.creedvi.utils.gltfj.gltf;

import com.creedvi.utils.gltfj.gltf.animation.gltfj_Animation;
import com.creedvi.utils.gltfj.gltf.camera.gltfj_Camera;
import com.creedvi.utils.gltfj.gltf.material.gltfj_Material;
import com.creedvi.utils.gltfj.gltf.material.gltfj_MaterialVariant;
import com.creedvi.utils.gltfj.gltf.mesh.gltfj_Mesh;
import com.creedvi.utils.gltfj.gltf.texture.gltfj_Sampler;
import com.creedvi.utils.gltfj.gltf.texture.gltfj_Texture;

import java.util.ArrayList;

public class gltfj_glTF {

    public int scene, meshCount, materialCount, accessorCount, bufferViewCount, bufferCount, imageCount, textureCount,
            samplerCount, skinCount, cameraCount, lightCount, nodeCount, sceneCount, animationCount, materialVariantCount, extensionCount;
    public gltfj_Asset asset;

    public ArrayList<gltfj_Mesh> meshes;
    public ArrayList<gltfj_Material> materials;
    public ArrayList<gltfj_Accessor> accessors;
    public ArrayList<gltfj_BufferView> bufferViews;
    public ArrayList<gltfj_Buffer> buffers;
    public ArrayList<gltfj_Image> images;
    public ArrayList<gltfj_Texture> textures;
    public ArrayList<gltfj_Sampler> samplers;
    public ArrayList<gltfj_Skin> skins;
    public ArrayList<gltfj_Camera> cameras;
    public ArrayList<gltfj_Light> lights;
    public ArrayList<gltfj_Node> nodes;
    public ArrayList<gltfj_Scene> scenes;
    public ArrayList<gltfj_Animation> animations;
    public ArrayList<gltfj_MaterialVariant> materialVariants;
    public ArrayList<gltfj_Extension> extensions;
    public ArrayList<String> extensionsUsed, extensionsRequired;

    public gltfj_Extras extras;

    public gltfj_glTF() {
        asset = new gltfj_Asset();

        meshes = new ArrayList<>();
        materials = new ArrayList<>();
        accessors = new ArrayList<>();
        bufferViews = new ArrayList<>();
        buffers = new ArrayList<>();
        images = new ArrayList<>();
        textures = new ArrayList<>();
        samplers = new ArrayList<>();
        skins = new ArrayList<>();
        cameras = new ArrayList<>();
        lights = new ArrayList<>();
        nodes = new ArrayList<>();
        scenes = new ArrayList<>();
        animations = new ArrayList<>();
        materialVariants = new ArrayList<>();
        extensions = new ArrayList<>();
        extensionsUsed = new ArrayList<>();
        extensionsRequired = new ArrayList<>();

        extras = new gltfj_Extras();
    }
}
