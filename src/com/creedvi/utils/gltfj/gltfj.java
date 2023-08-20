package com.creedvi.utils.gltfj;


import com.creedvi.utils.gltfj.gltf.animation.gltfj_Animation;
import com.creedvi.utils.gltfj.gltf.animation.gltfj_AnimationChannel;
import com.creedvi.utils.gltfj.gltf.animation.gltfj_AnimationSampler;
import com.creedvi.utils.gltfj.gltf.camera.gltfj_Camera;
import com.creedvi.utils.gltfj.gltf.camera.gltfj_CameraOrthographic;
import com.creedvi.utils.gltfj.gltf.camera.gltfj_CameraPerspective;
import com.creedvi.utils.gltfj.gltf.*;
import com.creedvi.utils.gltfj.gltf.material.gltfj_Material;
import com.creedvi.utils.gltfj.gltf.mesh.gltfj_Mesh;
import com.creedvi.utils.gltfj.gltf.mesh.gltfj_MorphTarget;
import com.creedvi.utils.gltfj.gltf.mesh.gltfj_Primitive;
import com.creedvi.utils.gltfj.gltf.texture.gltfj_Sampler;
import com.creedvi.utils.gltfj.gltf.texture.gltfj_Texture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

public class gltfj {

    private static String filepath;

    public static gltfj_glTF Read(String fileName) {
        gltfj_glTF result = new gltfj_glTF();

        filepath = fileName.substring(0, fileName.lastIndexOf("/") + 1);

        if (fileName.substring(fileName.lastIndexOf(".")).equalsIgnoreCase(".gltf")) {
            String jsonText = LoadFileText(fileName);
            result = ReadJSON(jsonText);
            result.fileType = gltfj_glTF.FileType.GLTF;
        }
        else if (fileName.substring(fileName.lastIndexOf(".")).equalsIgnoreCase(".glb")) {
            byte[] fileData = LoadFileData(fileName);
            result = ReadBinary(fileData);
            result.fileType = gltfj_glTF.FileType.GLB;
        }
        else {
            System.out.println("[glTF-J] ERROR: Unknown file type " + fileName.substring(fileName.lastIndexOf(".")));
            result.fileType = gltfj_glTF.FileType.INVALID;
        }

        return result;
    }

