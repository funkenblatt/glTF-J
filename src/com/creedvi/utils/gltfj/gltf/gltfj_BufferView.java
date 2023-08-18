package com.creedvi.utils.gltfj.gltf;

import com.creedvi.utils.gltfj.gltf.mesh.gltfj_MeshOptCompression;

import java.util.ArrayList;

public class gltfj_BufferView {

    public enum BufferViewTarget {
        INVALID, ARRAY_BUFFER, ELEMENT_ARRAY_BUFFER
    }

    public String name;
    public int buffer, offset, size, stride, extensionsCount;
    public BufferViewTarget target;
    public byte[] data;
    public gltfj_MeshOptCompression compression;
    public gltfj_Extras extras;
    public ArrayList<gltfj_Extension> extensions;

    public gltfj_BufferView() {
        compression = new gltfj_MeshOptCompression();
        extras = new gltfj_Extras();
        extensions = new ArrayList<>();
    }

}
