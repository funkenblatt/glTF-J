package com.creedvi.utils.gltfj.gltf;

import java.util.ArrayList;

public class gltfj_Accessor {

    public enum AccessorType {
        SCALAR, VEC2, VEC3, VEC4, MAT2, MAT3, MAT4
    }

    public enum AccessorDataType {
        SIGNED_BYTE, UNSIGNED_BYTE, SIGNED_SHORT, UNSIGNED_SHORT, UNSIGNED_INT, FLOAT, INVALID
    }

    public int bufferView, byteOffset, count;
    public ArrayList max, min;
    public AccessorDataType componentType;
    public AccessorType type;

}
