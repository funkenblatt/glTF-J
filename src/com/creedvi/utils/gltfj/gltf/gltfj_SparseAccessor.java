package com.creedvi.utils.gltfj.gltf;

import java.util.ArrayList;

public class gltfj_SparseAccessor {

    public int count, indicesBufferView, indicesByteOffset, valuesBufferView, valuesByteOffset, extensionsCount, indicesExtensionsCount, valuesExtensionsCount;

    public gltfj_Accessor.AccessorDataType indicesComponentType;

    public gltfj_Extras extras, indicesExtras, valuesExtras;

    public ArrayList<gltfj_Extension> extensions, indicesExtensions, valuesExtensions;

    public gltfj_SparseAccessor() {
        extras = new gltfj_Extras();
        indicesExtras = new gltfj_Extras();
        valuesExtras = new gltfj_Extras();

        extensions = new ArrayList<>();
        indicesExtensions = new ArrayList<>();
        valuesExtensions = new ArrayList<>();
    }

}
