package com.creedvi.utils.gltfj.gltf;

public class gltfj_Attribute {

    public enum AttributeType {
        INVALID, POSITION, NORMAL, TANGENT, TEXCOORD_0, COLOR, JOINTS, WEIGHTS
    }

    public String name;

    public AttributeType type;

    public int index, data;

}
