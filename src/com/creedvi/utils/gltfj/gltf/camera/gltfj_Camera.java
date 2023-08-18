package com.creedvi.utils.gltfj.gltf.camera;

import com.creedvi.utils.gltfj.gltf.gltfj_Extension;
import com.creedvi.utils.gltfj.gltf.gltfj_Extras;

import java.util.ArrayList;

public class gltfj_Camera {

    public enum CameraType {
        ORTHOGRAPHIC, PERSPECTIVE
    }

    public String name;
    public CameraType type;
    public gltfj_CameraOrthographic data_o;
    public gltfj_CameraPerspective data_p;
    public boolean perspective, orthographic;

    public int extensionCount;

    public gltfj_Extras extras;
    public ArrayList<gltfj_Extension> extensions;

    public gltfj_Camera() {
        extras = new gltfj_Extras();
        extensions = new ArrayList<>();
    }
}
