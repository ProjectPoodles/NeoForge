/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.neoforged.neoforge.client.indigo.renderer;

import java.util.HashMap;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.indigo.renderer.material.MaterialFinderImpl;
import net.neoforged.neoforge.client.indigo.renderer.mesh.MeshBuilderImpl;
import net.neoforged.neoforge.client.renderer.Renderer;
import net.neoforged.neoforge.client.renderer.material.MaterialFinder;
import net.neoforged.neoforge.client.renderer.material.RenderMaterial;
import net.neoforged.neoforge.client.renderer.mesh.MeshBuilder;

/**
 * The Fabric default renderer implementation. Supports all
 * features defined in the API except shaders and offers no special materials.
 */
public class IndigoRenderer implements Renderer {
    public static final IndigoRenderer INSTANCE = new IndigoRenderer();

    public static final RenderMaterial MATERIAL_STANDARD = INSTANCE.materialFinder().find();

    static {
        INSTANCE.registerMaterial(RenderMaterial.MATERIAL_STANDARD, MATERIAL_STANDARD);
    }

    private final HashMap<ResourceLocation, RenderMaterial> materialMap = new HashMap<>();

    private IndigoRenderer() {}

    @Override
    public MeshBuilder meshBuilder() {
        return new MeshBuilderImpl();
    }

    @Override
    public MaterialFinder materialFinder() {
        return new MaterialFinderImpl();
    }

    @Override
    public RenderMaterial materialById(ResourceLocation id) {
        return materialMap.get(id);
    }

    @Override
    public boolean registerMaterial(ResourceLocation id, RenderMaterial material) {
        if (materialMap.containsKey(id)) return false;

        // cast to prevent acceptance of impostor implementations
        materialMap.put(id, material);
        return true;
    }
}