    private static gltfj_glTF ReadJSON(String jsonText) {
        gltfj_glTF result = new gltfj_glTF();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(jsonText);

            JsonNode asset = root.path("asset");
            if (!asset.isMissingNode()) {
                result.asset.version = asset.path("version").asText();
                result.asset.minVersion = asset.path("minVersion").asText();
                result.asset.generator = asset.path("generator").asText();
                result.asset.copyright = asset.path("copyright").asText();
            }

            JsonNode meshes = root.path("meshes");
            if (!meshes.isMissingNode()) {
                int m = 0;
                for (JsonNode node : meshes) {
                    result.meshes.add(m, new gltfj_Mesh());

                    result.meshes.get(m).name = node.path("name").asText();
                    JsonNode weight = node.path("weight");
                    if (!weight.isMissingNode()) {
                        result.meshes.get(m).weights = new double[weight.size()];
                        for (int w = 0; w < weight.size(); w++) {
                            result.meshes.get(m).weights[w] = weight.get(w).asDouble();
                        }
                        result.meshes.get(m).weightsCount = weight.size();
                    }

                    JsonNode primitives = node.path("primitives");
                    if (!primitives.isMissingNode()) {
                        int p = 0;
                        for (JsonNode pri : primitives) {
                            result.meshes.get(m).primitives.add(p, new gltfj_Primitive());

                            result.meshes.get(m).primitives.get(p).indices = pri.path("indices").asInt();
                            result.meshes.get(m).primitives.get(p).material = pri.path("material").asInt();
                            int mode = pri.path("mode").asInt(0);
                            result.meshes.get(m).primitives.get(p).type = gltfj_Primitive.PrimitiveType.values()[mode];

                            JsonNode attrib = pri.path("attributes");
                            int a = 0;
                            if (!attrib.path("POSITION").isMissingNode()) {
                                result.meshes.get(m).primitives.get(p).attributes.add(new gltfj_Attribute());
                                result.meshes.get(m).primitives.get(p).attributes.get(a).type = gltfj_Attribute.AttributeType.POSITION;
                                result.meshes.get(m).primitives.get(p).attributes.get(a).index = attrib.path("POSITION").asInt();
                                a++;
                            }
                            if (!attrib.path("NORMAL").isMissingNode()) {
                                result.meshes.get(m).primitives.get(p).attributes.add(new gltfj_Attribute());
                                result.meshes.get(m).primitives.get(p).attributes.get(a).type = gltfj_Attribute.AttributeType.NORMAL;
                                result.meshes.get(m).primitives.get(p).attributes.get(a).index = attrib.path("NORMAL").asInt();
                                a++;
                            }
                            if (!attrib.path("TANGENT").isMissingNode()) {
                                result.meshes.get(m).primitives.get(p).attributes.add(new gltfj_Attribute());
                                result.meshes.get(m).primitives.get(p).attributes.get(a).type = gltfj_Attribute.AttributeType.TANGENT;
                                result.meshes.get(m).primitives.get(p).attributes.get(a).index = attrib.path("TANGENT").asInt();
                                a++;
                            }
                            if (!attrib.path("TEXCOORD_0").isMissingNode()) {
                                result.meshes.get(m).primitives.get(p).attributes.add(new gltfj_Attribute());
                                result.meshes.get(m).primitives.get(p).attributes.get(a).type = gltfj_Attribute.AttributeType.TEXCOORD_0;
                                result.meshes.get(m).primitives.get(p).attributes.get(a).index = attrib.path("TEXCOORD_0").asInt();
                                a++;
                            }
                            if (!attrib.path("COLOR").isMissingNode()) {
                                result.meshes.get(m).primitives.get(p).attributes.add(new gltfj_Attribute());
                                result.meshes.get(m).primitives.get(p).attributes.get(a).type = gltfj_Attribute.AttributeType.COLOR;
                                result.meshes.get(m).primitives.get(p).attributes.get(a).index = attrib.path("COLOR").asInt();
                                a++;
                            }
                            if (!attrib.path("JOINTS").isMissingNode()) {
                                result.meshes.get(m).primitives.get(p).attributes.add(new gltfj_Attribute());
                                result.meshes.get(m).primitives.get(p).attributes.get(a).type = gltfj_Attribute.AttributeType.JOINTS;
                                result.meshes.get(m).primitives.get(p).attributes.get(a).index = attrib.path("JOINTS").asInt();
                                a++;
                            }
                            if (!attrib.path("WEIGHTS").isMissingNode()) {
                                result.meshes.get(m).primitives.get(p).attributes.add(new gltfj_Attribute());
                                result.meshes.get(m).primitives.get(p).attributes.get(a).type = gltfj_Attribute.AttributeType.WEIGHTS;
                                result.meshes.get(m).primitives.get(p).attributes.get(a).index = attrib.path("WEIGHTS").asInt();
                                a++;
                            }
                            result.meshes.get(m).primitives.get(p).attributesCount = a;

                            JsonNode targets = primitives.path("targets");
                            if (!targets.isMissingNode()) {
                                int t = 0;
                                for (; t < targets.size(); t++) {
                                    result.meshes.get(m).primitives.get(p).targets.add(t, new gltfj_MorphTarget());

                                    JsonNode target = targets.get(t);
                                    int ta = 0;
                                    if (!target.path("POSITION").isMissingNode()) {
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.add(new gltfj_Attribute());
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).type = gltfj_Attribute.AttributeType.POSITION;
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).index = target.path("POSITION").asInt();
                                        ta++;
                                    }
                                    if (!target.path("NORMAL").isMissingNode()) {
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.add(new gltfj_Attribute());
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).type = gltfj_Attribute.AttributeType.NORMAL;
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).index = target.path("NORMAL").asInt();
                                        ta++;
                                    }
                                    if (!target.path("TANGENT").isMissingNode()) {
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.add(new gltfj_Attribute());
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).type = gltfj_Attribute.AttributeType.TANGENT;
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).index = target.path("TANGENT").asInt();
                                        ta++;
                                    }
                                    if (!target.path("TEXCOORD_0").isMissingNode()) {
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.add(new gltfj_Attribute());
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).type = gltfj_Attribute.AttributeType.TEXCOORD_0;
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).index = target.path("TEXCOORD_0").asInt();
                                        ta++;
                                    }
                                    if (!target.path("COLOR").isMissingNode()) {
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.add(new gltfj_Attribute());
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).type = gltfj_Attribute.AttributeType.COLOR;
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).index = target.path("COLOR").asInt();
                                        ta++;
                                    }
                                    if (!target.path("JOINTS").isMissingNode()) {
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.add(new gltfj_Attribute());
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).type = gltfj_Attribute.AttributeType.JOINTS;
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).index = target.path("JOINTS").asInt();
                                        ta++;
                                    }
                                    if (!target.path("WEIGHTS").isMissingNode()) {
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.add(new gltfj_Attribute());
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).type = gltfj_Attribute.AttributeType.WEIGHTS;
                                        result.meshes.get(m).primitives.get(p).targets.get(ta).attributes.get(ta).index = target.path("WEIGHTS").asInt();
                                        ta++;
                                    }
                                    result.meshes.get(m).primitives.get(p).targets.get(t).attributeCount = ta;
                                }
                                result.meshes.get(m).primitives.get(p).targetsCount = t;
                            }

                            p++;
                        }


                    }

                    m++;
                }
                result.meshCount = m;
            }

            JsonNode materials = root.path("materials");
            if (!materials.isMissingNode()) {
                int m = 0;
                for (JsonNode material : materials) {
                    gltfj_Material mat = new gltfj_Material();

                    mat.name = material.path("name").asText();
                    mat.alphaMode = gltfj_Material.AlphaMode.valueOf(material.path("alphaMode").asText("OPAQUE").toUpperCase());
                    mat.alphaCutoff = material.path("alphaCutoff").asDouble();
                    mat.doubleSided = material.path("doubleSided").asBoolean();
                    mat.unlit = material.path("unlit").asBoolean();

                    JsonNode emf = material.path("emmissiveFactor");
                    if (!emf.isMissingNode()) {
                        for (int emff = 0; emff < emf.size(); emff++) {
                            mat.emissiveFactor[emff] = emf.get(emff).asDouble();
                        }
                    }

                    JsonNode normalTexture = material.path("normalTexture");
                    if (!normalTexture.isMissingNode()) {
                        mat.normalTexture.texture = normalTexture.path("texture").asInt();
                        mat.normalTexture.texcoord = normalTexture.path("texCoord").asInt();
                        mat.normalTexture.scale = normalTexture.path("scale").asDouble();

                        JsonNode trans = normalTexture.path("transform");
                        if (!trans.isMissingNode()) {
                            mat.normalTexture.transform.textcoord = trans.path("texCoord").asInt();
                            mat.normalTexture.transform.rotation = trans.path("rotation").asDouble();
                        }
                        JsonNode offset = material.path("offset");
                        if (!offset.isMissingNode()) {
                            for (int off = 0; off < offset.size(); off++) {
                                mat.normalTexture.transform.offset[off] = offset.get(off).asDouble();
                            }
                        }
                        JsonNode scale = material.path("scale");
                        if (!scale.isMissingNode()) {
                            for (int sca = 0; sca < scale.size(); sca++) {
                                mat.normalTexture.transform.scale[sca] = scale.get(sca).asDouble();
                            }
                        }
                    }

                    JsonNode occlusionTexture = material.path("occlusionTexture");
                    if (!occlusionTexture.isMissingNode()) {
                        mat.occlusionTexture.texture = occlusionTexture.path("texture").asInt();
                        mat.occlusionTexture.texcoord = occlusionTexture.path("texCoord").asInt();
                        mat.occlusionTexture.scale = occlusionTexture.path("scale").asDouble();

                        JsonNode trans = occlusionTexture.path("transform");
                        if (!trans.isMissingNode()) {
                            mat.occlusionTexture.transform.textcoord = trans.path("texCoord").asInt();
                            mat.occlusionTexture.transform.rotation = trans.path("rotation").asDouble();
                        }
                        JsonNode offset = material.path("offset");
                        if (!offset.isMissingNode()) {
                            for (int off = 0; off < offset.size(); off++) {
                                mat.occlusionTexture.transform.offset[off] = offset.get(off).asDouble();
                            }
                        }
                        JsonNode scale = material.path("scale");
                        if (!scale.isMissingNode()) {
                            for (int sca = 0; sca < scale.size(); sca++) {
                                mat.occlusionTexture.transform.scale[sca] = scale.get(sca).asDouble();
                            }
                        }
                    }

                    JsonNode emissiveTexture = material.path("emissiveTexture");
                    if (!emissiveTexture.isMissingNode()) {
                        mat.emissiveTexture.texture = emissiveTexture.path("texture").asInt();
                        mat.emissiveTexture.texcoord = emissiveTexture.path("texCoord").asInt();
                        mat.emissiveTexture.scale = emissiveTexture.path("scale").asDouble();

                        JsonNode trans = emissiveTexture.path("transform");
                        if (!trans.isMissingNode()) {
                            mat.emissiveTexture.transform.textcoord = trans.path("texCoord").asInt();
                            mat.emissiveTexture.transform.rotation = trans.path("rotation").asDouble();
                        }
                        JsonNode offset = material.path("offset");
                        if (!offset.isMissingNode()) {
                            for (int off = 0; off < offset.size(); off++) {
                                mat.emissiveTexture.transform.offset[off] = offset.get(off).asDouble();
                            }
                        }
                        JsonNode scale = material.path("scale");
                        if (!scale.isMissingNode()) {
                            for (int sca = 0; sca < scale.size(); sca++) {
                                mat.emissiveTexture.transform.scale[sca] = scale.get(sca).asDouble();
                            }
                        }
                    }

                    JsonNode met = material.path("pbrMetallicRoughness");
                    if (!met.isMissingNode()) {
                        mat.hasMetallicRoughness = true;

                        JsonNode baseColour = met.path("baseColorFactor");
                        if (!baseColour.isMissingNode()) {
                            for (int bcf = 0; bcf < baseColour.size(); bcf++) {
                                mat.metallicRoughness.baseColorFactor[bcf] = baseColour.get(bcf).asDouble();
                            }
                        }
                        mat.metallicRoughness.metallicFactor = met.path("metallicFactor").asDouble();
                        mat.metallicRoughness.roughnessFactor = met.path("roughnessFactor").asDouble();

                        JsonNode baseTex = met.path("baseColorTexture");
                        if (!baseTex.isMissingNode()) {
                            mat.metallicRoughness.baseColorTexture.texture = baseTex.path("texture").asInt();
                            mat.metallicRoughness.baseColorTexture.texcoord = baseTex.path("texCoord").asInt();
                            mat.metallicRoughness.baseColorTexture.scale = baseTex.path("scale").asDouble();

                            JsonNode trans = baseTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.metallicRoughness.baseColorTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.metallicRoughness.baseColorTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.metallicRoughness.baseColorTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.metallicRoughness.baseColorTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }

                        JsonNode mrTex = met.path("metallicRoughnessTexture");
                        if (!mrTex.isMissingNode()) {
                            mat.metallicRoughness.baseColorTexture.texture = mrTex.path("texture").asInt();
                            mat.metallicRoughness.baseColorTexture.texcoord = mrTex.path("texCoord").asInt();
                            mat.metallicRoughness.baseColorTexture.scale = mrTex.path("scale").asDouble();

                            JsonNode trans = mrTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.metallicRoughness.baseColorTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.metallicRoughness.baseColorTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.metallicRoughness.baseColorTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.metallicRoughness.baseColorTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }
                    }

                    JsonNode specGloss = material.path("pbrSpecularGlossiness");
                    if (!specGloss.isMissingNode()) {
                        mat.hasSpecularGlossiness = true;

                        JsonNode diffuseFactor = specGloss.path("diffuseFactor");
                        if (!diffuseFactor.isMissingNode()) {
                            for (int diff = 0; diff < diffuseFactor.size(); diff++) {
                                mat.specularGlossiness.diffuseFactor[diff] = diffuseFactor.get(diff).asDouble();
                            }
                        }
                        JsonNode specularFactor = specGloss.path("specularFactor");
                        if (!specularFactor.isMissingNode()) {
                            for (int specf = 0; specf < specularFactor.size(); specf++) {
                                mat.specularGlossiness.specularFactor[specf] = specularFactor.get(specf).asDouble();
                            }
                        }
                        mat.specularGlossiness.glossinessFactor = specGloss.path("glossinessFactor").asDouble();

                        JsonNode diffTex = specGloss.path("diffuseTexture");
                        if (!diffTex.isMissingNode()) {
                            mat.specularGlossiness.diffuseTexture.texture = diffTex.path("texture").asInt();
                            mat.specularGlossiness.diffuseTexture.texcoord = diffTex.path("texCoord").asInt();
                            mat.specularGlossiness.diffuseTexture.scale = diffTex.path("scale").asDouble();

                            JsonNode trans = diffTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.specularGlossiness.diffuseTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.specularGlossiness.diffuseTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.specularGlossiness.diffuseTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.specularGlossiness.diffuseTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }

                        JsonNode sgTex = specGloss.path("specularGlossinessTexture");
                        if (!sgTex.isMissingNode()) {
                            mat.specularGlossiness.specularGlossinessTexture.texture = sgTex.path("texture").asInt();
                            mat.specularGlossiness.specularGlossinessTexture.texcoord = sgTex.path("texCoord").asInt();
                            mat.specularGlossiness.specularGlossinessTexture.scale = sgTex.path("scale").asDouble();

                            JsonNode trans = sgTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.specularGlossiness.specularGlossinessTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.specularGlossiness.specularGlossinessTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.specularGlossiness.specularGlossinessTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.specularGlossiness.specularGlossinessTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }
                    }

                    JsonNode clear = material.path("clearCoat");
                    if (!clear.isMissingNode()) {
                        mat.hasMetallicRoughness = true;

                        JsonNode diffuse = clear.path("baseColorFactor");
                        if (!diffuse.isMissingNode()) {
                            for (int diff = 0; diff < diffuse.size(); diff++) {
                                mat.clearCoat.diffuseFactor[diff] = diffuse.get(diff).asDouble();
                            }
                        }
                        JsonNode specular = clear.path("baseColorFactor");
                        if (!specular.isMissingNode()) {
                            for (int diff = 0; diff < specular.size(); diff++) {
                                mat.clearCoat.diffuseFactor[diff] = specular.get(diff).asDouble();
                            }
                        }
                        mat.clearCoat.glossinessFactor = clear.path("glossinessFactor").asDouble();

                        JsonNode ccTex = clear.path("clearCoatTexture");
                        if (!ccTex.isMissingNode()) {
                            mat.clearCoat.clearCoatTexture.texture = ccTex.path("texture").asInt();
                            mat.clearCoat.clearCoatTexture.texcoord = ccTex.path("texCoord").asInt();
                            mat.clearCoat.clearCoatTexture.scale = ccTex.path("scale").asDouble();

                            JsonNode trans = ccTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.clearCoat.clearCoatTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.clearCoat.clearCoatTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.clearCoat.clearCoatTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.clearCoat.clearCoatTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }

                        JsonNode ccrTex = clear.path("clearCoatRoughnessTexture");
                        if (!ccrTex.isMissingNode()) {
                            mat.clearCoat.clearCoatRoughnessTexture.texture = ccrTex.path("texture").asInt();
                            mat.clearCoat.clearCoatRoughnessTexture.texcoord = ccrTex.path("texCoord").asInt();
                            mat.clearCoat.clearCoatRoughnessTexture.scale = ccrTex.path("scale").asDouble();

                            JsonNode trans = ccrTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.clearCoat.clearCoatRoughnessTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.clearCoat.clearCoatRoughnessTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.clearCoat.clearCoatRoughnessTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.clearCoat.clearCoatRoughnessTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }

                        JsonNode ccnTex = clear.path("clearCoatNormalTexture");
                        if (!ccnTex.isMissingNode()) {
                            mat.clearCoat.clearCoatNormalTexture.texture = ccnTex.path("texture").asInt();
                            mat.clearCoat.clearCoatNormalTexture.texcoord = ccnTex.path("texCoord").asInt();
                            mat.clearCoat.clearCoatNormalTexture.scale = ccnTex.path("scale").asDouble();

                            JsonNode trans = ccnTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.clearCoat.clearCoatNormalTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.clearCoat.clearCoatNormalTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.clearCoat.clearCoatNormalTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.clearCoat.clearCoatNormalTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }
                    }

                    JsonNode ior = material.path("ior");
                    if (!ior.isMissingNode()) {
                        mat.hasIor = true;
                        mat.ior.ior = ior.path("ior").asDouble();
                    }

                    JsonNode specular = material.path("specular");
                    if (!specular.isMissingNode()) {
                        mat.hasSpecular = true;

                        JsonNode specularColour = specular.path("specularColorFactor");
                        if (!specularColour.isMissingNode()) {
                            for (int scf = 0; scf < specularColour.size(); scf++) {
                                mat.specular.specularColorFactor[scf] = specularColour.get(scf).asDouble();
                            }
                        }
                        mat.specular.specularFactor = specular.path("specularFactor").asDouble();

                        JsonNode specTex = specular.path("specularTexture");
                        if (!specTex.isMissingNode()) {
                            mat.specular.specularTexture.texture = specTex.path("texture").asInt();
                            mat.specular.specularTexture.texcoord = specTex.path("texCoord").asInt();
                            mat.specular.specularTexture.scale = specTex.path("scale").asDouble();

                            JsonNode trans = specTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.specular.specularTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.specular.specularTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.specular.specularTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.specular.specularTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }

                        JsonNode scTex = specular.path("specularColorTexture");
                        if (!scTex.isMissingNode()) {
                            mat.specular.specularColorTexture.texture = scTex.path("texture").asInt();
                            mat.specular.specularColorTexture.texcoord = scTex.path("texCoord").asInt();
                            mat.specular.specularColorTexture.scale = scTex.path("scale").asDouble();

                            JsonNode trans = scTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.specular.specularColorTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.specular.specularColorTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.specular.specularColorTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.specular.specularColorTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }
                    }

                    JsonNode sheen = material.path("sheen");
                    if (!sheen.isMissingNode()) {
                        mat.hasSheen = true;

                        JsonNode sheenColorFactor = sheen.path("sheenColorFactor");
                        if (!sheenColorFactor.isMissingNode()) {
                            for (int scf = 0; scf < sheenColorFactor.size(); scf++) {
                                mat.sheen.sheenColorFactor[scf] = sheenColorFactor.get(scf).asDouble();
                            }
                        }
                        mat.sheen.sheenRoughnessFactor = sheen.path("sheenRoughnessFactor").asDouble();

                        JsonNode scTex = sheen.path("sheenColorTexture");
                        if (!scTex.isMissingNode()) {
                            mat.sheen.sheenColorTexture.texture = scTex.path("texture").asInt();
                            mat.sheen.sheenColorTexture.texcoord = scTex.path("texCoord").asInt();
                            mat.sheen.sheenColorTexture.scale = scTex.path("scale").asDouble();

                            JsonNode trans = scTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.sheen.sheenColorTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.sheen.sheenColorTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.sheen.sheenColorTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.sheen.sheenColorTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }

                        JsonNode srTex = sheen.path("sheenRoughnessTexture");
                        if (!srTex.isMissingNode()) {
                            mat.sheen.sheenRoughnessTexture.texture = srTex.path("texture").asInt();
                            mat.sheen.sheenRoughnessTexture.texcoord = srTex.path("texCoord").asInt();
                            mat.sheen.sheenRoughnessTexture.scale = srTex.path("scale").asDouble();

                            JsonNode trans = srTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.sheen.sheenRoughnessTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.sheen.sheenRoughnessTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.sheen.sheenRoughnessTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.sheen.sheenRoughnessTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }
                    }

                    JsonNode transmission = material.path("transmission");
                    if (!transmission.isMissingNode()) {
                        mat.hasTransmission = true;

                        mat.transmission.transmissionFactor = transmission.path("transmissionFactor").asDouble();

                        JsonNode tTex = transmission.path("transmissionTexture");
                        if (!tTex.isMissingNode()) {
                            mat.transmission.transmissionTexture.texture = tTex.path("texture").asInt();
                            mat.transmission.transmissionTexture.texcoord = tTex.path("texCoord").asInt();
                            mat.transmission.transmissionTexture.scale = tTex.path("scale").asDouble();

                            JsonNode trans = tTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.transmission.transmissionTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.transmission.transmissionTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.transmission.transmissionTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.transmission.transmissionTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }

                    }

                    JsonNode volume = material.path("volume");
                    if (!volume.isMissingNode()) {
                        mat.hasVolume = true;

                        JsonNode attenuationColor = volume.path("attenuationColor");
                        if (!attenuationColor.isMissingNode()) {
                            for (int atc = 0; atc < attenuationColor.size(); atc++) {
                                mat.volume.attenuationColor[atc] = attenuationColor.get(atc).asDouble();
                            }
                        }
                        mat.volume.thicknessFactor = volume.path("thicknessFactor").asDouble();
                        mat.volume.attenuationDistance = volume.path("attenuationDistance").asDouble();

                        JsonNode tTex = volume.path("thicknessTexture");
                        if (!tTex.isMissingNode()) {
                            mat.volume.thicknessTexture.texture = tTex.path("texture").asInt();
                            mat.volume.thicknessTexture.texcoord = tTex.path("texCoord").asInt();
                            mat.volume.thicknessTexture.scale = tTex.path("scale").asDouble();

                            JsonNode trans = tTex.path("transform");
                            if (!trans.isMissingNode()) {
                                mat.volume.thicknessTexture.transform.textcoord = trans.path("texCoord").asInt();
                                mat.volume.thicknessTexture.transform.rotation = trans.path("rotation").asDouble();
                            }
                            JsonNode offset = material.path("offset");
                            if (!offset.isMissingNode()) {
                                for (int off = 0; off < offset.size(); off++) {
                                    mat.volume.thicknessTexture.transform.offset[off] = offset.get(off).asDouble();
                                }
                            }
                            JsonNode scale = material.path("scale");
                            if (!scale.isMissingNode()) {
                                for (int sca = 0; sca < scale.size(); sca++) {
                                    mat.volume.thicknessTexture.transform.scale[sca] = scale.get(sca).asDouble();
                                }
                            }
                        }

                    }

                    result.materials.add(m, mat);
                    m++;
                }
                result.materialCount = m;
            }

            JsonNode accessors = root.path("accessors");
            if (!accessors.isMissingNode()) {
                int a = 0;
                for (JsonNode accessor : accessors) {
                    result.accessors.add(a, new gltfj_Accessor());

                    result.accessors.get(a).bufferView = accessor.path("bufferView").asInt();
                    result.accessors.get(a).byteOffset = accessor.path("byteOffset").asInt();
                    result.accessors.get(a).count = accessor.path("count").asInt();

                    String type = accessor.path("type").asText().toUpperCase();
                    result.accessors.get(a).type = gltfj_Accessor.AccessorType.valueOf(type);

                    int compType =  accessor.path("componentType").asInt();
                    result.accessors.get(a).componentType = gltfj_Accessor.AccessorDataType.values()[compType - 5120];

                    JsonNode maNode = accessor.path("max");
                    JsonNode miNode = accessor.path("min");

                    for (int i = 0; i < maNode.size() || i < miNode.size(); i++) {
                        switch (result.accessors.get(a).componentType) {
                            case SIGNED_BYTE:
                            case UNSIGNED_BYTE:
                            case UNSIGNED_INT:
                            case SIGNED_SHORT:
                            case UNSIGNED_SHORT:
                                result.accessors.get(a).max.add(maNode.get(i).asInt());
                                result.accessors.get(a).min.add(miNode.get(i).asInt());
                                break;
                            case FLOAT:
                                result.accessors.get(a).max.add(maNode.get(i).asDouble());
                                result.accessors.get(a).min.add(miNode.get(i).asDouble());
                                break;
                        }
                    }

                    // TODO: 8/20/23 Sparse Accessors

                    a++;
                }
                result.accessorCount = a;
            }

            JsonNode bufferViews = root.path("bufferViews");
            if (!bufferViews.isMissingNode()) {
                int bv = 0;
                for (JsonNode node : bufferViews) {
                    result.bufferViews.add(bv, new gltfj_BufferView());

                    result.bufferViews.get(bv).name = node.path("name").asText();
                    result.bufferViews.get(bv).buffer = node.path("buffer").asInt();
                    result.bufferViews.get(bv).offset = node.path("offset").asInt();
                    result.bufferViews.get(bv).size = node.path("byteLength").asInt();
                    result.bufferViews.get(bv).stride = node.path("byteStride").asInt();

                    int tgt = node.path("target").asInt(-1);
                    if (tgt == 34962) {
                        result.bufferViews.get(bv).target = gltfj_BufferView.BufferViewTarget.ARRAY_BUFFER;
                    }
                    else if (tgt == 34963) {
                        result.bufferViews.get(bv).target = gltfj_BufferView.BufferViewTarget.ELEMENT_ARRAY_BUFFER;
                    }
                    else {
                        result.bufferViews.get(bv).target = gltfj_BufferView.BufferViewTarget.INVALID;
                    }
                }
                result.bufferViewCount = bv;
            }

            JsonNode buffers = root.path("buffers");
            if (!buffers.isMissingNode()) {
                int b = 0;
                for (JsonNode node : buffers) {
                    result.buffers.add(b, new gltfj_Buffer());

                    result.buffers.get(b).name = node.path("name").asText();
                    result.buffers.get(b).uri = node.path("uri").asText();
                    result.buffers.get(b).size = node.path("byteLength").asInt();

                    if (result.buffers.get(b).uri != null) {
                        if (result.buffers.get(b).uri.contains("octet-stream;base64")) {
                            String uri = result.buffers.get(b).uri;
                            String encoded = uri.substring(uri.lastIndexOf(",") + 1);
                            result.buffers.get(b).data = Base64.getDecoder().decode(encoded);
                        }
                        else {
                            result.buffers.get(b).data = LoadFileData(filepath + result.buffers.get(b).uri);
                        }
                    }
                    b++;
                }
                result.bufferCount = b;
            }

            JsonNode images = root.path("images");
            if (!images.isMissingNode()) {
                int i = 0;
                for (JsonNode node: images) {
                    result.images.add(i, new gltfj_Image());

                    result.images.get(i).name = node.path("name").asText();
                    result.images.get(i).uri = node.path("uri").asText();
                    result.images.get(i).mimeType = node.path("mimeType").asText();
                    result.images.get(i).bufferView = node.path("bufferView").asInt();

                    i++;
                }
                result.imageCount = i;
            }

            JsonNode textures = root.path("textures");
            if (!textures.isMissingNode()) {
                int t = 0;
                for (JsonNode node : textures) {
                    result.textures.add(t, new gltfj_Texture());

                    result.textures.get(t).image = node.path("source").asInt();
                    result.textures.get(t).sampler = node.path("sampler").asInt();

                    t++;
                }
                result.textureCount = t;
            }

            JsonNode samplers = root.path("samplers");
            if (!samplers.isMissingNode()) {
                int s = 0;
                for (JsonNode node : samplers) {
                    result.samplers.add(s, new gltfj_Sampler());

                    result.samplers.get(s).name = node.path("name").asText();
                    result.samplers.get(s).magFilter = node.path("magFilter").asInt();
                    result.samplers.get(s).minFilter = node.path("minFilter").asInt();
                    result.samplers.get(s).sWrap = node.path("wrapS").asInt();
                    result.samplers.get(s).tWrap = node.path("wrapT").asInt();

                    s++;
                }
                result.samplerCount = s;
            }

            JsonNode skins = root.path("skins");
            if (!skins.isMissingNode()) {
                int s = 0;
                for (JsonNode node : skins) {
                    result.skins.add(s, new gltfj_Skin());

                    result.skins.get(s).name = node.path("name").asText();
                    result.skins.get(s).inverseBindMatrices = node.path("inverseBindMatrices").asInt();
                    result.skins.get(s).skeleton = node.path("skeleton").asInt();

                    JsonNode joints = node.path("joints");
                    if (!joints.isMissingNode()) {
                        int j = 0;
                        result.skins.get(s).joints = new int[joints.size()];
                        for (; j < joints.size(); j++) {
                            result.skins.get(s).joints[j] = joints.get(j).asInt();
                        }
                        result.skins.get(s).jointsCount = j;
                    }
                }
                result.skinCount = s;
            }

            JsonNode cameras = root.path("cameras");
            if (!cameras.isMissingNode()) {
                int c = 0;
                for (JsonNode node : cameras) {
                    result.cameras.add(c, new gltfj_Camera());

                    result.cameras.get(c).name = node.path("name").asText();
                    result.cameras.get(c).type = gltfj_Camera.CameraType.valueOf(node.path("type").asText().toUpperCase());

                    if (result.cameras.get(c).type == gltfj_Camera.CameraType.ORTHOGRAPHIC) {
                        result.cameras.get(c).data_o = new gltfj_CameraOrthographic();
                        result.cameras.get(c).orthographic = true;

                        JsonNode orth = node.path("orthographic");
                        result.cameras.get(c).data_o.x_mag = orth.path("xmag").asDouble();
                        result.cameras.get(c).data_o.y_mag =  orth.path("ymag").asDouble();
                        result.cameras.get(c).data_o.z_far =  orth.path("zfar").asDouble();
                        result.cameras.get(c).data_o.z_near = orth.path("znear").asDouble();

                    }
                    else {
                        //assume perspective
                        result.cameras.get(c).data_p = new gltfj_CameraPerspective();
                        result.cameras.get(c).perspective = true;

                        JsonNode pers = node.path("perspective");
                        result.cameras.get(c).data_p.aspectRatio = pers.path("aspectRatio").asDouble();
                        result.cameras.get(c).data_p.y_fov = pers.path("yfov").asDouble();
                        result.cameras.get(c).data_p.z_far = pers.path("zfar").asDouble();
                        result.cameras.get(c).data_p.z_near = pers.path("znear").asDouble();
                    }
                }
                result.cameraCount = c;
            }

            JsonNode lights = root.path("lights");
            if (!lights.isMissingNode()) {
                int l = 0;
                for (JsonNode node : lights) {
                    result.lights.add(l, new gltfj_Light());

                    result.lights.get(l).name = node.path("name").asText();
                    result.lights.get(l).intensity = node.path("intensity").asDouble();
                    result.lights.get(l).range = node.path("range").asDouble();
                    result.lights.get(l).spot_InnerConeAngle = node.path("innerConeAngle").asDouble();
                    result.lights.get(l).spot_OuterConeAngle = node.path("outerConeAngle").asDouble();
                    result.lights.get(l).type = gltfj_Light.LightType.valueOf(node.path("type").asText("INVALID").toUpperCase());

                    JsonNode colour = node.path("color");
                    result.lights.get(l).color = new double[colour.size()];
                    for (int j = 0; j < colour.size(); j++) {
                        result.lights.get(l).color[j] = colour.get(j).asDouble();
                    }
                }
                result.lightCount = l;
            }

            JsonNode nodes = root.path("nodes");
            if (!nodes.isMissingNode()) {
                int n = 0;
                for (JsonNode node : nodes) {
                    result.nodes.add(n, new gltfj_Node());

                    result.nodes.get(n).name = node.path("name").asText();
                    result.nodes.get(n).skin = node.path("skin").asInt(0);
                    result.nodes.get(n).mesh = node.path("mesh").asInt(0);
                    result.nodes.get(n).camera = node.path("camera").asInt(0);
                    result.nodes.get(n).light = node.path("light").asInt(0);
                    result.nodes.get(n).parent = node.path("parent").asInt(0);

                    JsonNode rot = root.path("rotation");
                    result.nodes.get(n).rotation = new double[rot.size()];
                    for (int j = 0; j < rot.size(); j++) {
                        result.nodes.get(n).rotation[j] = rot.get(j).asDouble();
                    }
                    result.nodes.get(n).childrenCount = rot.size();

                    JsonNode sca = root.path("scale");
                    result.nodes.get(n).scale = new double[sca.size()];
                    for (int j = 0; j < sca.size(); j++) {
                        result.nodes.get(n).rotation[j] = sca.get(j).asDouble();
                    }
                    result.nodes.get(n).childrenCount = sca.size();

                    JsonNode trans = root.path("translation");
                    result.nodes.get(n).translation = new double[trans.size()];
                    for (int j = 0; j < trans.size(); j++) {
                        result.nodes.get(n).rotation[j] = trans.get(j).asDouble();
                    }
                    result.nodes.get(n).childrenCount = trans.size();

                    JsonNode mat = root.path("matrix");
                    result.nodes.get(n).matrix = new double[mat.size()];
                    for (int j = 0; j < mat.size(); j++) {
                        result.nodes.get(n).rotation[j] = mat.get(j).asDouble();
                    }
                    result.nodes.get(n).childrenCount = mat.size();

                    JsonNode wei = root.path("weights");
                    result.nodes.get(n).weights = new double[wei.size()];
                    for (int j = 0; j < wei.size(); j++) {
                        result.nodes.get(n).rotation[j] = wei.get(j).asDouble();
                    }
                    result.nodes.get(n).childrenCount = wei.size();

                    JsonNode chi = root.path("children");
                    result.nodes.get(n).children = new int[chi.size()];
                    for (int j = 0; j < chi.size(); j++) {
                        result.nodes.get(n).rotation[j] = chi.get(j).asInt();
                    }
                    result.nodes.get(n).childrenCount = chi.size();

                }
                result.nodeCount = n;
            }

            result.scene = root.path("scene").asInt();

            JsonNode scenes = root.path("scenes");
            if (!scenes.isMissingNode()) {
                int s = 0;
                for (JsonNode node : scenes) {
                    result.scenes.add(s, new gltfj_Scene());

                    result.scenes.get(s).name = node.path("name").asText();
                    JsonNode sNode = root.path("nodes");
                    result.scenes.get(s).nodes = new int[sNode.size()];
                    for (int j = 0; j < sNode.size(); j++) {
                        result.scenes.get(s).nodes[j] = sNode.get(j).asInt();
                    }
                }
                result.sceneCount = s;
            }

            JsonNode animations = root.path("animations");
            if (!animations.isMissingNode()) {
                int a = 0;
                for (JsonNode node : animations) {
                    result.animations.add(a, new gltfj_Animation());

                    result.animations.get(a).name = node.path("name").asText();

                    JsonNode channels = node.path("channels");
                    if (!channels.isMissingNode()) {
                        int c = 0;
                        for (JsonNode channel : channels) {
                            result.animations.get(a).channels.add(c, new gltfj_AnimationChannel());
                            result.animations.get(a).channels.get(c).sampler = channel.path("sampler").asInt(0);
                            result.animations.get(a).channels.get(c).targetNode = channel.path("target").path("node").asInt();
                            result.animations.get(a).channels.get(c).targetPath = gltfj_AnimationChannel.AnimationPathType.valueOf(channel.path("target").path("path").asText("INVALID").toUpperCase());

                            c++;
                        }
                        result.animations.get(a).channelCount = a;
                    }

                    JsonNode aSamplers = node.path("samplers");
                    if (!aSamplers.isMissingNode()) {
                        int s = 0;
                        for (JsonNode sampler : aSamplers) {
                            result.animations.get(a).samplers.add(s, new gltfj_AnimationSampler());

                            result.animations.get(a).samplers.get(s).input = sampler.path("input").asInt();
                            result.animations.get(a).samplers.get(s).output = sampler.path("output").asInt();
                            result.animations.get(a).samplers.get(s).interpolation = gltfj_AnimationSampler.InterpolationType.valueOf(sampler.path("interpolation").asText().toUpperCase());
                        }
                        result.animations.get(a).samplerCount = s;
                    }
                }
                result.animationCount = a;
            }

            JsonNode materialVariants = root.path("materialVariants");
            if (!materialVariants.isMissingNode()) {
                // TODO: 8/20/23 materialVariants
            }

            JsonNode extensions = root.path("extensions");
            if (!extensions.isMissingNode()) {
                // TODO: 8/20/23 extensions
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }

    private static gltfj_glTF ReadBinary(byte[] fileData) {
        gltfj_glTF result = new gltfj_glTF();

        int ptr = 0;
        char[] magic = new char[4];
        for (int i = 0; i < 4; i++, ptr++) {
            magic[i] = (char) fileData[ptr];
        }
        if (magic[0] != 'g'|| magic[1] != 'l' || magic[2] != 'T' || magic[3] != 'F') {
            System.out.println("[glTF-J] Error: File failed checksum. Invalid binary data.");
            return result;
        }

        int version;
        byte[] buffer = new byte[Integer.BYTES];
        for (int i = 0; i < Integer.BYTES; i++, ptr++) {
            buffer[i] = fileData[ptr];
        }
        version = ByteArrayToInt(buffer);
        System.out.println("[glTF-J] INFO: file version: " + version);

        int size;
        for (int i = 0; i < Integer.BYTES; i++, ptr++) {
            buffer[i] = fileData[ptr];
        }
        size = ByteArrayToInt(buffer);
        System.out.println("[glTF-J] INFO: file size: " + size + " bytes");

        //parse chunks
        while (ptr < fileData.length){
            int chunkLength, chunkType;
            for (int i = 0; i < Integer.BYTES; i++, ptr++) {
                buffer[i] = fileData[ptr];
            }
            chunkLength = ByteArrayToInt(buffer);
            for (int i = 0; i < Integer.BYTES; i++, ptr++) {
                buffer[i] = fileData[ptr];
            }
            chunkType = ByteArrayToInt(buffer);

            if (chunkType == 0x4E4F534A) {
                //Chunk is type JSON
                String jsonText = "";
                for (int i = 0; i < chunkLength; i++, ptr++) {
                    jsonText += (char) fileData[ptr];
                }
                result = ReadJSON(jsonText);
            }
            else {
                //Chunk is not type JSON and clearly of the BIN.
                for (int b = 0; b < result.bufferCount; b++) {
                    result.buffers.get(b).data = new byte[result.buffers.get(b).size];
                    for (int i = 0; i < result.buffers.get(b).size; i++, ptr++) {
                        result.buffers.get(b).data[i] = fileData[ptr];
                    }
                }
            }
        }


        return result;
    }


    //-----
    //utils
    //-----

    private static String LoadFileText(String fileName) {
        InputStream inputStream;
        if (fileName.contains("/")) {
            inputStream = gltfj.class.getResourceAsStream(fileName.substring(fileName.lastIndexOf('/')));
        } else {
            inputStream = gltfj.class.getResourceAsStream("/" + fileName);
        }
        if (inputStream == null) {
            String ext = fileName.substring(fileName.lastIndexOf('.')).toUpperCase();
            inputStream = gltfj.class.getResourceAsStream(fileName.substring(0, fileName.lastIndexOf('.')) + ext);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String[] tmp = reader.lines().toArray(String[]::new);
        StringBuilder builder = new StringBuilder();

        for (String s : tmp) {
            builder.append(s);
            builder.append("\n");
        }

        if (tmp != null) {
            return builder.toString();
        }
        return null;
    }

    private static byte[] LoadFileData(String fileName) {
        byte[] data = null;

        InputStream inputStream;
        if (fileName.contains("/")) {
            inputStream = gltfj.class.getResourceAsStream(fileName.substring(fileName.lastIndexOf('/')));
        } else {
            inputStream = gltfj.class.getResourceAsStream("/" + fileName);
        }
        if (inputStream == null) {
            String ext = fileName.substring(fileName.lastIndexOf('.')).toUpperCase();
            inputStream = gltfj.class.getResourceAsStream(fileName.substring(0, fileName.lastIndexOf('.')) + ext);
        }

        if (inputStream != null) {
            try {
                data = new byte[inputStream.available()];
                inputStream.read(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    // Convert array of four bytes to int
    private static int ByteArrayToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

}
