package com.creedvi.utils.gltfj.gltf.animation;

import com.creedvi.utils.gltfj.gltf.gltfj_Extension;
import com.creedvi.utils.gltfj.gltf.gltfj_Extras;

import java.util.ArrayList;

public class gltfj_Animation {

    public String name;

    public ArrayList<gltfj_AnimationSampler> samplers;
    public ArrayList<gltfj_AnimationChannel> channels;
    public ArrayList<gltfj_Extension> extensions;

    public int samplerCount, channelCount, extensionCount;

    public gltfj_Extras extras;

    public gltfj_Animation() {
        samplers = new ArrayList<>();
        channels = new ArrayList<>();
        extensions = new ArrayList<>();

        extras = new gltfj_Extras();
    }
}
