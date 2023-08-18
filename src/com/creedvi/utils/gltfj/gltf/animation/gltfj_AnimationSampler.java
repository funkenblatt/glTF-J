package com.creedvi.utils.gltfj.gltf.animation;

import com.creedvi.utils.gltfj.gltf.gltfj_Accessor;
import com.creedvi.utils.gltfj.gltf.gltfj_Extension;
import com.creedvi.utils.gltfj.gltf.gltfj_Extras;

import java.util.ArrayList;

public class gltfj_AnimationSampler {

    public enum InterpolationType {
        LINEAR, STEP, CUBIC_SPLINE
    }

    public int input, output, extensionsCount;
    public ArrayList<gltfj_Extension> extensions;

    public InterpolationType interpolation;

    public gltfj_Extras extras;

    public gltfj_AnimationSampler() {
        extensions = new ArrayList<>();

        extras = new gltfj_Extras();
    }
}
