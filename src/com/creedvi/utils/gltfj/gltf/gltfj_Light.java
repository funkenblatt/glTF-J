package com.creedvi.utils.gltfj.gltf;

public class gltfj_Light {

    public enum LightType {
        INVALID, DIRECTIONAL, POINT, SPOT
    }

    public String name;

    public double[] color;
    public double intensity, range, spot_InnerConeAngle, spot_OuterConeAngle;

    public LightType type;

    public gltfj_Extras extras;

    public gltfj_Light() {
        color = new double[3];
        extras = new gltfj_Extras();
    }
}
