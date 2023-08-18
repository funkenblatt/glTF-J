package com.creedvi.utils.gltfj.gltf.mesh;

public class gltfj_MeshOptCompression {

    enum MeshOptCompressionMode {
        INVALID, ATTRIBUTES, TRIANGLES, INDICES
    }

    enum MeshOptCompressionFilter {
        NONE, OCTAHEDRAL, QUATERNION, EXPONENTIAL
    }

    int buffer, offest, size, stride, count;
    MeshOptCompressionMode mode;
    MeshOptCompressionFilter filter;

}
