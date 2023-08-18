package com.creedvi.utils.gltfj.gltf.mesh;

import com.creedvi.utils.gltfj.gltf.gltfj_Accessor;
import com.creedvi.utils.gltfj.gltf.gltfj_Attribute;
import com.creedvi.utils.gltfj.gltf.gltfj_Extension;
import com.creedvi.utils.gltfj.gltf.gltfj_Extras;
import com.creedvi.utils.gltfj.gltf.material.gltfj_Material;
import com.creedvi.utils.gltfj.gltf.material.gltfj_MaterialMapping;

import java.util.ArrayList;

public class gltfj_Primitive {

    public enum PrimitiveType {
        POINTS, LINES, LINE_LOOP, lINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN
    }

    public PrimitiveType type;
    public int indices, material, attributesCount, targetsCount, mappingsCount, extensionsCount;
    public ArrayList<gltfj_Attribute> attributes;
    public ArrayList<gltfj_MorphTarget> targets;
    public ArrayList<gltfj_MaterialMapping> materialMappings;
    public ArrayList<gltfj_Extension> extensions;

    public gltfj_DracoMeshCompression meshCompression;

    public gltfj_Extras extras;

    public gltfj_Primitive() {
        attributes = new ArrayList<>();
        targets = new ArrayList<>();
        materialMappings = new ArrayList<>();
        extensions = new ArrayList<>();

        extras = new gltfj_Extras();
    }

}
