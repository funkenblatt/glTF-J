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
import com.fasterxml.jackson.jr.ob.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import static com.creedvi.utils.gltfj.gltf.gltfj_Light.LightType.INVALID;

public class gltfj {

    private static String filepath;

    public static gltfj_glTF Read(String fileName) {
        gltfj_glTF result = null;

        filepath = fileName.substring(0, fileName.lastIndexOf("/") + 1);

        if (fileName.substring(fileName.lastIndexOf(".")).equalsIgnoreCase(".gltf")) {
            String jsonText = LoadFileText(fileName);
            result = ReadJSON(jsonText);
        } else if (fileName.substring(fileName.lastIndexOf(".")).equalsIgnoreCase(".glb")) {
            byte[] fileData = LoadFileData(fileName);
            result = ReadBinary(fileData);
        } else {
            System.out.println("[glTF-J] ERROR: Unknown file type " + fileName.substring(fileName.lastIndexOf(".")));
        }

        return result;
    }

    private static gltfj_glTF ReadJSON(String jsonText) {
        Map<String, Object> jsonObjects;
        gltfj_glTF result = new gltfj_glTF();

        try {
            jsonObjects = JSON.std.mapFrom(jsonText);

            Map<String, Object> assetMap = (Map<String, Object>) jsonObjects.get("asset");
            if (assetMap != null) {
                result.asset.version = String.valueOf(assetMap.get("version"));
                result.asset.minVersion = String.valueOf(assetMap.get("minVersion"));
                result.asset.generator = String.valueOf(assetMap.get("generator"));
                result.asset.copyright = String.valueOf(assetMap.get("copyright"));
            }

            ArrayList<Map> meshes = (ArrayList<Map>) jsonObjects.get("meshes");
            if (meshes != null) {
                for (int m = 0; m < meshes.size(); m++) {
                    result.meshes.add(m, new gltfj_Mesh());

                    result.meshes.get(m).name = String.valueOf(meshes.get(m).get("name"));
                    ArrayList<Double> m_weight = (ArrayList<Double>) meshes.get(m).get("weights");
                    if (m_weight != null) {
                        result.meshes.get(m).weights = new double[m_weight.size()];
                        for (int w = 0; w < m_weight.size(); w++) {
                            result.meshes.get(m).weights[w] = m_weight.get(w);
                        }
                        result.meshes.get(m).weightsCount = m_weight.size();
                    }

                    ArrayList<Map> primitives = (ArrayList<Map>) meshes.get(m).get("primitives");
                    for (int p = 0; p < primitives.size(); p++) {
                        result.meshes.get(m).primitives.add(p, new gltfj_Primitive());

                        result.meshes.get(m).primitives.get(p).indices = (primitives.get(p).get("indices") == null) ? 0 : (int) primitives.get(p).get("indices");
                        result.meshes.get(m).primitives.get(p).material = (primitives.get(p).get("material") == null) ? 0 : (int) primitives.get(p).get("material");
                        int mode = (primitives.get(p).get("mode") == null) ? 0 : (int) primitives.get(p).get("mode");
                        result.meshes.get(m).primitives.get(p).type = gltfj_Primitive.PrimitiveType.values()[mode];

                        Map<String, Integer> attrib = (Map<String, Integer>) primitives.get(p).get("attributes");
                        int a = 0;
                        for (Map.Entry<String, Integer> entry : attrib.entrySet()) {
                            result.meshes.get(m).primitives.get(p).attributes.add(a, new gltfj_Attribute());
                            result.meshes.get(m).primitives.get(p).attributes.get(a).type = gltfj_Attribute.AttributeType.valueOf(entry.getKey());
                            result.meshes.get(m).primitives.get(p).attributes.get(a).index = entry.getValue();
                            a++;
                        }
                        result.meshes.get(m).primitives.get(p).attributesCount = a;

                        ArrayList<Map> tgts = (ArrayList<Map>) primitives.get(p).get("targets");
                        if (tgts != null) {
                            for (int t = 0; t < tgts.size(); t++) {
                                result.meshes.get(m).primitives.get(p).targets.add(t, new gltfj_MorphTarget());

                                Map<String, Integer> tgt_attrib = tgts.get(0);
                                int ta = 0;
                                for (Map.Entry<String, Integer> entry : tgt_attrib.entrySet()) {
                                    result.meshes.get(m).primitives.get(p).targets.get(t).attributes.add(ta, new gltfj_Attribute());
                                    result.meshes.get(m).primitives.get(p).targets.get(t).attributes.get(ta).type = gltfj_Attribute.AttributeType.valueOf(entry.getKey());
                                    result.meshes.get(m).primitives.get(p).targets.get(t).attributes.get(ta).index = entry.getValue();
                                    ta++;
                                }
                                result.meshes.get(m).primitives.get(p).targets.get(t).attributeCount = ta;

                            }
                        }

                    }

                }
                result.meshCount = result.meshes.size();
            }

            ArrayList<Map> materials = (ArrayList<Map>) jsonObjects.get("materials");
            if (materials != null) {
                for (int m = 0; m < materials.size(); m++) {
                    result.materials.add(m, new gltfj_Material());

                    result.materials.get(m).name = String.valueOf(materials.get(m).get("name"));
                    result.materials.get(m).alphaMode = (materials.get(m).get("alphaMode") == null) ? null : gltfj_Material.AlphaMode.valueOf(String.valueOf(materials.get(m).get("alphaMode")));
                    result.materials.get(m).alphaCutoff = (materials.get(m).get("alpheCutoff") == null) ? 0 : (double) materials.get(m).get("alpheCutoff");
                    result.materials.get(m).doubleSided = (materials.get(m).get("doubleSided") == null) ? false : (boolean) materials.get(m).get("doubleSided");
                    result.materials.get(m).unlit = (materials.get(m).get("unlit") == null) ? false : (boolean) materials.get(m).get("unlit");
                    ArrayList<Double> emf = (ArrayList<Double>) materials.get(m).get("emissiveFactor");
                    if (emf != null) {
                        for (int emff = 0; emff < emf.size(); emff++) {
                            result.materials.get(m).emissiveFactor[emff] = emf.get(emff);
                        }
                    }

                    Map<String, Object> normalTexture = (Map<String, Object>) materials.get(m).get("normalTexture");
                    if (normalTexture != null) {
                        result.materials.get(m).normalTexture.texture = (int) normalTexture.get("index");
                        result.materials.get(m).normalTexture.texcoord = (int) normalTexture.get("texCoord");
                        result.materials.get(m).normalTexture.scale = (normalTexture.get("scale") == null) ? 1.0f : (double) normalTexture.get("scale");

                        Map<String, Object> trans = (Map<String, Object>) normalTexture.get("transform");
                        if (trans != null) {
                            result.materials.get(m).normalTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                            result.materials.get(m).normalTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                            ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                            if (offset != null) {
                                for (int of = 0; of < offset.size(); of++) {
                                    result.materials.get(m).normalTexture.transform.offset[of] = offset.get(of);
                                }
                            }
                            ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                            if (scale != null) {
                                for (int sf = 0; sf < scale.size(); sf++) {
                                    result.materials.get(m).normalTexture.transform.scale[sf] = scale.get(sf);
                                }
                            }
                        }
                    }

                    Map<String, Object> occlusionTexture = (Map<String, Object>) materials.get(m).get("occlusionTexture");
                    if (occlusionTexture != null) {
                        result.materials.get(m).occlusionTexture.texture = (int) occlusionTexture.get("index");
                        result.materials.get(m).occlusionTexture.texcoord = (int) occlusionTexture.get("texCoord");
                        result.materials.get(m).occlusionTexture.scale = (occlusionTexture.get("scale") == null) ? 1.0f : (double) occlusionTexture.get("scale");

                        Map<String, Object> trans = (Map<String, Object>) occlusionTexture.get("transform");
                        if (trans != null) {
                            result.materials.get(m).occlusionTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                            result.materials.get(m).occlusionTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                            ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                            if (offset != null) {
                                for (int of = 0; of < offset.size(); of++) {
                                    result.materials.get(m).occlusionTexture.transform.offset[of] = offset.get(of);
                                }
                            }
                            ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                            if (scale != null) {
                                for (int sf = 0; sf < scale.size(); sf++) {
                                    result.materials.get(m).occlusionTexture.transform.scale[sf] = scale.get(sf);
                                }
                            }
                        }
                    }

                    Map<String, Object> emissiveTexture = (Map<String, Object>) materials.get(m).get("emissiveTexture");
                    if (emissiveTexture != null) {
                        result.materials.get(m).emissiveTexture.texture = (int) emissiveTexture.get("index");
                        result.materials.get(m).emissiveTexture.texcoord = (int) emissiveTexture.get("texCoord");
                        result.materials.get(m).emissiveTexture.scale = (emissiveTexture.get("scale") == null) ? 1.0f : (double) emissiveTexture.get("scale");

                        Map<String, Object> trans = (Map<String, Object>) emissiveTexture.get("transform");
                        if (trans != null) {
                            result.materials.get(m).emissiveTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                            result.materials.get(m).emissiveTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                            ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                            if (offset != null) {
                                for (int of = 0; of < offset.size(); of++) {
                                    result.materials.get(m).emissiveTexture.transform.offset[of] = offset.get(of);
                                }
                            }
                            ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                            if (scale != null) {
                                for (int sf = 0; sf < scale.size(); sf++) {
                                    result.materials.get(m).emissiveTexture.transform.scale[sf] = scale.get(sf);
                                }
                            }
                        }
                    }

                    Map<String, Object> met = (Map<String, Object>) materials.get(m).get("pbrMetallicRoughness");
                    if (met != null) {
                        result.materials.get(m).hasMetallicRoughness = true;

                        ArrayList<Double> baseColour = (ArrayList<Double>) met.get("baseColorFactor");
                        if (baseColour != null) {
                            for (int bcf = 0; bcf < baseColour.size(); bcf++) {
                                result.materials.get(m).metallicRoughness.baseColorFactor[bcf] = baseColour.get(bcf);
                            }
                        }
                        result.materials.get(m).metallicRoughness.metallicFactor = (met.get("metallicFactor") == null) ? 0f : (double) met.get("metallicFactor");
                        result.materials.get(m).metallicRoughness.roughnessFactor = (met.get("roughnessFactor") == null) ? 0f : (double) met.get("roughnessFactor");

                        Map<String, Object> basetex = (Map<String, Object>) met.get("baseColorTexture");
                        if (basetex != null) {
                            result.materials.get(m).metallicRoughness.baseColorTexture.texture = (basetex.get("index") == null) ? 0 : (int) basetex.get("index");
                            result.materials.get(m).metallicRoughness.baseColorTexture.texcoord = (basetex.get("texCoord") == null) ? 0 : (int) basetex.get("texCoord");
                            result.materials.get(m).metallicRoughness.baseColorTexture.scale = (basetex.get("scale") == null) ? 1.0f : (double) basetex.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) basetex.get("transform");
                            if (trans != null) {
                                result.materials.get(m).metallicRoughness.baseColorTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).metallicRoughness.baseColorTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).metallicRoughness.baseColorTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).metallicRoughness.baseColorTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }

                        }

                        Map<String, Object> mrtex = (Map<String, Object>) met.get("metallicRoughnessTexture");
                        if (mrtex != null) {
                            result.materials.get(m).metallicRoughness.baseColorTexture.texture = (basetex.get("index") == null) ? 0 : (int) basetex.get("index");
                            result.materials.get(m).metallicRoughness.baseColorTexture.texcoord = (basetex.get("texCoord") == null) ? 0 : (int) basetex.get("texCoord");
                            result.materials.get(m).metallicRoughness.metallicRoughnessTexture.scale = (mrtex.get("scale") == null) ? 1.0f : (double) mrtex.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) mrtex.get("transform");
                            if (trans != null) {
                                result.materials.get(m).metallicRoughness.metallicRoughnessTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).metallicRoughness.metallicRoughnessTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).metallicRoughness.metallicRoughnessTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).metallicRoughness.metallicRoughnessTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }
                        }
                    }

                    Map<String, Object> specGloss = (Map<String, Object>) materials.get(m).get("pbrSpecularGlossiness");
                    if (specGloss != null) {
                        result.materials.get(m).hasSpecularGlossiness = true;

                        ArrayList<Double> diffuseFactor = (ArrayList<Double>) specGloss.get("diffuseFactor");
                        for (int diff = 0; diff < diffuseFactor.size(); diff++) {
                            result.materials.get(m).specularGlossiness.diffuseFactor[diff] = diffuseFactor.get(diff);
                        }
                        ArrayList<Double> specularFactor = (ArrayList<Double>) specGloss.get("specularFactor");
                        for (int specf = 0; specf < diffuseFactor.size(); specf++) {
                            result.materials.get(m).specularGlossiness.specularFactor[specf] = specularFactor.get(specf);
                        }
                        result.materials.get(m).specularGlossiness.glossinessFactor = (double) specGloss.get("glossinessFactor");

                        Map<String, Object> diffuseTexture = (Map<String, Object>) specGloss.get("diffuseTexture");
                        if (diffuseTexture != null) {
                            result.materials.get(m).metallicRoughness.baseColorTexture.texture = (diffuseTexture.get("index") == null) ? 0 : (int) diffuseTexture.get("index");
                            result.materials.get(m).metallicRoughness.baseColorTexture.texcoord = (diffuseTexture.get("texCoord") == null) ? 0 : (int) diffuseTexture.get("texCoord");
                            result.materials.get(m).specularGlossiness.diffuseTexture.scale = (diffuseTexture.get("scale") == null) ? 1.0f : (double) diffuseTexture.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) diffuseTexture.get("transform");
                            if (trans != null) {
                                result.materials.get(m).specularGlossiness.diffuseTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).specularGlossiness.diffuseTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).specularGlossiness.diffuseTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).specularGlossiness.diffuseTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }

                        }

                        Map<String, Object> sgtex = (Map<String, Object>) specGloss.get("specularGlossinessTexture");
                        if (sgtex != null) {
                            result.materials.get(m).specularGlossiness.specularGlossinessTexture.texture = (int) sgtex.get("index");
                            result.materials.get(m).specularGlossiness.specularGlossinessTexture.texcoord = (int) sgtex.get("texCoord");
                            result.materials.get(m).specularGlossiness.specularGlossinessTexture.scale = (sgtex.get("scale") == null) ? 1.0f : (double) sgtex.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) sgtex.get("transform");
                            if (trans != null) {
                                result.materials.get(m).specularGlossiness.specularGlossinessTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).specularGlossiness.specularGlossinessTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).specularGlossiness.specularGlossinessTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).specularGlossiness.specularGlossinessTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }
                        }
                    }

                    Map<String, Object> sheen = (Map<String, Object>) materials.get(m).get("sheen");
                    if (sheen != null) {
                        result.materials.get(m).hasSheen = true;

                        ArrayList<Double> sheenColor = (ArrayList<Double>) sheen.get("sheenColorFactor");
                        for (int sh = 0; sh < sheenColor.size(); sh++) {
                            result.materials.get(m).sheen.sheenColorFactor[sh] = sheenColor.get(sh);
                        }
                        result.materials.get(m).sheen.sheenRoughnessFactor = (double) sheen.get("sheenRoughnessFactor");

                        Map<String, Object> sctex = (Map<String, Object>) sheen.get("sheenColorTexture");
                        if (sctex != null) {
                            result.materials.get(m).sheen.sheenColorTexture.texture = (int) sctex.get("index");
                            result.materials.get(m).sheen.sheenColorTexture.texcoord = (int) sctex.get("texCoord");
                            result.materials.get(m).sheen.sheenColorTexture.scale = (sctex.get("scale") == null) ? 1.0f : (double) sctex.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) sctex.get("transform");
                            if (trans != null) {
                                result.materials.get(m).sheen.sheenColorTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).sheen.sheenColorTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).sheen.sheenColorTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).sheen.sheenColorTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }

                        }

                        Map<String, Object> srtex = (Map<String, Object>) sheen.get("sheenRoughnessTexture");
                        if (srtex != null) {
                            result.materials.get(m).sheen.sheenRoughnessTexture.texture = (int) srtex.get("index");
                            result.materials.get(m).sheen.sheenRoughnessTexture.texcoord = (int) srtex.get("texCoord");
                            result.materials.get(m).sheen.sheenRoughnessTexture.scale = (srtex.get("scale") == null) ? 1.0f : (double) srtex.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) srtex.get("transform");
                            if (trans != null) {
                                result.materials.get(m).sheen.sheenRoughnessTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).sheen.sheenRoughnessTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).sheen.sheenRoughnessTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).sheen.sheenRoughnessTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }
                        }
                    }

                    Map<String, Object> spec = (Map<String, Object>) materials.get(m).get("pbrSpecularGlossiness");
                    if (spec != null) {
                        result.materials.get(m).hasSpecular = true;

                        ArrayList<Double> scFactor = (ArrayList<Double>) spec.get("specularColorFactor");
                        for (int diff = 0; diff < scFactor.size(); diff++) {
                            result.materials.get(m).specular.specularColorFactor[diff] = scFactor.get(diff);
                        }
                        result.materials.get(m).specular.specularFactor = (double) spec.get("glossinessFactor");

                        Map<String, Object> specularTexture = (Map<String, Object>) spec.get("specularTexture");
                        if (specularTexture != null) {
                            result.materials.get(m).specular.specularTexture.texture = (int) specularTexture.get("index");
                            result.materials.get(m).specular.specularTexture.texcoord = (int) specularTexture.get("texCoord");
                            result.materials.get(m).specular.specularTexture.scale = (specularTexture.get("scale") == null) ? 1.0f : (double) specularTexture.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) specularTexture.get("transform");
                            if (trans != null) {
                                result.materials.get(m).specular.specularTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).specular.specularTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).specular.specularTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).specular.specularTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }

                        }

                        Map<String, Object> specularColorTexture = (Map<String, Object>) spec.get("specularColorTexture");
                        if (specularColorTexture != null) {
                            result.materials.get(m).specular.specularColorTexture.texture = (int) specularColorTexture.get("index");
                            result.materials.get(m).specular.specularColorTexture.texcoord = (int) specularColorTexture.get("texCoord");
                            result.materials.get(m).specular.specularColorTexture.scale = (specularColorTexture.get("scale") == null) ? 1.0f : (double) specularColorTexture.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) specularColorTexture.get("transform");
                            if (trans != null) {
                                result.materials.get(m).specular.specularColorTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).specular.specularColorTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).specular.specularColorTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).specular.specularColorTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }
                        }
                    }

                    Map<String, Object> transmission = (Map<String, Object>) materials.get(m).get("transmission");
                    if (transmission != null) {
                        result.materials.get(m).hasTransmission = true;

                        result.materials.get(m).transmission.transmissionFactor = (double) transmission.get("transmissionFactor");

                        Map<String, Object> transmissionTexture = (Map<String, Object>) transmission.get("transmissionTexture");
                        if (transmissionTexture != null) {
                            result.materials.get(m).transmission.transmissionTexture.texture = (int) transmissionTexture.get("index");
                            result.materials.get(m).transmission.transmissionTexture.texcoord = (int) transmissionTexture.get("texCoord");
                            result.materials.get(m).transmission.transmissionTexture.scale = (transmissionTexture.get("scale") == null) ? 1.0f : (double) transmissionTexture.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) transmissionTexture.get("transform");
                            if (trans != null) {
                                result.materials.get(m).transmission.transmissionTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).transmission.transmissionTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).transmission.transmissionTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).transmission.transmissionTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }

                        }
                    }

                    Map<String, Object> vol = (Map<String, Object>) materials.get(m).get("volume");
                    if (vol != null) {
                        result.materials.get(m).hasVolume = true;

                        ArrayList<Double> attnFactor = (ArrayList<Double>) vol.get("attenuationColor");
                        for (int diff = 0; diff < attnFactor.size(); diff++) {
                            result.materials.get(m).volume.attenuationColor[diff] = attnFactor.get(diff);
                        }
                        result.materials.get(m).volume.thicknessFactor = (double) vol.get("thicknessFactor");
                        result.materials.get(m).volume.attenuationDistance = (double) vol.get("attenuationDistance");

                        Map<String, Object> thicknessTexture = (Map<String, Object>) vol.get("thicknessTexture");
                        if (thicknessTexture != null) {
                            result.materials.get(m).volume.thicknessTexture.texture = (int) thicknessTexture.get("index");
                            result.materials.get(m).volume.thicknessTexture.texcoord = (int) thicknessTexture.get("texCoord");
                            result.materials.get(m).volume.thicknessTexture.scale = (thicknessTexture.get("scale") == null) ? 1.0f : (double) thicknessTexture.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) thicknessTexture.get("transform");
                            if (trans != null) {
                                result.materials.get(m).volume.thicknessTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).volume.thicknessTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).volume.thicknessTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).volume.thicknessTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }

                        }
                    }

                    Map<String, Object> clear = (Map<String, Object>) materials.get(m).get("clearCoat");
                    if (clear != null) {
                        result.materials.get(m).hasClearCoat = true;

                        ArrayList<Double> difFactor = (ArrayList<Double>) clear.get("diffuseFactor");
                        for (int diff = 0; diff < difFactor.size(); diff++) {
                            result.materials.get(m).clearCoat.diffuseFactor[diff] = difFactor.get(diff);
                        }
                        ArrayList<Double> specFactor = (ArrayList<Double>) clear.get("specularFactor");
                        for (int specf = 0; specf < specFactor.size(); specf++) {
                            result.materials.get(m).clearCoat.specularFactor[specf] = specFactor.get(specf);
                        }
                        result.materials.get(m).clearCoat.glossinessFactor = (double) clear.get("glossinessFactor");

                        Map<String, Object> clearCoatTexture = (Map<String, Object>) clear.get("clearCoatTexture");
                        if (clearCoatTexture != null) {
                            result.materials.get(m).clearCoat.clearCoatTexture.texture = (int) clearCoatTexture.get("index");
                            result.materials.get(m).clearCoat.clearCoatTexture.texcoord = (int) clearCoatTexture.get("texCoord");
                            result.materials.get(m).clearCoat.clearCoatTexture.scale = (clearCoatTexture.get("scale") == null) ? 1.0f : (double) clearCoatTexture.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) clearCoatTexture.get("transform");
                            if (trans != null) {
                                result.materials.get(m).clearCoat.clearCoatTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).clearCoat.clearCoatTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).clearCoat.clearCoatTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).clearCoat.clearCoatTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }

                        }

                        Map<String, Object> clearCoatRoughnessTexture = (Map<String, Object>) clear.get("clearCoatRoughnessTexture");
                        if (clearCoatRoughnessTexture != null) {
                            result.materials.get(m).clearCoat.clearCoatRoughnessTexture.texture = (int) clearCoatRoughnessTexture.get("index");
                            result.materials.get(m).clearCoat.clearCoatRoughnessTexture.texcoord = (int) clearCoatRoughnessTexture.get("texCoord");
                            result.materials.get(m).clearCoat.clearCoatRoughnessTexture.scale = (clearCoatRoughnessTexture.get("scale") == null) ? 1.0f : (double) clearCoatRoughnessTexture.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) clearCoatRoughnessTexture.get("transform");
                            if (trans != null) {
                                result.materials.get(m).clearCoat.clearCoatRoughnessTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).clearCoat.clearCoatRoughnessTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).clearCoat.clearCoatRoughnessTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).clearCoat.clearCoatRoughnessTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }
                        }

                        Map<String, Object> clearCoatNormalTexture = (Map<String, Object>) clear.get("clearCoatNormalTexture");
                        if (clearCoatNormalTexture != null) {
                            result.materials.get(m).clearCoat.clearCoatNormalTexture.texture = (int) clearCoatNormalTexture.get("index");
                            result.materials.get(m).clearCoat.clearCoatNormalTexture.texcoord = (int) clearCoatNormalTexture.get("texCoord");
                            result.materials.get(m).clearCoat.clearCoatNormalTexture.scale = (clearCoatNormalTexture.get("scale") == null) ? 1.0f : (double) clearCoatNormalTexture.get("scale");

                            Map<String, Object> trans = (Map<String, Object>) clearCoatNormalTexture.get("transform");
                            if (trans != null) {
                                result.materials.get(m).clearCoat.clearCoatNormalTexture.transform.textcoord = (trans.get("texCoord") == null) ? 0 : (int) trans.get("texCoord");
                                result.materials.get(m).clearCoat.clearCoatNormalTexture.transform.rotation = (trans.get("rotation") == null) ? 0.0f : (double) trans.get("rotation");
                                ArrayList<Double> offset = (ArrayList<Double>) trans.get("offset");
                                if (offset != null) {
                                    for (int of = 0; of < offset.size(); of++) {
                                        result.materials.get(m).clearCoat.clearCoatNormalTexture.transform.offset[of] = offset.get(of);
                                    }
                                }
                                ArrayList<Double> scale = (ArrayList<Double>) trans.get("scale");
                                if (scale != null) {
                                    for (int sf = 0; sf < scale.size(); sf++) {
                                        result.materials.get(m).clearCoat.clearCoatNormalTexture.transform.scale[sf] = scale.get(sf);
                                    }
                                }
                            }

                        }
                    }

                    Map<String, Object> ior = (Map<String, Object>) materials.get(m).get("ior");
                    if (ior != null) {
                        result.materials.get(m).hasIor = true;
                        result.materials.get(m).ior.ior = (double) ior.get("ior");
                    }

                }
                result.materialCount = materials.size();
            }

            ArrayList<Map> accessors = (ArrayList<Map>) jsonObjects.get("accessors");
            if (accessors != null) {
                for (int a = 0; a < accessors.size(); a++) {
                    result.accessors.add(a, new gltfj_Accessor());

                    result.accessors.get(a).bufferView = (accessors.get(a).get("bufferView") == null) ? 0 : (int) accessors.get(a).get("bufferView");
                    result.accessors.get(a).byteOffset = (accessors.get(a).get("byteOffset") == null) ? 0 : (int) accessors.get(a).get("byteOffset");
                    result.accessors.get(a).count = (accessors.get(a).get("count") == null) ? 0 : (int) accessors.get(a).get("count");

                    int compType = (accessors.get(a).get("componentType") == null) ? 5120 : (int) accessors.get(a).get("componentType");
                    result.accessors.get(a).componentType = gltfj_Accessor.AccessorDataType.values()[compType - 5120];

                    String type = String.valueOf(accessors.get(a).get("type"));
                    result.accessors.get(a).type = gltfj_Accessor.AccessorType.valueOf(type.toUpperCase());

                    switch (result.accessors.get(a).componentType) {
                        case SIGNED_BYTE:
                        case UNSIGNED_BYTE:
                            result.accessors.get(a).max = (ArrayList<Byte>) accessors.get(a).get("max");
                            result.accessors.get(a).min = (ArrayList<Byte>) accessors.get(a).get("min");
                            break;
                        case SIGNED_SHORT:
                        case UNSIGNED_SHORT:
                            result.accessors.get(a).max = (ArrayList<Short>) accessors.get(a).get("max");
                            result.accessors.get(a).min = (ArrayList<Short>) accessors.get(a).get("min");
                            break;
                        case UNSIGNED_INT:
                            result.accessors.get(a).max = (ArrayList<Integer>) accessors.get(a).get("max");
                            result.accessors.get(a).min = (ArrayList<Integer>) accessors.get(a).get("min");
                            break;
                        case FLOAT:
                            result.accessors.get(a).max = (ArrayList<Float>) accessors.get(a).get("max");
                            result.accessors.get(a).min = (ArrayList<Float>) accessors.get(a).get("min");
                            break;
                    }

                    // TODO: 8/11/23 Sparse Accessors

                }
                result.accessorCount = result.accessors.size();
            }

            ArrayList<Map> bufferViews = (ArrayList<Map>) jsonObjects.get("bufferViews");
            if (bufferViews != null) {
                for (int bv = 0; bv < bufferViews.size(); bv++) {
                    result.bufferViews.add(bv, new gltfj_BufferView());

                    result.bufferViews.get(bv).name = String.valueOf(bufferViews.get(bv).get("name"));
                    result.bufferViews.get(bv).buffer = (bufferViews.get(bv).get("buffer") == null) ? 0 : (int) bufferViews.get(bv).get("buffer");
                    result.bufferViews.get(bv).offset = (bufferViews.get(bv).get("offset") == null) ? 0 : (int) bufferViews.get(bv).get("offset");
                    result.bufferViews.get(bv).size = (bufferViews.get(bv).get("byteLength") == null) ? 1 : (int) bufferViews.get(bv).get("byteLength");
                    result.bufferViews.get(bv).stride = (bufferViews.get(bv).get("byteStride") == null) ? 4 : (int) bufferViews.get(bv).get("byteStride");

                    int tgt = (bufferViews.get(bv).get("target") == null) ? -1 : (int) bufferViews.get(bv).get("target");
                    if (tgt == 34962) {
                        result.bufferViews.get(bv).target = gltfj_BufferView.BufferViewTarget.ARRAY_BUFFER;
                    } else if (tgt == 34963) {
                        result.bufferViews.get(bv).target = gltfj_BufferView.BufferViewTarget.ELEMENT_ARRAY_BUFFER;
                    } else {
                        result.bufferViews.get(bv).target = gltfj_BufferView.BufferViewTarget.INVALID;
                    }

                }
                result.bufferViewCount = result.bufferViews.size();
            }

            ArrayList<Map> buffers = (ArrayList<Map>) jsonObjects.get("buffers");
            if (buffers != null) {
                for (int b = 0; b < buffers.size(); b++) {
                    result.buffers.add(b, new gltfj_Buffer());
                    result.buffers.get(b).name = String.valueOf(buffers.get(b).get("name"));
                    result.buffers.get(b).uri = (String) buffers.get(b).get("uri");
                    result.buffers.get(b).size = (buffers.get(b).get("byteLength") == null) ? -1 : (int) buffers.get(b).get("byteLength");

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
                }
                result.bufferCount = result.buffers.size();
            }

            ArrayList<Map> images = (ArrayList<Map>) jsonObjects.get("images");
            if (images != null) {
                for (int i = 0; i < images.size(); i++) {
                    result.images.add(i, new gltfj_Image());

                    result.images.get(i).name = String.valueOf(images.get(i).get("name"));
                    result.images.get(i).uri = String.valueOf(images.get(i).get("uri"));
                    result.images.get(i).mimeType = String.valueOf(images.get(i).get("mimeType"));
                    result.images.get(i).bufferView = (images.get(i).get("bufferView") == null) ? -1 : (int) images.get(i).get("bufferView");
                }
                result.imageCount = result.images.size();
            }

            ArrayList<Map> textures = (ArrayList<Map>) jsonObjects.get("textures");
            if (textures != null) {
                for (int t = 0; t < textures.size(); t++) {
                    result.textures.add(t, new gltfj_Texture());

                    result.textures.get(t).image = (int) textures.get(t).get("source");
                    result.textures.get(t).sampler = (int) textures.get(t).get("sampler");
                }
                result.textureCount = result.textures.size();
            }

            ArrayList<Map> samplers = (ArrayList<Map>) jsonObjects.get("samplers");
            if (samplers != null) {
                for (int s = 0; s < samplers.size(); s++) {
                    result.samplers.add(s, new gltfj_Sampler());

                    result.samplers.get(s).name = String.valueOf(samplers.get(s).get("name"));
                    result.samplers.get(s).magFilter = (samplers.get(s).get("magFilter") == null) ? 0 : (int) samplers.get(s).get("magFilter");
                    result.samplers.get(s).minFilter = (samplers.get(s).get("minFilter") == null) ? 0 : (int) samplers.get(s).get("minFilter");
                    result.samplers.get(s).sWrap = (samplers.get(s).get("wrapS") == null) ? 0 : (int) samplers.get(s).get("wrapS");
                    result.samplers.get(s).tWrap = (samplers.get(s).get("wrapT") == null) ? 0 : (int) samplers.get(s).get("wrapT");
                }
                result.samplerCount = result.samplers.size();
            }

            ArrayList<Map> skins = (ArrayList<Map>) jsonObjects.get("skins");
            if (skins != null) {
                for (int s = 0; s < skins.size(); s++) {
                    result.skins.add(s, new gltfj_Skin());
                    result.skins.get(s).name = (String) skins.get(s).get("name");
                    result.skins.get(s).inverseBindMatrices = (skins.get(s).get("inverseBindMatricies") == null) ? -1 : (int) skins.get(s).get("inverseBindMatricies");
                    result.skins.get(s).skeleton = (skins.get(s).get("skeleton") == null) ? -1 : (int) skins.get(s).get("skeleton");

                    ArrayList<Integer> s_joints = (ArrayList<Integer>) skins.get(s).get("joints");
                    if (s_joints != null) {
                        result.skins.get(s).joints = new int[s_joints.size()];
                        for (int j = 0; j < s_joints.size(); j++) {
                            result.skins.get(s).joints[j] = s_joints.get(j);
                        }
                        result.skins.get(s).jointsCount = s_joints.size();
                    }

                }
                result.skinCount = result.skins.size();
            }

            ArrayList<Map> cameras = (ArrayList<Map>) jsonObjects.get("cameras");
            if (cameras != null) {
                for (int c = 0; c < cameras.size(); c++) {
                    result.cameras.add(c, new gltfj_Camera());

                    result.cameras.get(c).name = String.valueOf(cameras.get(c).get("name"));
                    result.cameras.get(c).type = gltfj_Camera.CameraType.valueOf(String.valueOf(cameras.get(c).get("type")));
                    if (result.cameras.get(c).type == gltfj_Camera.CameraType.ORTHOGRAPHIC) {
                        result.cameras.get(c).data_o = new gltfj_CameraOrthographic();
                        result.cameras.get(c).orthographic = true;

                        Map<String, Object> camo = (Map<String, Object>) cameras.get(c).get("orthographic");
                        result.cameras.get(c).data_o.x_mag = (camo.get("xmag") == null) ? 0.0f : (double) camo.get("xmag");
                        result.cameras.get(c).data_o.y_mag = (camo.get("ymag") == null) ? 0.0f : (double) camo.get("ymag");
                        result.cameras.get(c).data_o.z_far = (camo.get("zfar") == null) ? 0.0f : (double) camo.get("zfar");
                        result.cameras.get(c).data_o.z_near = (camo.get("znear") == null) ? 0.0f : (double) camo.get("znear");
                    } else {
                        // assume perspective
                        result.cameras.get(c).data_p = new gltfj_CameraPerspective();
                        result.cameras.get(c).perspective = true;

                        Map<String, Object> camp = (Map<String, Object>) cameras.get(c).get("perspective");
                        result.cameras.get(c).data_p.aspectRatio = (camp.get("aspectRatio") == null) ? 0.0f : (double) camp.get("aspectRatio");
                        result.cameras.get(c).data_p.y_fov = (camp.get("yfov") == null) ? 0.0f : (double) camp.get("yfov");
                        result.cameras.get(c).data_p.z_far = (camp.get("zfar") == null) ? 0.0f : (double) camp.get("zfar");
                        result.cameras.get(c).data_p.z_near = (camp.get("znear") == null) ? 0.0f : (double) camp.get("znear");
                    }
                }
                result.cameraCount = cameras.size();
            }

            ArrayList<Map> lights = (ArrayList<Map>) jsonObjects.get("lights");
            if (lights != null) {
                for (int l = 0; l < lights.size(); l++) {
                    result.lights.add(l, new gltfj_Light());

                    result.lights.get(l).name = String.valueOf(lights.get(l).get("name"));
                    result.lights.get(l).intensity = (lights.get(l).get("intensity") == null) ? 0.0f : (double) lights.get(l).get("intensity");
                    result.lights.get(l).range = (lights.get(l).get("range") == null) ? 0.0f : (double) lights.get(l).get("range");
                    result.lights.get(l).spot_InnerConeAngle = (lights.get(l).get("innerConeAngle") == null) ? 0.0f : (double) lights.get(l).get("innerConeAngle");
                    result.lights.get(l).spot_OuterConeAngle = (lights.get(l).get("outerConeAngle") == null) ? 0.0f : (double) lights.get(l).get("outerConeAngle");
                    result.lights.get(l).type = (lights.get(l).get("type") == null) ? INVALID : gltfj_Light.LightType.valueOf(String.valueOf(lights.get(l).get("type")));

                    ArrayList<Float> colour = (ArrayList<Float>) lights.get(l).get("color");
                    if (colour != null) {
                        for (int cf = 0; cf < colour.size(); cf++) {
                            result.lights.get(l).color[cf] = colour.get(cf);
                        }
                    }
                }
                result.lightCount = lights.size();
            }

            ArrayList<Map> nodes = (ArrayList<Map>) jsonObjects.get("nodes");
            if (nodes != null) {
                for (int n = 0; n < nodes.size(); n++) {
                    result.nodes.add(n, new gltfj_Node());

                    result.nodes.get(n).name = (String) nodes.get(n).get("name");
                    result.nodes.get(n).skin = (nodes.get(n).get("skin") == null) ? -1 : (int) nodes.get(n).get("skin");
                    result.nodes.get(n).mesh = (nodes.get(n).get("mesh") == null) ? -1 : (int) nodes.get(n).get("mesh");
                    result.nodes.get(n).camera = (nodes.get(n).get("camera") == null) ? -1 : (int) nodes.get(n).get("camera");
                    result.nodes.get(n).light = (nodes.get(n).get("light") == null) ? -1 : (int) nodes.get(n).get("light");
                    result.nodes.get(n).parent = (nodes.get(n).get("parent") == null) ? -1 : (int) nodes.get(n).get("parent");
                    ArrayList<Double> rot = (ArrayList<Double>) nodes.get(n).get("rotation");
                    if (rot != null) {
                        for (int rotf = 0; rotf < rot.size(); rotf++) {
                            result.nodes.get(n).rotation[rotf] = rot.get(rotf);
                        }
                    }

                    ArrayList<Double> sca = (ArrayList<Double>) nodes.get(n).get("scale");
                    if (sca != null) {
                        for (int scaf = 0; scaf < sca.size(); scaf++) {
                            result.nodes.get(n).scale[scaf] = sca.get(scaf);
                        }
                    }
                    ArrayList<Double> trans = (ArrayList<Double>) nodes.get(n).get("translation");
                    if (trans != null) {
                        for (int transf = 0; transf < trans.size(); transf++) {
                            result.nodes.get(n).rotation[transf] = trans.get(transf);
                        }
                    }
                    ArrayList<Double> mat = (ArrayList<Double>) nodes.get(n).get("matrix");
                    if (mat != null) {
                        for (int matf = 0; matf < mat.size(); matf++) {
                            result.nodes.get(n).matrix[matf] = mat.get(matf);
                        }
                    }
                    ArrayList<Double> wei = (ArrayList<Double>) nodes.get(n).get("weights");
                    if (wei != null) {
                        result.nodes.get(n).weights = new double[wei.size()];
                        for (int weif = 0; weif < wei.size(); weif++) {
                            result.nodes.get(n).weights[weif] = wei.get(weif);
                        }
                        result.nodes.get(n).weightsCount = wei.size();
                    }
                    ArrayList<Integer> chi = (ArrayList<Integer>) nodes.get(n).get("children");
                    if (chi != null) {
                        result.nodes.get(n).children = new int[chi.size()];
                        for (int c = 0; c < chi.size(); c++) {
                            result.nodes.get(n).weights[c] = chi.get(c);
                        }
                        result.nodes.get(n).childrenCount = chi.size();
                    }
                }
                result.nodeCount = result.nodes.size();
            }

            result.scene = (int) jsonObjects.get("scene");

            ArrayList<Map> scenes = (ArrayList<Map>) jsonObjects.get("scenes");
            if (scenes != null) {
                for (int s = 0; s < scenes.size(); s++) {
                    result.scenes.add(s, new gltfj_Scene());
                    result.scenes.get(s).name = String.valueOf(scenes.get(s).get("name"));
                    ArrayList<Integer> snodes = (ArrayList<Integer>) scenes.get(s).get("nodes");
                    result.scenes.get(s).nodes = new int[nodes.size()];
                    for (int n = 0; n < nodes.size(); n++) {
                        result.scenes.get(s).nodes[n] = snodes.get(n);
                    }
                    result.scenes.get(s).nodeCount = result.scenes.get(s).nodes.length;
                }
                result.sceneCount = result.scenes.size();
            }

            ArrayList<Map> animations = (ArrayList<Map>) jsonObjects.get("animations");
            if (animations != null) {
                for (int a = 0; a < animations.size(); a++) {
                    result.animations.add(a, new gltfj_Animation());
                    result.animations.get(a).name = String.valueOf(animations.get(a).get("name"));

                    ArrayList<Map> channels = (ArrayList<Map>) animations.get(a).get("channels");
                    if (channels != null) {
                        for (int c = 0; c < channels.size(); c++) {
                            result.animations.get(a).channels.add(c, new gltfj_AnimationChannel());
                            result.animations.get(a).channels.get(c).sampler = (channels.get(c).get("sampler") == null) ? 0 : (int) channels.get(c).get("sampler");
                            Map<String, Object> target = (Map<String, Object>) channels.get(c).get("target");
                            result.animations.get(a).channels.get(c).targetNode = (target.get("node") == null) ? -1 : (int) target.get("node");
                            result.animations.get(a).channels.get(c).targetPath = (target.get("path") == null) ? gltfj_AnimationChannel.AnimationPathType.INVALID : gltfj_AnimationChannel.AnimationPathType.valueOf(String.valueOf(target.get("path")).toUpperCase());
                        }
                        result.animations.get(a).channelCount = channels.size();
                    }

                    ArrayList<Map> aSamplers = (ArrayList<Map>) animations.get(a).get("samplers");
                    if (aSamplers != null) {
                        for (int s = 0; s < aSamplers.size(); s++) {
                            result.animations.get(a).samplers.add(s, new gltfj_AnimationSampler());

                            result.animations.get(a).samplers.get(a).input = (aSamplers.get(s).get("input") == null) ? 0 : (int) aSamplers.get(s).get("input");
                            result.animations.get(a).samplers.get(a).output = (aSamplers.get(s).get("output") == null) ? 0 : (int) aSamplers.get(s).get("output");
                            result.animations.get(a).samplers.get(a).interpolation = (aSamplers.get(s).get("interpolation") == null) ? gltfj_AnimationSampler.InterpolationType.LINEAR : gltfj_AnimationSampler.InterpolationType.valueOf(String.valueOf(aSamplers.get(s).get("interpolation")).toUpperCase());
                        }
                        result.animations.get(a).samplerCount = aSamplers.size();
                    }
                }
                result.animationCount = animations.size();
            }

            // TODO: 8/15/23
            ArrayList<Map> materialVariants = (ArrayList<Map>) jsonObjects.get("materialVariants");

            // TODO: 8/15/23
            ArrayList<Map> extensions = (ArrayList<Map>) jsonObjects.get("extensions");

        } catch (IOException e) {
            throw new RuntimeException(e);
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
            return null;
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
