package com.creedvi.utils.gltfj.gltf.camera;

import com.creedvi.utils.gltfj.gltf.gltfj_Extras;

public class gltfj_CameraPerspective {

    public double aspectRatio, y_fov, z_near, z_far;

    public gltfj_Extras extras;

    public gltfj_CameraPerspective() {
        extras = new gltfj_Extras();
    }
}
