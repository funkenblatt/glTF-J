package com.creedvi.utils.gltfj.gltf.animation;

import com.creedvi.utils.gltfj.gltf.gltfj_Extension;
import com.creedvi.utils.gltfj.gltf.gltfj_Extras;

import java.util.ArrayList;

public class gltfj_AnimationChannel {

    public enum AnimationPathType {
        INVALID, TRANSLATION, ROTATION, SCALE, WEIGHTS
    }

    public int sampler, targetNode, extensionsCount;

    public AnimationPathType targetPath;

    public ArrayList<gltfj_Extension> extensions;

    public gltfj_Extras extras;

    public gltfj_AnimationChannel() {
        extensions = new ArrayList<>();
        extras = new gltfj_Extras();
    }
}
